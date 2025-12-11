package com.example.rantu.data

import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class RoomRepository {

    // Obtiene todos los cuartos desde la tabla `cuartos` de PostgREST
    suspend fun getAllRooms(): List<Room> = withContext(Dispatchers.IO) {
        try {
            val url = "${SupabaseClient.postgrestUrl}/cuartos"
            val text: String = SupabaseClient.client.get(url).body()

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
