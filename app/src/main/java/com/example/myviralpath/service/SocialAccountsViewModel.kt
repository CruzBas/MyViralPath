package com.example.myviralpath.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myviralpath.supabase.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Facebook
import io.github.jan.supabase.auth.providers.Google
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import io.github.jan.supabase.auth.status.SessionStatus

class SocialAccountsViewModel : ViewModel() {
    private val _isInstagramLinked = MutableStateFlow(false)
    val isInstagramLinked: StateFlow<Boolean> = _isInstagramLinked.asStateFlow()

    private val _isYoutubeLinked = MutableStateFlow(false)
    val isYoutubeLinked: StateFlow<Boolean> = _isYoutubeLinked.asStateFlow()

    init {
        // Observamos el estado de la sesión para actualizar las vinculaciones reales
        viewModelScope.launch {
            supabase.auth.sessionStatus.collect { status ->
                if (status is SessionStatus.Authenticated) {
                    val user = supabase.auth.currentUserOrNull()
                    val identities = user?.identities ?: emptyList()
                    
                    _isYoutubeLinked.value = identities.any { it.provider == "google" }
                    // Usualmente Instagram se enlaza a través de Facebook Login o el provider "instagram"
                    _isInstagramLinked.value = identities.any { it.provider == "facebook" || it.provider == "instagram" }
                } else {
                    _isYoutubeLinked.value = false
                    _isInstagramLinked.value = false
                }
            }
        }
    }

    fun linkInstagram() {
        viewModelScope.launch {
            try {
                // Iniciamos flujo OAuth de Supabase para vincular identidad
                supabase.auth.linkIdentity(provider = Facebook, redirectUrl = "myviralpath://login-callback")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun linkYoutube() {
        viewModelScope.launch {
            try {
                supabase.auth.linkIdentity(provider = Google, redirectUrl = "myviralpath://login-callback")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
