package com.example.myviralpath.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myviralpath.sampledata.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

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
                _authState.value = AuthState.Success("Registro exitoso. Revisa tu correo.")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Error al registrarse")
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
                _authState.value = AuthState.Success("Sesión iniciada correctamente")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Error al iniciar sesión")
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
