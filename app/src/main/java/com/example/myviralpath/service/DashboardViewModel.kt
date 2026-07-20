package com.example.myviralpath.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myviralpath.supabase.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.client.call.body
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put
import kotlinx.serialization.json.intOrNull

// ─── Data models ─────────────────────────────────────────────────────────────

@Serializable
data class RecentVideo(
    val videoId: String = "",
    val title: String = "",
    val publishedAt: String = "",
    val thumbnailUrl: String = "",
    val views: Long = 0,
    val likes: Long = 0
)

data class YoutubeStats(
    val channelId: String = "",
    val channelTitle: String = "",
    val subscriberCount: Long = 0,
    val viewCount: Long = 0,
    val videoCount: Int = 0,
    val avgViewsPerVideo: Long = 0,
    val recentVideos: List<RecentVideo> = emptyList(),
    val topVideoTitle: String = "",
    val topVideoViews: Long = 0,
    val growthPercent: Double = 0.0,
    val fetchedAt: String = ""
)

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(val stats: YoutubeStats) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
    /** Canal no vinculado o sin datos en caché */
    object NotLinked : DashboardUiState()
}

// ─── ViewModel ────────────────────────────────────────────────────────────────

class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            try {
                // 1. Check if user is authenticated
                val user = supabase.auth.currentUserOrNull()
                if (user == null) {
                    _uiState.value = DashboardUiState.NotLinked
                    return@launch
                }

                // 2. First try to load from cache (youtube_analytics table)
                val cached = loadFromCache(user.id)
                if (cached != null) {
                    _uiState.value = DashboardUiState.Success(cached)
                    // Refresh in background if data is older than 30 min
                    val thirtyMinAgo = System.currentTimeMillis() - (30 * 60 * 1000)
                    if (cached.fetchedAt.isNotEmpty()) {
                        refreshFromEdgeFunction()
                    }
                    return@launch
                }

                // 3. No cache – fetch fresh from Edge Function
                refreshFromEdgeFunction()

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = DashboardUiState.Error(
                    e.localizedMessage ?: "Error al cargar datos de YouTube"
                )
            }
        }
    }

    private suspend fun loadFromCache(userId: String): YoutubeStats? {
        return try {
            val response = supabase.postgrest.from("youtube_analytics").select {
                filter { eq("user_id", userId) }
            }
            val arr = response.decodeAs<JsonArray>()
            if (arr.isEmpty()) return null

            val obj = arr[0].jsonObject
            YoutubeStats(
                channelId     = obj["channel_id"]?.jsonPrimitive?.content ?: "",
                channelTitle  = obj["channel_title"]?.jsonPrimitive?.content ?: "",
                subscriberCount = obj["subscriber_count"]?.jsonPrimitive?.longOrNull ?: 0L,
                viewCount       = obj["view_count"]?.jsonPrimitive?.longOrNull ?: 0L,
                videoCount      = obj["video_count"]?.jsonPrimitive?.intOrNull ?: 0,
                avgViewsPerVideo = obj["avg_views_per_video"]?.jsonPrimitive?.longOrNull ?: 0L,
                topVideoTitle   = obj["top_video_title"]?.jsonPrimitive?.content ?: "",
                topVideoViews   = obj["top_video_views"]?.jsonPrimitive?.longOrNull ?: 0L,
                growthPercent   = obj["growth_percent"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
                fetchedAt       = obj["fetched_at"]?.jsonPrimitive?.content ?: "",
                recentVideos    = parseRecentVideos(obj["recent_videos"]?.toString() ?: "[]")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseRecentVideos(jsonStr: String): List<RecentVideo> {
        return try {
            json.decodeFromString<List<RecentVideo>>(jsonStr)
        } catch (_: Exception) {
            emptyList()
        }
    }

    private suspend fun refreshFromEdgeFunction() {
        try {
            // Get Google provider_token if the user linked Google
            val user = supabase.auth.currentUserOrNull() ?: return
            val googleIdentity = user.identities?.firstOrNull { it.provider == "google" }
            val providerToken = googleIdentity?.identityData?.get("provider_token")
                ?.jsonPrimitive?.content

            // Build request body
            val bodyObj = buildJsonObject {
                if (providerToken != null) {
                    put("provider_token", providerToken)
                }
                // If no OAuth token and no API key, edge function will return error
            }

            val response = supabase.functions.invoke(
                function = "fetch-youtube-stats",
                body = bodyObj
            )

            val responseBody = response.body<String>()
            val responseJson = json.parseToJsonElement(responseBody).jsonObject

            val success = responseJson["success"]?.jsonPrimitive?.content?.toBoolean() ?: false
            if (!success) {
                val errMsg = responseJson["error"]?.jsonPrimitive?.content ?: "Error desconocido"
                // If edge function fails, show cached data or not-linked state
                val cached = loadFromCache(user.id)
                _uiState.value = if (cached != null) {
                    DashboardUiState.Success(cached)
                } else {
                    DashboardUiState.NotLinked
                }
                return
            }

            val data = responseJson["data"]?.jsonObject ?: run {
                _uiState.value = DashboardUiState.NotLinked
                return
            }

            val stats = YoutubeStats(
                channelId       = data["channelId"]?.jsonPrimitive?.content ?: "",
                channelTitle    = data["channelTitle"]?.jsonPrimitive?.content ?: "",
                subscriberCount = data["subscriberCount"]?.jsonPrimitive?.longOrNull ?: 0L,
                viewCount       = data["viewCount"]?.jsonPrimitive?.longOrNull ?: 0L,
                videoCount      = data["videoCount"]?.jsonPrimitive?.intOrNull ?: 0,
                avgViewsPerVideo = data["avgViewsPerVideo"]?.jsonPrimitive?.longOrNull ?: 0L,
                topVideoTitle   = data["topVideoTitle"]?.jsonPrimitive?.content ?: "",
                topVideoViews   = data["topVideoViews"]?.jsonPrimitive?.longOrNull ?: 0L,
                growthPercent   = data["growthPercent"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
                fetchedAt       = data["fetchedAt"]?.jsonPrimitive?.content ?: "",
                recentVideos    = parseRecentVideos(data["recentVideos"]?.toString() ?: "[]")
            )

            _uiState.value = DashboardUiState.Success(stats)

        } catch (e: Exception) {
            e.printStackTrace()
            // Don't override success state if we already have cached data
            if (_uiState.value !is DashboardUiState.Success) {
                _uiState.value = DashboardUiState.Error(
                    e.localizedMessage ?: "Error al conectar con YouTube"
                )
            }
        }
    }

    fun retry() {
        loadDashboard()
    }

    /** Formatea números grandes: 1500000 → "1.5M", 24000 → "24K" */
    companion object {
        fun formatCount(count: Long): String {
            return when {
                count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
                count >= 1_000     -> String.format("%.1fK", count / 1_000.0)
                else               -> count.toString()
            }
        }
    }
}
