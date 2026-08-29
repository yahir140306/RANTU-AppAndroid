package com.example.rantu.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rantu.data.Room
import com.example.rantu.data.RoomRepository
import kotlinx.coroutines.launch

class RoomViewModel(private val repository: RoomRepository) : ViewModel() {
    private val _rooms = mutableStateOf<List<Room>>(emptyList())
    val rooms: State<List<Room>> = _rooms

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private var currentPage = 1
    private var isLastPage = false
    private val limit = 10

    init {
        fetchRooms()
    }

    fun fetchRooms() {
        currentPage = 1
        isLastPage = false
        _rooms.value = emptyList()
        loadMore()
    }

    fun loadMore() {
        if (_isLoading.value || isLastPage) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val newRooms = repository.getAllRooms(page = currentPage, limit = limit)
                if (newRooms.size < limit) {
                    isLastPage = true
                }
                
                val currentRooms = _rooms.value.toMutableList()
                newRooms.forEach { room ->
                    if (currentRooms.none { it.id == room.id }) {
                        currentRooms.add(room)
                    }
                }
                
                _rooms.value = currentRooms
                currentPage++
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.message ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
