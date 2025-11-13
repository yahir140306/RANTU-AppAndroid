package com.example.rantu.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rantu.data.Room
import com.example.rantu.data.RoomRepository
import kotlinx.coroutines.launch

// El ViewModel sobrevive a cambios de configuración como rotar la pantalla
class RoomViewModel : ViewModel() {
    private val repository = RoomRepository()

    // Estado para la lista de cuartos
    val rooms = mutableStateOf<List<Room>>(emptyList())
    // Estado para saber si se están cargando los datos
    val isLoading = mutableStateOf(true)
    // Estado para guardar un mensaje de error legible para el usuario
    val error = mutableStateOf<String?>(null)

    init {
        // Se llama al crear el ViewModel
        fetchRooms()
    }

    // Hacemos pública la función para que la UI pueda reintentar
    fun fetchRooms() {
        // viewModelScope es un coroutine scope ligado al ciclo de vida del ViewModel
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                val roomList = repository.getAllRooms()
                rooms.value = roomList
            } catch (e: Exception) {
                // Mapear errores comunes a mensajes legibles para el usuario
                val message = when (e) {
                    is java.net.UnknownHostException -> "Sin conexión. Revisa tu conexión a Internet."
                    is java.net.SocketTimeoutException -> "Tiempo de espera agotado al conectarse al servidor."
                    is io.ktor.client.plugins.ResponseException -> {
                        val status = e.response.status.value
                        "Error del servidor (HTTP $status). Intenta más tarde."
                    }
                    else -> e.message ?: "Error desconocido al cargar cuartos"
                }

                // Guardar mensaje y limpiar lista
                error.value = message
                rooms.value = emptyList()
                println("[RoomViewModel] Error fetching rooms: ${e}")
            } finally {
                isLoading.value = false
            }
        }
    }
}
