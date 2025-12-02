package com.example.rantu

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.example.rantu.Components.LoginScreen
import com.example.rantu.Components.ViewFist
import com.example.rantu.data.SupabaseClient
import com.example.rantu.ui.theme.RANTUTheme
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.handleDeeplinks
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Manejar deep link de Supabase automáticamente
        SupabaseClient.client.handleDeeplinks(intent)
        
        setContent {
            RANTUTheme (darkTheme = false) {
                var isLoggedIn by remember { mutableStateOf(false) }
                var userEmail by remember { mutableStateOf<String?>(null) }
                var showLoginScreen by remember { mutableStateOf(false) }

                // Observar cambios en la sesión
                LaunchedEffect(Unit) {
                    // Verificar sesión al inicio y observar cambios
                    SupabaseClient.client.auth.sessionStatus.collect { status ->
                        val session = SupabaseClient.client.auth.currentSessionOrNull()
                        isLoggedIn = session != null
                        userEmail = session?.user?.email
                        
                        // Si se autentica exitosamente, cerrar pantalla de login
                        if (isLoggedIn && showLoginScreen) {
                            showLoginScreen = false
                        }
                    }
                }

                if (showLoginScreen) {
                    LoginScreen(
                        onLoginSuccess = {
                            // No hacemos nada aquí, el LaunchedEffect detectará el cambio de sesión
                        },
                        onBack = {
                            showLoginScreen = false
                        }
                    )
                } else {
                    ViewFist(
                        isLoggedIn = isLoggedIn,
                        userEmail = userEmail,
                        onLoginClick = {
                            showLoginScreen = true
                        },
                        onLogoutClick = {
                            lifecycleScope.launch {
                                SupabaseClient.client.auth.signOut()
                                isLoggedIn = false
                                userEmail = null
                            }
                        }
                    )
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // Manejar deep link cuando la app ya está abierta
        SupabaseClient.client.handleDeeplinks(intent)
    }
}