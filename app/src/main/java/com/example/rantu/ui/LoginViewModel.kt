package com.example.rantu.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rantu.data.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.OTP
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _loginState = mutableStateOf<LoginState>(LoginState.Idle)
    val loginState: State<LoginState> = _loginState

    // Enviar Magic Link al correo (sin código OTP)
    fun sendMagicLink(email: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                // Enviar Magic Link (enlace mágico) al correo
                // El parámetro redirectUrl especifica el deep link para abrir la app
                SupabaseClient.client.auth.signInWith(OTP, redirectUrl = "rantu://login") {
                    this.email = email
                    // No se necesita verificación adicional, el usuario hace clic en el link del correo
                }
                _loginState.value = LoginState.EmailSent(email)
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Error al enviar el correo")
            }
        }
    }
    
    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class EmailSent(val email: String) : LoginState()
    data class Error(val message: String) : LoginState()
}
