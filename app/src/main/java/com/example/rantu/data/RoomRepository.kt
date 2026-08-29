package com.example.rantu.data

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class RoomRepository {

    // Obtiene todos los cuartos desde la tabla `cuartos` de PostgREST
    suspend fun getAllRooms(page: Int = 1, limit: Int = 10): List<Room> = withContext(Dispatchers.IO) {
        try {
            val offset = (page - 1) * limit
            val url = "${SupabaseClient.postgrestUrl}/cuartos?activo=eq.true&order=created_at.desc"
            
            // PostgREST Range header for pagination: Range: 0-9
            val text: String = SupabaseClient.client.get(url) {
                headers {
                    append("Range", "$offset-${offset + limit - 1}")
                }
            }.body()

            val json = Json { ignoreUnknownKeys = true }
            val rooms = json.decodeFromString<List<Room>>(text)

            // Normalizar imageUrl: si la URL no tiene esquema, construirla usando Supabase Storage/public
            rooms.map { room ->
                val normalized = normalizeImageUrl(room.imageUrl)
                room.copy(imageUrl = normalized)
            }
        } catch (e: Exception) {
            println("Error al obtener cuartos: ${e.message}")
            emptyList()
        }
    }

    private fun normalizeImageUrl(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return ""
        // Si ya es una URL completa, devolverla
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) return trimmed

        // Si empieza con '/', puede ser un path relativo al host
        if (trimmed.startsWith('/')) {
            return "${SupabaseClient.SUPABASE_URL}$trimmed"
        }

        // Caso común: se almacena 'bucket/path/to/file.jpg' o solo 'path/to/file.jpg'
        // Intentamos formar la URL pública de Supabase Storage
        return "${SupabaseClient.SUPABASE_URL}/storage/v1/object/public/$trimmed"
    }
}
