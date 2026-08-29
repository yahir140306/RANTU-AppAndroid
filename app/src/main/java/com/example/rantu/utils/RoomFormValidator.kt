package com.example.rantu.utils

import android.net.Uri

object RoomFormValidator {
    fun validateForm(
        titulo: String,
        descripcion: String,
        precio: String,
        celular: String,
        caracteristicas: String,
        ubicacion: String,
        hasImages: Boolean
    ): String? {
        return when {
            titulo.isBlank() -> "El título es requerido"
            descripcion.isBlank() -> "La descripción es requerida"
            precio.isBlank() -> "El precio es requerido"
            precio.toDoubleOrNull() == null -> "El precio debe ser un número válido"
            precio.toDouble() <= 0 -> "El precio debe ser mayor a 0"
            celular.isBlank() -> "El número de celular es requerido"
            celular.length < 10 -> "El número de celular debe tener al menos 10 dígitos"
            caracteristicas.isBlank() -> "Las características son requeridas"
            caracteristicas.length < 20 -> "Las características deben tener al menos 20 caracteres"
            ubicacion.isBlank() -> "La ubicación es requerida"
            ubicacion.length < 10 -> "La ubicación debe tener al menos 10 caracteres"
            !hasImages -> "Debes proporcionar al menos una imagen"
            else -> null
        }
    }
}
