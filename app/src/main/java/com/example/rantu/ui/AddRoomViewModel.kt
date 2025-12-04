package com.example.rantu.ui

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rantu.data.RoomInsert
import com.example.rantu.data.RoomRepository
import com.example.rantu.data.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class AddRoomViewModel : ViewModel() {
    private val repository = RoomRepository()

    // Estados del formulario
    val titulo = mutableStateOf("")
    val descripcion = mutableStateOf("")
    val precio = mutableStateOf("")
    val celular = mutableStateOf("")
    val caracteristicas = mutableStateOf("")
    val ubicacion = mutableStateOf("")
    
    // Estados de las imágenes
    val imagen1Uri = mutableStateOf<Uri?>(null)
    val imagen2Uri = mutableStateOf<Uri?>(null)
    val imagen3Uri = mutableStateOf<Uri?>(null)
    
    // Estados de UI
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)
    val success = mutableStateOf(false)
    
    // Estados de progreso de subida
    val uploadProgress = mutableStateOf("")
    
    // Validaciones
    fun validateForm(): String? {
        return when {
            titulo.value.isBlank() -> "El título es requerido"
            descripcion.value.isBlank() -> "La descripción es requerida"
            precio.value.isBlank() -> "El precio es requerido"
            precio.value.toDoubleOrNull() == null -> "El precio debe ser un número válido"
            precio.value.toDouble() <= 0 -> "El precio debe ser mayor a 0"
            celular.value.isBlank() -> "El número de celular es requerido"
            celular.value.length < 10 -> "El número de celular debe tener al menos 10 dígitos"
            caracteristicas.value.isBlank() -> "Las características son requeridas"
            caracteristicas.value.length < 20 -> "Las características deben tener al menos 20 caracteres"
            ubicacion.value.isBlank() -> "La ubicación es requerida"
            ubicacion.value.length < 10 -> "La ubicación debe tener al menos 10 caracteres"
            imagen1Uri.value == null -> "Debes subir al menos una imagen"
            else -> null
        }
    }
    
    // Subir imagen a Supabase Storage
    private suspend fun uploadImage(context: Context, uri: Uri, imageName: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                // Leer el contenido de la imagen
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: return@withContext null
                
                val bytes = inputStream.readBytes()
                inputStream.close()
                
                // Validar tamaño (5MB max)
                val maxSize = 5 * 1024 * 1024
                if (bytes.size > maxSize) {
                    throw IllegalStateException("La imagen $imageName es muy grande. Máximo 5MB")
                }
                
                // Generar nombre único
                val timestamp = System.currentTimeMillis()
                val randomStr = (1..6).map { ('a'..'z').random() }.joinToString("")
                val extension = when {
                    uri.toString().contains(".jpg", ignoreCase = true) -> "jpg"
                    uri.toString().contains(".jpeg", ignoreCase = true) -> "jpeg"
                    uri.toString().contains(".png", ignoreCase = true) -> "png"
                    else -> "jpg"
                }
                val fileName = "${imageName}_${timestamp}_${randomStr}.$extension"
                
                // Subir a Supabase Storage
                val bucket = SupabaseClient.client.storage.from("cuartos-images")
                bucket.upload(fileName, bytes)
                
                // Obtener URL pública
                val publicUrl = bucket.publicUrl(fileName)
                
                println("[AddRoomViewModel] Uploaded $imageName: $publicUrl")
                publicUrl
            } catch (e: Exception) {
                println("[AddRoomViewModel] Error uploading $imageName: ${e.message}")
                throw e
            }
        }
    }
    
    // Crear cuarto
    fun createRoom(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            uploadProgress.value = ""
            
            try {
                // Validar formulario
                val validationError = validateForm()
                if (validationError != null) {
                    error.value = validationError
                    isLoading.value = false
                    return@launch
                }
                
                // Obtener usuario actual
                val user = SupabaseClient.client.auth.currentUserOrNull()
                    ?: throw IllegalStateException("Usuario no autenticado")
                
                // Subir imágenes
                uploadProgress.value = "Subiendo imagen principal..."
                val imagen1Url = imagen1Uri.value?.let { uploadImage(context, it, "cuarto_1") }
                
                var imagen2Url: String? = null
                if (imagen2Uri.value != null) {
                    uploadProgress.value = "Subiendo imagen 2..."
                    imagen2Url = uploadImage(context, imagen2Uri.value!!, "cuarto_2")
                }
                
                var imagen3Url: String? = null
                if (imagen3Uri.value != null) {
                    uploadProgress.value = "Subiendo imagen 3..."
                    imagen3Url = uploadImage(context, imagen3Uri.value!!, "cuarto_3")
                }
                
                // Crear cuarto
                uploadProgress.value = "Guardando cuarto..."
                val roomData = RoomInsert(
                    titulo = titulo.value.trim(),
                    descripcion = descripcion.value.trim(),
                    precio = precio.value.toDouble(),
                    celular = celular.value.trim(),
                    caracteristicas = caracteristicas.value.trim(),
                    ubicacion = ubicacion.value.trim(),
                    imagen_1 = imagen1Url,
                    imagen_2 = imagen2Url,
                    imagen_3 = imagen3Url,
                    user_id = user.id,
                    activo = true
                )
                
                repository.createRoom(roomData)
                
                success.value = true
                uploadProgress.value = "¡Cuarto creado exitosamente!"
                
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
                
            } catch (e: Exception) {
                val message = when (e) {
                    is IllegalStateException -> e.message ?: "Error de autenticación"
                    is java.net.UnknownHostException -> "Sin conexión a Internet"
                    is java.net.SocketTimeoutException -> "Tiempo de espera agotado"
                    else -> e.message ?: "Error desconocido al crear cuarto"
                }
                error.value = message
                uploadProgress.value = ""
                println("[AddRoomViewModel] Error creating room: $e")
            } finally {
                isLoading.value = false
            }
        }
    }
    
    fun clearForm() {
        titulo.value = ""
        descripcion.value = ""
        precio.value = ""
        celular.value = ""
        caracteristicas.value = ""
        ubicacion.value = ""
        imagen1Uri.value = null
        imagen2Uri.value = null
        imagen3Uri.value = null
        error.value = null
        success.value = false
        uploadProgress.value = ""
    }
}
