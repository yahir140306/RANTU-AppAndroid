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

    // Estado para la lista de cuartos original (sin filtrar)
    private val allRooms = mutableStateOf<List<Room>>(emptyList())
    
    // Estado para la lista de cuartos mostrados (puede estar filtrada)
    val rooms = mutableStateOf<List<Room>>(emptyList())
    
    // Total de cuartos (sin filtrar) para mostrar en contador
    val totalRoomsCount: Int
        get() = allRooms.value.size
    
    // Estado para saber si se están cargando los datos
    val isLoading = mutableStateOf(true)
    
    // Estado para guardar un mensaje de error legible para el usuario
    val error = mutableStateOf<String?>(null)
    
    // Estados para el filtro de precio
    val minPrice = mutableStateOf("")
    val maxPrice = mutableStateOf("")
    val soloDisponibles = mutableStateOf(true)
    val isFilterActive = mutableStateOf(false)

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
                allRooms.value = roomList
                applyFilter()
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
    
    // Aplicar el filtro de precio
    fun applyFilter() {
        val min = minPrice.value.toDoubleOrNull() ?: 0.0
        val max = maxPrice.value.toDoubleOrNull() ?: Double.MAX_VALUE
        
        rooms.value = if (minPrice.value.isEmpty() && maxPrice.value.isEmpty() && soloDisponibles.value) {
            isFilterActive.value = false
            allRooms.value
        } else {
            isFilterActive.value = true
            allRooms.value.filter { room ->
                val price = room.price ?: 0.0
                val cumplePrecio = price >= min && price <= max
                val cumpleDisponibilidad = !soloDisponibles.value || (room.isAvailable == true)
                cumplePrecio && cumpleDisponibilidad
            }
        }
    }
    
    // Limpiar el filtro
    fun clearFilter() {
        minPrice.value = ""
        maxPrice.value = ""
        soloDisponibles.value = true
        isFilterActive.value = false
        rooms.value = allRooms.value
    }
    
    // Actualizar precio mínimo
    fun updateMinPrice(value: String) {
        minPrice.value = value
    }
    
    // Actualizar precio máximo
    fun updateMaxPrice(value: String) {
        maxPrice.value = value
    }
}
