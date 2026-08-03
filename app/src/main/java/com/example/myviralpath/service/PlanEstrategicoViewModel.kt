package com.example.myviralpath.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myviralpath.data.models.NextStep
import com.example.myviralpath.data.models.StrategicPlan
import com.example.myviralpath.supabase.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonPrimitive
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class PlanEstrategicoViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _currentPlan = MutableStateFlow<StrategicPlan?>(null)
    val currentPlan: StateFlow<StrategicPlan?> = _currentPlan.asStateFlow()

    private val _nextSteps = MutableStateFlow<List<NextStep>>(emptyList())
    val nextSteps: StateFlow<List<NextStep>> = _nextSteps.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun fetchCurrentPlan() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = supabase.auth.retrieveUserForCurrentSession()
                val today = format.format(Date())

                val plan = supabase.from("strategic_plans")
                    .select {
                        filter {
                            eq("user_id", user.id)
                            eq("plan_date", today)
                        }
                    }
                    .decodeSingleOrNull<StrategicPlan>()

                _currentPlan.value = plan

                if (plan != null) {
                    val tasks = supabase.from("next_steps")
                        .select {
                            filter {
                                eq("plan_id", plan.id)
                            }
                        }
                        .decodeList<NextStep>()
                    _nextSteps.value = tasks.sortedBy { it.order_index }
                } else {
                    _nextSteps.value = emptyList()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al cargar el plan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun generateNewPlan() {
        viewModelScope.launch {
            _isGenerating.value = true
            _errorMessage.value = null
            try {
                val response = supabase.functions.invoke("generate-plan")
                val bodyText = response.bodyAsText()
                val statusCode = response.status.value

                if (statusCode != 200) {
                    val errorMsg = try {
                        val json = Json.decodeFromString<JsonObject>(bodyText)
                        json["error"]?.jsonPrimitive?.content ?: "Error del servidor ($statusCode)"
                    } catch (_: Exception) {
                        "Error del servidor ($statusCode)"
                    }
                    _errorMessage.value = "❌ $errorMsg"
                    return@launch
                }

                val jsonResponse = Json.decodeFromString<JsonObject>(bodyText)
                val success = jsonResponse["success"]?.jsonPrimitive?.booleanOrNull ?: false

                if (success) {
                    val user = supabase.auth.retrieveUserForCurrentSession()
                    val today = format.format(Date())

                    val plan = supabase.from("strategic_plans")
                        .select {
                            filter {
                                eq("user_id", user.id)
                                eq("plan_date", today)
                            }
                        }
                        .decodeSingleOrNull<StrategicPlan>()

                    _currentPlan.value = plan

                    if (plan != null) {
                        val tasks = supabase.from("next_steps")
                            .select {
                                filter { eq("plan_id", plan.id) }
                            }
                            .decodeList<NextStep>()
                        _nextSteps.value = tasks.sortedBy { it.order_index }
                    }
                } else {
                    val errorMsg = jsonResponse["error"]?.jsonPrimitive?.content ?: "Error desconocido de la IA"
                    _errorMessage.value = "❌ $errorMsg"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                val rawMsg = e.message ?: "Error desconocido"
                val cleanMsg = try {
                    // La primera línea del mensaje puede contener el cuerpo JSON de la respuesta
                    val firstLine = rawMsg.lines().firstOrNull { it.trim().startsWith("{") } ?: rawMsg.lines().first()
                    val json = Json.decodeFromString<JsonObject>(firstLine.trim())
                    val errField = json["error"]?.jsonPrimitive?.content
                    val msgField = json["message"]?.jsonPrimitive?.content
                    // Retorna el campo de error, si no existe el de mensaje, o la línea original completa
                    errField ?: msgField ?: firstLine
                } catch (_: Exception) {
                    rawMsg.lines().first()
                }
                _errorMessage.value = "❌ $cleanMsg"
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun toggleTaskCompletion(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                supabase.from("next_steps")
                    .update(
                        {
                            set("is_completed", isCompleted)
                            // Opcionalmente se podría establecer completed_at aquí
                        }
                    ) {
                        filter { eq("id", taskId) }
                    }
                
                // Actualizar el estado local (UI) sin tener que volver a consultar toda la lista de tareas
                val currentTasks = _nextSteps.value.toMutableList()
                val index = currentTasks.indexOfFirst { it.id == taskId }
                if (index != -1) {
                    currentTasks[index] = currentTasks[index].copy(is_completed = isCompleted)
                    _nextSteps.value = currentTasks
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al actualizar tarea"
            }
        }
    }
}
