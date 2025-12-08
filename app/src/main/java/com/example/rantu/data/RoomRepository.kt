package com.example.rantu.data

import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
data class RoomInsert(
    val titulo: String,
    val descripcion: String,
    val precio: Double,
    val celular: String? = null,
    val caracteristicas: String? = null,
    val ubicacion: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val imagen_1: String? = null,
    val imagen_2: String? = null,
    val imagen_3: String? = null,
    val user_id: String,
    val activo: Boolean = true
)

@Serializable
data class RoomUpdate(
    val titulo: String? = null,
    val descripcion: String? = null,
    val precio: Double? = null,
    val celular: String? = null,
    val caracteristicas: String? = null,
    val ubicacion: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val imagen_1: String? = null,
    val imagen_2: String? = null,
    val imagen_3: String? = null,
    val activo: Boolean? = null
)

class RoomRepository {

    // Obtiene todos los cuartos activos
    suspend fun getAllRooms(): List<Room> = withContext(Dispatchers.IO) {
        val rooms = SupabaseClient.client.from("cuartos")
            .select()
            .decodeList<Room>()
        
        println("[RoomRepository] fetched rooms: ${rooms.size}")

        val mapped = rooms.map { room ->
            val rawImage = room.resolvedImageUrl()
            val normalized = normalizeImageUrl(rawImage)
            room.copy(imageUrl = normalized)
        }
        println("[RoomRepository] parsed rooms: ${mapped.size}")
        mapped
    }

    // Obtiene los cuartos del usuario autenticado (todos, activos e inactivos)
    suspend fun getUserRooms(): List<Room> = withContext(Dispatchers.IO) {
        val user = SupabaseClient.client.auth.currentUserOrNull()
            ?: throw IllegalStateException("Usuario no autenticado")
        
        val rooms = SupabaseClient.client.from("cuartos")
            .select {
                filter {
                    eq("user_id", user.id)
                }
            }
            .decodeList<Room>()
            .sortedByDescending { it.id }
        
        println("[RoomRepository] fetched user rooms: ${rooms.size}")
        
        rooms.map { room ->
            val rawImage = room.resolvedImageUrl()
            val normalized = normalizeImageUrl(rawImage)
            room.copy(imageUrl = normalized)
        }
    }

    // Obtiene un cuarto por ID
    suspend fun getRoomById(roomId: Int): Room? = withContext(Dispatchers.IO) {
        try {
            val room = SupabaseClient.client.from("cuartos")
                .select {
                    filter {
                        eq("id", roomId)
                    }
                }
                .decodeSingle<Room>()
            
            val rawImage = room.resolvedImageUrl()
            val normalized = normalizeImageUrl(rawImage)
            room.copy(imageUrl = normalized)
        } catch (e: Exception) {
            println("[RoomRepository] Error fetching room $roomId: ${e.message}")
            null
        }
    }

    // Crea un nuevo cuarto
    suspend fun createRoom(roomData: RoomInsert): Room = withContext(Dispatchers.IO) {
        val room = SupabaseClient.client.from("cuartos")
            .insert(roomData) {
                select()
            }
            .decodeSingle<Room>()
        
        println("[RoomRepository] created room: ${room.id}")
        room
    }

    // Actualiza un cuarto existente
    suspend fun updateRoom(roomId: Int, roomData: RoomUpdate): Room = withContext(Dispatchers.IO) {
        val user = SupabaseClient.client.auth.currentUserOrNull()
            ?: throw IllegalStateException("Usuario no autenticado")
        
        // Verificar que el cuarto pertenece al usuario
        val existingRoom = getRoomById(roomId)
            ?: throw IllegalStateException("Cuarto no encontrado")
        
        if (existingRoom.user_id != user.id) {
            throw IllegalStateException("No tienes permisos para editar este cuarto")
        }
        
        val room = SupabaseClient.client.from("cuartos")
            .update(roomData) {
                filter {
                    eq("id", roomId)
                    eq("user_id", user.id)
                }
                select()
            }
            .decodeSingle<Room>()
        
        println("[RoomRepository] updated room: ${room.id}")
        room
    }

