package com.example.myviralpath.service

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myviralpath.supabase.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch
import android.widget.Toast

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val _authState = mutableStateOf<AuthState>(AuthState.Idle)
    val authState: State<AuthState> = _authState

    fun signUp(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = pass
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
            } catch (e: Exception) {
                // Aquí personalizamos el mensaje
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
            } catch (e: Exception) {

            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
