package com.example.rantu

import android.content.Intent
import android.net.Uri
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

import com.example.rantu.Components.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Manejar deep link de Supabase automáticamente
        SupabaseClient.client.handleDeeplinks(intent)
        
        setContent {
            // Cargar preferencia de tema oscuro
            val context = androidx.compose.ui.platform.LocalContext.current
            val sharedPreferences = remember { context.getSharedPreferences("rantu_prefs", android.content.Context.MODE_PRIVATE) }
            val systemTheme = androidx.compose.foundation.isSystemInDarkTheme()
            
            var isDarkMode by remember { 
                mutableStateOf(sharedPreferences.getBoolean("dark_mode", systemTheme)) 
            }

            RANTUTheme (darkTheme = isDarkMode) {
                var isLoggedIn by remember { mutableStateOf(false) }
                var userEmail by remember { mutableStateOf<String?>(null) }
                var showLoginScreen by remember { mutableStateOf(false) }
                var showRegisterScreen by remember { mutableStateOf(false) }
                var deepLinkRoomId by remember { mutableStateOf<Int?>(null) }

                // Procesar deep links de cuartos
                LaunchedEffect(Unit) {
                    handleDeepLink(intent)?.let { roomId ->
                        deepLinkRoomId = roomId
                    }
                }

                // Observar cambios en la sesión
                LaunchedEffect(Unit) {
                    // Verificar sesión al inicio y observar cambios
                    SupabaseClient.client.auth.sessionStatus.collect { status ->
                        val session = SupabaseClient.client.auth.currentSessionOrNull()
                        isLoggedIn = session != null
                        userEmail = session?.user?.email
                        
                        // Si se autentica exitosamente, cerrar pantalla de login/registro
                        if (isLoggedIn && (showLoginScreen || showRegisterScreen)) {
                            showLoginScreen = false
                            showRegisterScreen = false
                        }
                    }
                }

                if (showLoginScreen) {
                    LoginScreen(
                        onLoginSuccess = { },
                        onBack = { showLoginScreen = false },
                        onRegisterClick = { 
                            showLoginScreen = false
                            showRegisterScreen = true 
                        }
                    )
                } else if (showRegisterScreen) {
                    RegisterScreen(
                        onRegisterSuccess = { },
                        onBack = { showRegisterScreen = false },
                        onLoginClick = { 
                            showRegisterScreen = false
                            showLoginScreen = true 
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
                        },
                        deepLinkRoomId = deepLinkRoomId,
                        onDeepLinkHandled = {
                            deepLinkRoomId = null
                        },
                        isDarkMode = isDarkMode,
                        onThemeToggle = {
                            isDarkMode = !isDarkMode
                            sharedPreferences.edit().putBoolean("dark_mode", isDarkMode).apply()
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
        
        // Manejar deep links de cuartos cuando la app está abierta
        handleDeepLink(intent)?.let { roomId ->
            // Aquí podrías emitir un evento o usar un estado compartido
            // Por ahora, recreamos la actividad para procesarlo
            recreate()
        }
    }
    
    private fun handleDeepLink(intent: Intent?): Int? {
        val data: Uri? = intent?.data
        
        return when {
            // Formato: https://prototype-delta-vert.vercel.app/cuarto/123
            data?.host == "prototype-delta-vert.vercel.app" && 
            data.pathSegments.getOrNull(0) == "cuarto" -> {
                data.pathSegments.getOrNull(1)?.toIntOrNull()
            }
            
            // Formato: http://localhost:4321/cuarto/123
            data?.host == "localhost" && 
            data.pathSegments.getOrNull(0) == "cuarto" -> {
                data.pathSegments.getOrNull(1)?.toIntOrNull()
            }
            
            // Formato: rantu://cuarto/123
            data?.scheme == "rantu" && data.host == "cuarto" -> {
                data.pathSegments.firstOrNull()?.toIntOrNull()
            }
            
            else -> null
        }
    }
}