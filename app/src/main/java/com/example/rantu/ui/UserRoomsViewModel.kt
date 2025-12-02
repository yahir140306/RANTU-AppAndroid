package com.example.rantu.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rantu.data.Room
import com.example.rantu.data.RoomInsert
import com.example.rantu.data.RoomRepository
import com.example.rantu.data.RoomUpdate
import kotlinx.coroutines.launch

class UserRoomsViewModel : ViewModel() {
    private val repository = RoomRepository()

    // Estado para la lista de cuartos del usuario
    val rooms = mutableStateOf<List<Room>>(emptyList())
    
    // Estado para saber si se están cargando los datos
    val isLoading = mutableStateOf(true)
    
    // Estado para guardar un mensaje de error
    val error = mutableStateOf<String?>(null)
    
    // Estados para estadísticas
    val totalRooms: Int
        get() = rooms.value.size
    
    val averagePrice: Double
        get() = if (rooms.value.isEmpty()) 0.0 
                else rooms.value.mapNotNull { it.price }.average()

    init {
        loadUserRooms()
    }

    fun loadUserRooms() {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                val roomList = repository.getUserRooms()
                rooms.value = roomList
                println("[UserRoomsViewModel] Loaded ${roomList.size} user rooms")
            } catch (e: Exception) {
                val message = when (e) {
                    is IllegalStateException -> e.message ?: "Error de autenticación"
                    is java.net.UnknownHostException -> "Sin conexión a Internet"
                    is java.net.SocketTimeoutException -> "Tiempo de espera agotado"
                    else -> e.message ?: "Error desconocido al cargar cuartos"
                }
                error.value = message
                rooms.value = emptyList()
                println("[UserRoomsViewModel] Error loading user rooms: $e")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deleteRoom(roomId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val success = repository.deleteRoom(roomId)
                if (success) {
                    // Eliminar de la lista local
                    rooms.value = rooms.value.filter { it.id != roomId }
                    onSuccess()
                } else {
                    onError("No se pudo eliminar el cuarto")
                }
            } catch (e: Exception) {
                val message = e.message ?: "Error eliminando cuarto"
                onError(message)
                println("[UserRoomsViewModel] Error deleting room: $e")
            }
        }
    }
}
