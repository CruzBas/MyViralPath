package com.example.myviralpath.service

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myviralpath.supabase.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.put

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val _authState = mutableStateOf<AuthState>(AuthState.Idle)
    val authState: State<AuthState> = _authState

    private val _onboardingCompleted = mutableStateOf<Boolean?>(null)
    val onboardingCompleted: State<Boolean?> = _onboardingCompleted

    fun checkOnboardingStatus() {
        viewModelScope.launch {
            try {
                val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
                val response = supabase.postgrest.from("profiles").select {
                    filter {
                        eq("id", userId)
                    }
                }
                val jsonArray = response.decodeAs<JsonArray>()
                if (jsonArray.isNotEmpty()) {
                    val profileObj = jsonArray[0].jsonObject
                    val completed = profileObj["onboarding_completed"]?.jsonPrimitive?.booleanOrNull ?: false
                    _onboardingCompleted.value = completed
                } else {
                    _onboardingCompleted.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _onboardingCompleted.value = false
            }
        }
    }

    fun signUp(email: String, pass: String, fullName: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = pass
                    data = buildJsonObject {
                        put("full_name", fullName)
                    }
                }

                _authState.value =
                    AuthState.Success("Registro exitoso. Revisa tu correo.")

            } catch (e: Exception) {
                _authState.value =
                    AuthState.Error(e.localizedMessage ?: "Error al registrarse")
            }
        }
    }

    fun signIn(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                supabase.auth.signInWith(Email) {
                    this.email = email
                    this.password = pass
                }
                _authState.value = AuthState.Success("¡Bienvenido de nuevo!")
                // Fetch onboarding status on login
                checkOnboardingStatus()
            } catch (e: Exception) {
                val notice = when {
                    e.message?.contains("invalid", ignoreCase = true) == true ->
                        "Correo o contraseña incorrectos. Inténtalo de nuevo."
                    e.message?.contains("network", ignoreCase = true) == true ->
                        "Parece que no tienes conexión a internet."
                    else -> "Hubo un problema al iniciar sesión. Inténtalo más tarde."
                }
                _authState.value = AuthState.Error(notice)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                supabase.auth.signOut()
                _onboardingCompleted.value = null
            } catch (e: Exception) {

            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun saveOnboardingData(
        niche: String,
        platforms: List<String>,
        countryName: String,
        ageMin: Int,
        ageMax: Int,
        gender: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val userId = supabase.auth.currentUserOrNull()?.id ?: throw Exception("Usuario no autenticado")

                val dbNiche = when (niche) {
                    "Educación" -> "education"
                    "Fitness" -> "fitness"
                    "Negocios" -> "business"
                    "Tecnología" -> "technology"
                    "Gaming" -> "gaming"
                    "Lifestyle" -> "lifestyle"
                    "Entretenimiento" -> "entertainment"
                    else -> "other"
                }

                val dbPlatforms = platforms.map {
                    when (it) {
                        "Instagram" -> "instagram"
                        "TikTok" -> "tiktok"
                        "YouTube" -> "youtube"
                        "Shorts" -> "shorts"
                        "Reels" -> "reels"
                        else -> it.lowercase()
                    }
                }

                val dbGender = when (gender) {
                    "MIXTO" -> "mixed"
                    "FEMENINO" -> "female"
                    "MASCULINO" -> "male"
                    else -> "mixed"
                }

                // Guardar nicho
                supabase.postgrest.from("user_niches").insert(
                    buildJsonObject {
                        put("user_id", userId)
                        put("niche", dbNiche)
                        if (dbNiche == "other") {
                            put("custom_niche", niche)
                        }
                    }
                )

                // Guardar plataformas
                dbPlatforms.forEach { platform ->
                    supabase.postgrest.from("user_platforms").insert(
                        buildJsonObject {
                            put("user_id", userId)
                            put("platform", platform)
                        }
                    )
                }

                // Guardar audiencia objetivo
                supabase.postgrest.from("audience_targets").insert(
                    buildJsonObject {
                        put("user_id", userId)
                        put("country_name", countryName)
                        put("is_global", countryName == "Global (EN)" || countryName.isEmpty())
                        put("age_min", ageMin)
                        put("age_max", ageMax)
                        put("gender", dbGender)
                    }
                )

                // Actualizar perfil
                supabase.postgrest.from("profiles").update(
                    buildJsonObject {
                        put("onboarding_completed", true)
                    }
                ) {
                    filter {
                        eq("id", userId)
                    }
                }

                _onboardingCompleted.value = true
                _authState.value = AuthState.Success("Onboarding completado")
            } catch (e: Exception) {
                e.printStackTrace()
                _authState.value = AuthState.Error(e.localizedMessage ?: "Error al guardar configuración")
            }
        }
    }
}
