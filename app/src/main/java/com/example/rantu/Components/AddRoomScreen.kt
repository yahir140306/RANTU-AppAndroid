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
import com.example.rantu.di.ViewModelFactory
import coil.compose.AsyncImage
import com.example.rantu.ui.AddRoomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoomScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    onOpenLocationPicker: (Double?, Double?, (Double, Double) -> Unit) -> Unit = { _, _, _ -> },
    addRoomViewModel: AddRoomViewModel = viewModel(factory = ViewModelFactory)
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    // Launchers para seleccionar imágenes
    val launcher1 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { addRoomViewModel.imagen1Uri.value = it }
    }
    
    val launcher2 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { addRoomViewModel.imagen2Uri.value = it }
    }
    
    val launcher3 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { addRoomViewModel.imagen3Uri.value = it }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Agregar Nuevo Cuarto", fontWeight = FontWeight.Bold)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Descripción
            Text(
                text = "Completa la información para publicar tu cuarto",
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
                        value = addRoomViewModel.titulo.value,
                        onValueChange = { addRoomViewModel.titulo.value = it },
                        label = { Text("Nombre del cuarto *") },
                        placeholder = { Text("Ej: Cuarto luminoso cerca del centro") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Descripción
                    OutlinedTextField(
                        value = addRoomViewModel.descripcion.value,
                        onValueChange = { addRoomViewModel.descripcion.value = it },
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
                        value = addRoomViewModel.precio.value,
                        onValueChange = { addRoomViewModel.precio.value = it },
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
                        value = addRoomViewModel.celular.value,
                        onValueChange = { addRoomViewModel.celular.value = it },
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
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Detalles
            RoomFormDetails(
                    caracteristicas = addRoomViewModel.caracteristicas.value,
                    onCaracteristicasChange = { addRoomViewModel.caracteristicas.value = it },
                    celular = addRoomViewModel.celular.value,
                    onCelularChange = { addRoomViewModel.celular.value = it }
                )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Imágenes
            RoomImageUploader(
                    imagen1Uri = addRoomViewModel.imagen1Uri.value,
                    imagen2Uri = addRoomViewModel.imagen2Uri.value,
                    imagen3Uri = addRoomViewModel.imagen3Uri.value,
                    onImage1Click = { launcher1.launch("image/*") },
                    onImage2Click = { launcher2.launch("image/*") },
                    onImage3Click = { launcher3.launch("image/*") }
                )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mensajes de error/progreso
            if (addRoomViewModel.error.value != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Text(
                        text = addRoomViewModel.error.value!!,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            if (addRoomViewModel.uploadProgress.value.isNotEmpty()) {
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
                            text = addRoomViewModel.uploadProgress.value,
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
                    enabled = !addRoomViewModel.isLoading.value
                ) {
                    Text("Cancelar")
                }
                
                Button(
                    onClick = {
                        addRoomViewModel.createRoom(context) {
                            onSuccess()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !addRoomViewModel.isLoading.value,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6)
                    )
                ) {
                    if (addRoomViewModel.isLoading.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text("Agregar Cuarto")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ImagePicker(
    label: String,
    uri: Uri?,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        if (uri == null) {
            // Área de selección
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
                    Text(
                        text = "PNG, JPG hasta 5MB",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            // Vista previa de la imagen
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = uri,
                    contentDescription = label,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Botón para eliminar
                IconButton(
                    onClick = onRemoveImage,
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
                        contentDescription = "Eliminar imagen",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
