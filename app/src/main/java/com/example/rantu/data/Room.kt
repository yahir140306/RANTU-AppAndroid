package com.example.rantu.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// @Serializable le dice a la librería cómo convertir el JSON de Supabase a esta clase
@Serializable
data class Room(
    val id: Int,
    // Mapear nombres de columnas en español que usa la base de datos (ej. 'titulo', 'descripcion')
    @SerialName("titulo")
    val title: String = "",

    @SerialName("descripcion")
    val description: String = "",

    @SerialName("precio")
    val price: Double = 0.0,

    // Algunas tablas usan 'activo' o 'is_available' — aceptamos ambas en la lógica de la app
    @SerialName("activo")
    val isAvailable: Boolean = true,

    // Podemos recibir la URL en 'image_url' o en 'imagen_1' según el backend (legacy)
    @SerialName("image_url")
    val imageUrl: String = "",

    @SerialName("imagen_1")
    val imagen1: String = ""
) {
    // Resuelve cuál campo de imagen usar (preferir 'imageUrl' si existe)
    fun resolvedImageUrl(): String = when {
        imageUrl.isNotBlank() -> imageUrl
        imagen1.isNotBlank() -> imagen1
        else -> ""
    }
}
