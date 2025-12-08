package com.example.rantu.Components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.rantu.data.Room
import com.example.rantu.ui.EditRoomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRoomScreen(
    room: Room,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    onOpenLocationPicker: (Double?, Double?, (Double, Double) -> Unit) -> Unit = { _, _, _ -> },
    editRoomViewModel: EditRoomViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    // Cargar datos del cuarto al inicio
    LaunchedEffect(room.id) {
        editRoomViewModel.loadRoom(room.id)
    }
    
    // Launchers para seleccionar imágenes
    val launcher1 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { editRoomViewModel.imagen1UriNew.value = it }
    }
    
    val launcher2 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { editRoomViewModel.imagen2UriNew.value = it }
    }
    
    val launcher3 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { editRoomViewModel.imagen3UriNew.value = it }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Editar Cuarto", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        if (editRoomViewModel.isLoading.value && editRoomViewModel.currentRoom.value == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Descripción
                Text(
                    text = "Actualiza la información de tu cuarto",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Información Básica
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "📋 Información Básica",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // Título
                        OutlinedTextField(
                            value = editRoomViewModel.titulo.value,
                            onValueChange = { editRoomViewModel.titulo.value = it },
                            label = { Text("Nombre del cuarto *") },
                            placeholder = { Text("Ej: Cuarto luminoso cerca del centro") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Descripción
                        OutlinedTextField(
                            value = editRoomViewModel.descripcion.value,
                            onValueChange = { editRoomViewModel.descripcion.value = it },
                            label = { Text("Descripción *") },
                            placeholder = { Text("Describe las características, amenidades, ubicación...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            maxLines = 5
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Precio
                        OutlinedTextField(
                            value = editRoomViewModel.precio.value,
                            onValueChange = { editRoomViewModel.precio.value = it },
                            label = { Text("Precio mensual *") },
                            placeholder = { Text("5000.00") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            leadingIcon = { Text("$", fontWeight = FontWeight.Bold) },
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Celular
                        OutlinedTextField(
                            value = editRoomViewModel.celular.value,
                            onValueChange = { editRoomViewModel.celular.value = it },
                            label = { Text("Número de Celular (WhatsApp) *") },
                            placeholder = { Text("3001234567") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true
                        )
                        Text(
                            text = "Este número se usará para contacto por WhatsApp",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Disponibilidad
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = if (editRoomViewModel.disponible.value) 
                                        Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (editRoomViewModel.disponible.value) 
                                        "✅ Cuarto Disponible" else "❌ No Disponible",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (editRoomViewModel.disponible.value) 
                                        Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                                Text(
                                    text = if (editRoomViewModel.disponible.value)
                                        "Los usuarios pueden ver y contactarte"
                                    else
                                        "El cuarto no aparecerá en búsquedas",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Switch(
                                checked = editRoomViewModel.disponible.value,
                                onCheckedChange = { editRoomViewModel.disponible.value = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF4CAF50),
                                    checkedTrackColor = Color(0xFFC8E6C9),
                                    uncheckedThumbColor = Color(0xFFEF5350),
                                    uncheckedTrackColor = Color(0xFFFFCDD2)
                                )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Detalles
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "📝 Detalles del Cuarto",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // Características
                        OutlinedTextField(
                            value = editRoomViewModel.caracteristicas.value,
                            onValueChange = { editRoomViewModel.caracteristicas.value = it },
                            label = { Text("Características *") },
                            placeholder = { Text("Baño privado, WiFi, cocina compartida...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            maxLines = 4
                        )
                        Text(
                            text = "Mínimo 20 caracteres",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Ubicación
                        OutlinedTextField(
                            value = editRoomViewModel.ubicacion.value,
                            onValueChange = { editRoomViewModel.ubicacion.value = it },
                            label = { Text("Ubicación Específica *") },
                            placeholder = { Text("Colonia, referencias cercanas, accesos...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            maxLines = 4
                        )
                        Text(
                            text = "Mínimo 10 caracteres",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Ubicación en mapa
                        Text(
                            text = "📍 Ubicación en el Mapa",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        if (editRoomViewModel.latitud.value != null && editRoomViewModel.longitud.value != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFE8F5E9)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "✅ Ubicación seleccionada",
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF2E7D32)
                                        )
                                        Text(
                                            text = "${"%.6f".format(editRoomViewModel.latitud.value)}, ${"%.6f".format(editRoomViewModel.longitud.value)}",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            editRoomViewModel.latitud.value = null
                                            editRoomViewModel.longitud.value = null
                                        }
                                    ) {
                                        Icon(Icons.Default.Close, "Quitar ubicación", tint = Color(0xFF2E7D32))
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = {
                                onOpenLocationPicker(
                                    editRoomViewModel.latitud.value,
                                    editRoomViewModel.longitud.value
                                ) { lat, lng ->
                                    editRoomViewModel.latitud.value = lat
                                    editRoomViewModel.longitud.value = lng
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF667EEA)
                            )
                        ) {
                            Icon(
                                imageVector = if (editRoomViewModel.latitud.value != null) Icons.Default.Edit else Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (editRoomViewModel.latitud.value != null) 
                                    "Cambiar Ubicación en Mapa" 
                                else 
                                    "Agregar Ubicación en Mapa"
                            )
                        }
                        
                        Text(
                            text = "Opcional: Ayuda a los interesados a encontrar tu cuarto más fácilmente",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Imágenes
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "📷 Imágenes del Cuarto",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Solo sube imágenes si quieres cambiar las actuales",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // Imagen 1
                        EditableImagePicker(
                            label = "Imagen principal",
                            existingUrl = editRoomViewModel.imagen1UrlExisting.value,
                            newUri = editRoomViewModel.imagen1UriNew.value,
                            onPickImage = { launcher1.launch("image/*") },
                            onRemoveNewImage = { editRoomViewModel.imagen1UriNew.value = null }
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Imagen 2
                        EditableImagePicker(
                            label = "Imagen adicional",
                            existingUrl = editRoomViewModel.imagen2UrlExisting.value,
                            newUri = editRoomViewModel.imagen2UriNew.value,
                            onPickImage = { launcher2.launch("image/*") },
                            onRemoveNewImage = { editRoomViewModel.imagen2UriNew.value = null }
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Imagen 3
                        EditableImagePicker(
                            label = "Imagen adicional",
                            existingUrl = editRoomViewModel.imagen3UrlExisting.value,
                            newUri = editRoomViewModel.imagen3UriNew.value,
                            onPickImage = { launcher3.launch("image/*") },
                            onRemoveNewImage = { editRoomViewModel.imagen3UriNew.value = null }
                        )
                        
                        Text(
                            text = "Formatos permitidos: JPG, PNG. Tamaño máximo: 5MB por imagen.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Mensajes de error/progreso
                if (editRoomViewModel.error.value != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        )
                    ) {
                        Text(
                            text = editRoomViewModel.error.value!!,
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                if (editRoomViewModel.uploadProgress.value.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE3F2FD)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = editRoomViewModel.uploadProgress.value,
                                color = Color(0xFF1976D2)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                        enabled = !editRoomViewModel.isLoading.value
                    ) {
                        Text("Cancelar")
                    }
                    
                    Button(
                        onClick = {
                            editRoomViewModel.updateRoom(context, room.id) {
                                onSuccess()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !editRoomViewModel.isLoading.value,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF59E0B)
                        )
                    ) {
                        if (editRoomViewModel.isLoading.value) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text("Actualizar Cuarto")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun EditableImagePicker(
    label: String,
    existingUrl: String?,
    newUri: Uri?,
    onPickImage: () -> Unit,
    onRemoveNewImage: () -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Box(modifier = Modifier.fillMaxWidth()) {
            when {
                // Mostrar nueva imagen si se seleccionó
                newUri != null -> {
                    Box {
                        AsyncImage(
                            model = newUri,
                            contentDescription = label,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        
                        // Botón para eliminar
                        IconButton(
                            onClick = onRemoveNewImage,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(32.dp)
                                .background(
                                    color = Color.Red.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(50)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Eliminar imagen nueva",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        // Indicador de nueva imagen
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                                .background(
                                    color = Color(0xFF10B981),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Nueva",
                                fontSize = 10.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                // Mostrar imagen existente
                existingUrl != null && existingUrl.isNotEmpty() -> {
                    Box {
                        AsyncImage(
                            model = existingUrl,
                            contentDescription = label,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        
                        // Botón para cambiar imagen
                        IconButton(
                            onClick = onPickImage,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(32.dp)
                                .background(
                                    color = Color(0xFF3B82F6).copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(50)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Cambiar imagen",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                // Sin imagen - mostrar selector
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = 2.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(Color(0xFFF9FAFB))
                            .clickable { onPickImage() },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar imagen",
                                tint = Color.Gray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Haz clic para subir",
                                fontSize = 14.sp,
                                color = Color(0xFF3B82F6),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
