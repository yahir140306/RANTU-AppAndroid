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
        val url = "${SupabaseClient.postgrestUrl}/cuartos"
        val text: String = SupabaseClient.client.get(url).body()
        println("[RoomRepository] fetched JSON: $text")

            // Configuramos el parser para tolerar claves desconocidas y coercionar valores nulos
            // a los valores por defecto de las propiedades (@Serializable data classes).
            // Esto evita errores cuando el backend devuelve "imagen_1": null en lugar de
            // omitir la clave.
            val json = Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
        val rooms = json.decodeFromString<List<Room>>(text)

        // Normalizar imageUrl: si la URL no tiene esquema, construirla usando Supabase Storage/public
        // Usamos resolvedImageUrl() para soportar campos legacy (imagen_1) y image_url
        val mapped = rooms.map { room ->
            val rawImage = room.resolvedImageUrl()
            val normalized = normalizeImageUrl(rawImage)
            // Construir una copia con la URL normalizada en el campo 'imageUrl'
            room.copy(imageUrl = normalized)
        }
        println("[RoomRepository] parsed rooms: ${mapped.size}")
        mapped
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
