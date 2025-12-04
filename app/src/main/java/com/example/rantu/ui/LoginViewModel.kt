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

    /**
     * Enviar Magic Link al correo (igual que la versión web)
     * El usuario recibirá un correo con un enlace que lo autenticará automáticamente
     */
    fun sendMagicLink(email: String) {
        if (email.isBlank()) {
            _loginState.value = LoginState.Error("El correo electrónico es requerido")
            return
        }
        
        // Validar formato de email
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        if (!email.matches(emailRegex)) {
            _loginState.value = LoginState.Error("Por favor ingresa un correo electrónico válido")
            return
        }
        
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                println("[LoginViewModel] Enviando Magic Link a: $email")
                
                // Enviar Magic Link (OTP por email) - igual que la web
                // El redirectUrl debe coincidir con el esquema configurado en AndroidManifest.xml
                SupabaseClient.client.auth.signInWith(OTP) {
                    this.email = email
                    // Este es el deep link al que redirigirá después de hacer clic en el email
                    // Debe coincidir con android:scheme en el manifest
                    this.createUser = true // Crear usuario si no existe (como registro automático)
                }
                
                println("[LoginViewModel] ✅ Magic Link enviado exitosamente")
                _loginState.value = LoginState.EmailSent(email)
                
            } catch (e: Exception) {
                println("[LoginViewModel] ❌ Error al enviar Magic Link: ${e.message}")
                e.printStackTrace()
                
                val errorMessage = when {
                    e.message?.contains("network", ignoreCase = true) == true -> 
                        "Error de conexión. Verifica tu internet."
                    e.message?.contains("timeout", ignoreCase = true) == true -> 
                        "Tiempo de espera agotado. Intenta de nuevo."
                    e.message?.contains("invalid", ignoreCase = true) == true -> 
                        "Correo electrónico inválido."
                    else -> 
                        "No se pudo enviar el correo. Por favor intenta de nuevo."
                }
                
                _loginState.value = LoginState.Error(errorMessage)
            }
        }
    }
    
    /**
     * Resetear el estado para permitir enviar a otro correo
     */
    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}

/**
 * Estados posibles del proceso de login
 * Replicando el comportamiento de la versión web
 */
sealed class LoginState {
    object Idle : LoginState()  // Estado inicial
    object Loading : LoginState()  // Enviando el correo
    data class EmailSent(val email: String) : LoginState()  // Correo enviado exitosamente
    data class Error(val message: String) : LoginState()  // Error en el proceso
}
