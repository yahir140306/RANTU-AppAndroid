package com.example.rantu.ui

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rantu.data.Room
import com.example.rantu.data.RoomRepository
import com.example.rantu.data.RoomUpdate
import com.example.rantu.data.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditRoomViewModel : ViewModel() {
    private val repository = RoomRepository()

    // Estados del formulario
    val titulo = mutableStateOf("")
    val descripcion = mutableStateOf("")
    val precio = mutableStateOf("")
    val celular = mutableStateOf("")
    val caracteristicas = mutableStateOf("")
    val ubicacion = mutableStateOf("")
    val disponible = mutableStateOf(true)
    
    // Estados de ubicación
    val latitud = mutableStateOf<Double?>(null)
    val longitud = mutableStateOf<Double?>(null)
    
    // URLs de imágenes existentes
    val imagen1UrlExisting = mutableStateOf<String?>(null)
    val imagen2UrlExisting = mutableStateOf<String?>(null)
    val imagen3UrlExisting = mutableStateOf<String?>(null)
    
    // URIs de nuevas imágenes (si se seleccionan)
    val imagen1UriNew = mutableStateOf<Uri?>(null)
    val imagen2UriNew = mutableStateOf<Uri?>(null)
    val imagen3UriNew = mutableStateOf<Uri?>(null)
    
    // Estados de UI
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)
    val success = mutableStateOf(false)
    val uploadProgress = mutableStateOf("")
    
    // Cuarto actual
    val currentRoom = mutableStateOf<Room?>(null)
    
    // Cargar datos del cuarto
    fun loadRoom(roomId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            
            try {
                val room = repository.getRoomById(roomId)
                if (room == null) {
                    error.value = "Cuarto no encontrado"
                    return@launch
                }
                
                currentRoom.value = room
                
                // Poblar formulario
                titulo.value = room.title ?: ""
                descripcion.value = room.description ?: ""
                precio.value = room.price?.toString() ?: ""
                celular.value = room.celular ?: ""
                caracteristicas.value = room.caracteristicas ?: ""
                ubicacion.value = room.ubicacion ?: ""
                // Asegurar que disponible se sincroniza correctamente con activo
                disponible.value = room.isAvailable ?: true
                latitud.value = room.latitud
                longitud.value = room.longitud
                
                println("[EditRoomViewModel] Loaded room - activo: ${room.isAvailable}, disponible: ${disponible.value}")
                
                // URLs de imágenes existentes
                imagen1UrlExisting.value = room.imagen1
                imagen2UrlExisting.value = room.imagen2
                imagen3UrlExisting.value = room.imagen3
                
            } catch (e: Exception) {
                error.value = e.message ?: "Error cargando cuarto"
                println("[EditRoomViewModel] Error loading room: $e")
            } finally {
                isLoading.value = false
            }
        }
    }
    
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
            else -> null
        }
    }
    
    // Subir imagen a Supabase Storage
    private suspend fun uploadImage(context: Context, uri: Uri, imageName: String): String? {
        return withContext(Dispatchers.IO) {
            try {
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
                
                println("[EditRoomViewModel] Uploaded $imageName: $publicUrl")
                publicUrl
            } catch (e: Exception) {
                println("[EditRoomViewModel] Error uploading $imageName: ${e.message}")
                throw e
            }
        }
    }
    
    // Actualizar cuarto
    fun updateRoom(context: Context, roomId: Int, onSuccess: () -> Unit) {
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
                
                // Verificar autenticación
                val user = SupabaseClient.client.auth.currentUserOrNull()
                    ?: throw IllegalStateException("Usuario no autenticado")
                
                // Subir nuevas imágenes si se seleccionaron
                var imagen1Url: String? = imagen1UrlExisting.value
                if (imagen1UriNew.value != null) {
                    uploadProgress.value = "Subiendo imagen principal..."
                    imagen1Url = uploadImage(context, imagen1UriNew.value!!, "cuarto_1")
                }
                
                var imagen2Url: String? = imagen2UrlExisting.value
                if (imagen2UriNew.value != null) {
                    uploadProgress.value = "Subiendo imagen 2..."
                    imagen2Url = uploadImage(context, imagen2UriNew.value!!, "cuarto_2")
                }
                
                var imagen3Url: String? = imagen3UrlExisting.value
                if (imagen3UriNew.value != null) {
                    uploadProgress.value = "Subiendo imagen 3..."
                    imagen3Url = uploadImage(context, imagen3UriNew.value!!, "cuarto_3")
                }
                
                // Actualizar cuarto
                uploadProgress.value = "Actualizando cuarto..."
                
                println("[EditRoomViewModel] Updating with latitud: ${latitud.value}, longitud: ${longitud.value}")
                
                val roomData = RoomUpdate(
                    titulo = titulo.value.trim(),
                    descripcion = descripcion.value.trim(),
                    precio = precio.value.toDouble(),
                    celular = celular.value.trim(),
                    caracteristicas = caracteristicas.value.trim(),
                    ubicacion = ubicacion.value.trim(),
                    latitud = latitud.value,
                    longitud = longitud.value,
                    imagen_1 = imagen1Url,
                    imagen_2 = imagen2Url,
                    imagen_3 = imagen3Url,
                    activo = disponible.value
                )
                
                repository.updateRoom(roomId, roomData)
                
                success.value = true
                uploadProgress.value = "¡Cuarto actualizado exitosamente!"
                
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
                
            } catch (e: Exception) {
                val message = when (e) {
                    is IllegalStateException -> e.message ?: "Error de autenticación"
                    is java.net.UnknownHostException -> "Sin conexión a Internet"
                    is java.net.SocketTimeoutException -> "Tiempo de espera agotado"
                    else -> e.message ?: "Error desconocido al actualizar cuarto"
                }
                error.value = message
                uploadProgress.value = ""
                println("[EditRoomViewModel] Error updating room: $e")
            } finally {
                isLoading.value = false
            }
        }
    }
}
