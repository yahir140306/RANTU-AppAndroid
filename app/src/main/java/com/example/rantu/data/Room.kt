package com.example.rantu.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// @Serializable le dice a la librería cómo convertir el JSON de Supabase a esta clase
@Serializable
data class Room(
    val id: Int,
    // Mapear nombres de columnas en español que usa la base de datos (ej. 'titulo', 'descripcion')
    @SerialName("titulo")
    val title: String? = "",

    @SerialName("descripcion")
    val description: String? = "",

    @SerialName("precio")
    val price: Double? = 0.0,
    
    @SerialName("user_id")
    val user_id: String? = null,
    
    @SerialName("celular")
    val celular: String? = null,
    
    @SerialName("caracteristicas")
    val caracteristicas: String? = null,
    
    @SerialName("ubicacion")
    val ubicacion: String? = null,

    @SerialName("latitud")
    val latitud: Double? = null,
    
    @SerialName("longitud")
    val longitud: Double? = null,

    // Algunas tablas usan 'activo' o 'is_available' — aceptamos ambas en la lógica de la app
    @SerialName("activo")
    val isAvailable: Boolean? = true,

    // Podemos recibir la URL en 'image_url' o en 'imagen_1' según el backend (legacy)
    @SerialName("image_url")
    val imageUrl: String? = null,

    @SerialName("imagen_1")
    val imagen1: String? = null,
    
    @SerialName("imagen_2")
    val imagen2: String? = null,
    
    @SerialName("imagen_3")
    val imagen3: String? = null,
    
    @SerialName("imagen_principal")
    val imagenPrincipal: String? = null
) {
    // Resuelve cuál campo de imagen usar (preferir 'imageUrl' si existe)
    fun resolvedImageUrl(): String {
        return when {
            !imageUrl.isNullOrBlank() -> imageUrl
            !imagenPrincipal.isNullOrBlank() -> imagenPrincipal
            !imagen1.isNullOrBlank() -> imagen1
            !imagen2.isNullOrBlank() -> imagen2
            !imagen3.isNullOrBlank() -> imagen3
            else -> ""
        }
    }
}