    // Elimina un cuarto (soft delete)
    suspend fun deleteRoom(roomId: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val user = SupabaseClient.client.auth.currentUserOrNull()
                ?: throw IllegalStateException("Usuario no autenticado")
            
            // Verificar que el cuarto pertenece al usuario
            val existingRoom = getRoomById(roomId)
                ?: throw IllegalStateException("Cuarto no encontrado")
            
            if (existingRoom.user_id != user.id) {
                throw IllegalStateException("No tienes permisos para eliminar este cuarto")
            }
            
            // Soft delete: marcar como inactivo
            SupabaseClient.client.from("cuartos")
                .update(mapOf("activo" to false)) {
                    filter {
                        eq("id", roomId)
                        eq("user_id", user.id)
                    }
                }
            
            println("[RoomRepository] deleted (soft) room: $roomId")
            true
        } catch (e: Exception) {
            println("[RoomRepository] Error deleting room: ${e.message}")
            false
        }
    }
    
    // Toggle estado activo/inactivo de un cuarto
    suspend fun toggleRoomActive(roomId: Int, isActive: Boolean): Boolean = withContext(Dispatchers.IO) {
        try {
            val user = SupabaseClient.client.auth.currentUserOrNull()
                ?: throw IllegalStateException("Usuario no autenticado")
            
            // Verificar que el cuarto pertenece al usuario
            val existingRoom = getRoomById(roomId)
                ?: throw IllegalStateException("Cuarto no encontrado")
            
            if (existingRoom.user_id != user.id) {
                throw IllegalStateException("No tienes permisos para modificar este cuarto")
            }
            
            // Actualizar estado activo
            SupabaseClient.client.from("cuartos")
                .update(mapOf("activo" to isActive)) {
                    filter {
                        eq("id", roomId)
                        eq("user_id", user.id)
                    }
                }
            
            println("[RoomRepository] toggled room $roomId to active: $isActive")
            true
        } catch (e: Exception) {
            println("[RoomRepository] Error toggling room active state: ${e.message}")
            false
        }
    }

    private fun normalizeImageUrl(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return ""
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) return trimmed

        if (trimmed.startsWith('/')) {
            return "${SupabaseClient.SUPABASE_URL}$trimmed"
        }

        return "${SupabaseClient.SUPABASE_URL}/storage/v1/object/public/$trimmed"
    }
    
    // ==================== Funciones de Comentarios ====================
    
    // Obtiene comentarios de un cuarto con estadísticas
    suspend fun getComments(roomId: Int): CommentsResponse = withContext(Dispatchers.IO) {
        val comments = SupabaseClient.client.from("comentarios")
            .select {
                filter {
                    eq("cuarto_id", roomId)
                }
            }
            .decodeList<Comment>()
            .sortedByDescending { it.id }
        
        val total = comments.size
        val promedio = if (total > 0) {
            comments.map { it.calificacion }.average()
        } else {
            0.0
        }
        
        println("[RoomRepository] fetched $total comments for room $roomId, average: $promedio")
        
        CommentsResponse(
            comentarios = comments,
            estadisticas = CommentStatistics(
                total = total,
                promedio = Math.round(promedio * 10) / 10.0
            )
        )
    }
    
    // Crea un nuevo comentario
    suspend fun createComment(roomId: Int, comentario: String, calificacion: Int): Comment = withContext(Dispatchers.IO) {
        val user = SupabaseClient.client.auth.currentUserOrNull()
            ?: throw IllegalStateException("Usuario no autenticado")
        
        // Verificar si el usuario ya comentó
        val existing = SupabaseClient.client.from("comentarios")
            .select {
                filter {
                    eq("cuarto_id", roomId)
                    eq("user_id", user.id)
                }
            }
            .decodeList<Comment>()
        
        if (existing.isNotEmpty()) {
            throw IllegalStateException("Ya has comentado este cuarto")
        }
        
        val commentData = CommentInsert(
            cuartoId = roomId,
            userId = user.id,
            comentario = comentario,
            calificacion = calificacion
        )
        
        val comment = SupabaseClient.client.from("comentarios")
            .insert(commentData) {
                select()
            }
            .decodeSingle<Comment>()
        
        println("[RoomRepository] created comment: ${comment.id}")
        comment
    }
}