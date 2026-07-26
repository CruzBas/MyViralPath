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
import io.github.jan.supabase.annotations.SupabaseInternal

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

@Serializable
data class FetchStatsRequest(
    @SerialName("provider_token") val providerToken: String? = null,
    @SerialName("channel_id") val channelId: String? = null
)

// ─── ViewModel ────────────────────────────────────────────────────────────────

class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    init {
        viewModelScope.launch {
            supabase.auth.sessionStatus.collect { status ->
                if (status is io.github.jan.supabase.auth.status.SessionStatus.Authenticated) {
                    loadDashboard()
                } else if (status is io.github.jan.supabase.auth.status.SessionStatus.NotAuthenticated) {
                    _uiState.value = DashboardUiState.NotLinked
                }
            }
        }
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            try {
                val user = supabase.auth.currentUserOrNull()
                if (user == null) {
                    _uiState.value = DashboardUiState.NotLinked
                    return@launch
                }

                // Intentar cargar de caché primero para dar respuesta inmediata
                val cached = loadFromCache(user.id)
                if (cached != null) {
                    _uiState.value = DashboardUiState.Success(cached)
                    // Refrescar en segundo plano si hay caché
                    refreshFromEdgeFunction()
                    return@launch
                }

                // Si no hay caché, refrescar obligatoriamente
                refreshFromEdgeFunction()

            } catch (e: Exception) {
                e.printStackTrace()
                handleLoadError(e)
            }
        }
    }

    private fun handleLoadError(e: Exception) {
        val message = e.localizedMessage ?: "Error desconocido"
        when {
            message.contains("permisos", ignoreCase = true) || 
            message.contains("sincronizar", ignoreCase = true) ||
            message.contains("provider_token", ignoreCase = true) ||
            message.contains("401") || message.contains("403") -> {
                _uiState.value = DashboardUiState.Error("Tu sesión no tiene permisos de YouTube. Pulsa 'Reintentar' para sincronizar con Google.")
            }
            message.contains("rate_limit", ignoreCase = true) -> {
                _uiState.value = DashboardUiState.Error("Demasiados intentos. Espera un momento y reintenta.")
            }
            else -> {
                _uiState.value = DashboardUiState.Error("Error al conectar con YouTube. Verifica tu conexión e inténtalo de nuevo.")
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

    private suspend fun refreshFromEdgeFunction(cachedChannelId: String? = null) {
        try {
            val session = supabase.auth.currentSessionOrNull()
            val user = session?.user ?: supabase.auth.retrieveUserForCurrentSession()
            val googleIdentity = user.identities?.firstOrNull { it.provider == "google" }

            // 2. Try to find the provider token
            val providerToken = session?.providerToken
                ?: googleIdentity?.identityData?.get("provider_token")?.jsonPrimitive?.content
                ?: googleIdentity?.identityData?.get("access_token")?.jsonPrimitive?.content

            // 3. Validation: If we have NO token and NO cached channel, we need Google Auth
            if (providerToken == null && cachedChannelId.isNullOrEmpty()) {
                _uiState.value = DashboardUiState.Error(
                    "Tu sesión actual no tiene permisos de YouTube. Por favor, pulsa 'Reintentar' para sincronizar con Google."
                )
                return
            }

            // Build request body using data class to ensure correct serialization and headers
            val requestBody = FetchStatsRequest(
                providerToken = providerToken,
                channelId = cachedChannelId
            )

            @OptIn(SupabaseInternal::class)
            val response = supabase.functions.invoke(
                function = "fetch-youtube-stats",
                body = requestBody
            )

            val responseBody = response.body<String>()
            println("YouTube Debug Response: $responseBody") // Log para depuración
            
            val responseJson = try {
                json.parseToJsonElement(responseBody).jsonObject
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error("Respuesta inválida del servidor. Reintenta en unos segundos.")
                return
            }

            val success = responseJson["success"]?.jsonPrimitive?.content?.toBoolean() ?: false
            if (!success) {
                val errMsg = responseJson["error"]?.jsonPrimitive?.content ?: "Error desconocido en el servidor"
                
                // Si falla la función, mostramos el error específico del servidor si es posible
                if (errMsg.contains("token") || errMsg.contains("auth")) {
                    _uiState.value = DashboardUiState.Error("Sesión de Google expirada. Pulsa 'Reintentar' para sincronizar.")
                } else {
                    _uiState.value = DashboardUiState.Error("YouTube dice: $errMsg")
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
            val errorMsg = e.localizedMessage ?: "Error al conectar con YouTube"
            
            // Parse exception to avoid ugly raw strings
            var cleanMsg = if (errorMsg.contains("\"error\":")) {
                try {
                    // try to extract the inner JSON error if present in the exception string
                    val jsonPart = errorMsg.substringBefore("}\nURL:").plus("}")
                    val parsed = json.parseToJsonElement(jsonPart).jsonObject
                    parsed["error"]?.jsonPrimitive?.content ?: "Error al conectar con YouTube"
                } catch (ex: Exception) {
                    "Error al cargar datos de YouTube"
                }
            } else {
                "Error de conexión con YouTube"
            }

            val isAuthError = cleanMsg.contains("token", ignoreCase = true) || 
                cleanMsg.contains("auth", ignoreCase = true) ||
                cleanMsg.contains("credentials", ignoreCase = true) ||
                errorMsg.contains("400") || 
                errorMsg.contains("401") ||
                errorMsg.contains("403")

            if (isAuthError) {
                cleanMsg = "Sesión de Google expirada o sin permisos. Pulsa 'Reintentar' para sincronizar con Google."
            }

            // Override success state ONLY if it's an auth error, so the user can re-authenticate
            if (_uiState.value !is DashboardUiState.Success || isAuthError) {
                _uiState.value = DashboardUiState.Error(cleanMsg)
            }
        }
    }

    fun retry() {
        loadDashboard()
    }
    
    fun signInWithGoogle() {
        viewModelScope.launch {
            try {
                supabase.auth.signInWith(io.github.jan.supabase.auth.providers.Google) {
                    scopes.add("https://www.googleapis.com/auth/youtube.readonly")
                    queryParams["prompt"] = "consent"
                    queryParams["access_type"] = "offline"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = DashboardUiState.Error("Error al iniciar Google Sign-In")
            }
        }
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
