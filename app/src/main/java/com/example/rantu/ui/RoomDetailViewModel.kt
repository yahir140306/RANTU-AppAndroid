package com.example.rantu.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rantu.data.Comment
import com.example.rantu.data.CommentStatistics
import com.example.rantu.data.RoomRepository
import kotlinx.coroutines.launch

class RoomDetailViewModel : ViewModel() {
    private val repository = RoomRepository()

    val comments = mutableStateOf<List<Comment>>(emptyList())
    val statistics = mutableStateOf<CommentStatistics?>(null)
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    fun loadComments(roomId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                val response = repository.getComments(roomId)
                comments.value = response.comentarios
                statistics.value = response.estadisticas
                println("[RoomDetailViewModel] Loaded ${response.comentarios.size} comments")
            } catch (e: Exception) {
                val message = e.message ?: "Error cargando comentarios"
                error.value = message
                println("[RoomDetailViewModel] Error loading comments: $e")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun addComment(
        roomId: Int,
        comentario: String,
        calificacion: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val newComment = repository.createComment(roomId, comentario, calificacion)
                // Recargar comentarios
                loadComments(roomId)
                onSuccess()
                println("[RoomDetailViewModel] Comment added successfully")
            } catch (e: Exception) {
                val message = when {
                    e.message?.contains("Ya has comentado") == true -> "Ya has comentado este cuarto"
                    e.message?.contains("no autenticado") == true -> "Debes iniciar sesión para comentar"
                    else -> e.message ?: "Error al publicar comentario"
                }
                onError(message)
                println("[RoomDetailViewModel] Error adding comment: $e")
            }
        }
    }
}
