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

class SocialAccountsViewModel : ViewModel() {
    private val _isInstagramLinked = MutableStateFlow(false)
    val isInstagramLinked: StateFlow<Boolean> = _isInstagramLinked.asStateFlow()

    private val _isYoutubeLinked = MutableStateFlow(false)
    val isYoutubeLinked: StateFlow<Boolean> = _isYoutubeLinked.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()




    fun updateLinkedAccounts() {
        viewModelScope.launch {
            try {

                val user = supabase.auth.retrieveUserForCurrentSession()
                val identities = user.identities
                
                if (identities != null) {
                    _isYoutubeLinked.value = identities.any { it.provider == "google" }
                    _isInstagramLinked.value = identities.any { it.provider == "facebook" || it.provider == "instagram" }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun linkInstagram() {
        executeLink {
            supabase.auth.linkIdentity(
                provider = Facebook,
                config = {
                    scopes.add("pages_show_list")
                    scopes.add("pages_read_engagement")
                    scopes.add("instagram_basic")
                    scopes.add("instagram_manage_insights")
                    queryParams["prompt"] = "select_account"
                }
            )
        }
    }

    fun linkYoutube() {
        executeLink {
            supabase.auth.linkIdentity(
                provider = Google,
                config = {
                    scopes.add("https://www.googleapis.com/auth/youtube.readonly")
                    queryParams["prompt"] = "consent"
                    queryParams["access_type"] = "offline"
                }
            )
        }
    }

    private fun executeLink(block: suspend () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                block()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
