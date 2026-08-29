import os
import re

components_code = """package com.example.rantu.Components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.rantu.data.Room
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.CaptureController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageGalleryModal(
    showImageModal: Boolean,
    selectedImageUrl: String,
    images: List<String>,
    onDismiss: () -> Unit
) {
    if (showImageModal) {
        val initialPage = remember(selectedImageUrl, images) { 
            images.indexOf(selectedImageUrl).takeIf { it >= 0 } ?: 0 
        }
        val modalPagerState = com.google.accompanist.pager.rememberPagerState(initialPage = initialPage)
        
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                HorizontalPager(
                    count = images.size,
                    state = modalPagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    AsyncImage(
                        model = images[page],
                        contentDescription = "Imagen Ampliada ${page + 1}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onDismiss() },
                        contentScale = ContentScale.Fit
                    )
                }
                
                if (images.size > 1) {
                    HorizontalPagerIndicator(
                        pagerState = modalPagerState,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        activeColor = Color.White,
                        inactiveColor = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RoomImageCarousel(
    images: List<String>,
    pagerState: PagerState,
    onImageClick: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            count = images.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) { page ->
            AsyncImage(
                model = images[page],
                contentDescription = "Imagen ${page + 1}",
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        onImageClick(images[page])
                    },
                contentScale = ContentScale.Crop
            )
        }
        
        if (images.size > 1) {
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                activeColor = Color.White,
                inactiveColor = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun RoomMap(room: Room, context: Context) {
    if (room.latitud != null && room.longitud != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "📍 Ubicación en el Mapa",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    val position = com.google.android.gms.maps.model.LatLng(
                        room.latitud,
                        room.longitud
                    )
                    val mapCameraPositionState = com.google.maps.android.compose.rememberCameraPositionState {
                        this.position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
                            position,
                            15f
                        )
                    }
                    
                    com.google.maps.android.compose.GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = mapCameraPositionState,
                        uiSettings = com.google.maps.android.compose.MapUiSettings(
                            zoomControlsEnabled = true,
                            scrollGesturesEnabled = true,
                            zoomGesturesEnabled = true
                        )
                    ) {
                        com.google.maps.android.compose.Marker(
                            state = com.google.maps.android.compose.rememberMarkerState(position = position),
                            title = room.title ?: "Cuarto"
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Coordenadas: ${"%.6f".format(room.latitud)}, ${"%.6f".format(room.longitud)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                
                Button(
                    onClick = {
                        val gmmIntentUri = Uri.parse("geo:${room.latitud},${room.longitud}?q=${room.latitud},${room.longitud}(${room.title})")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        
                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        } else {
                            val browserIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.google.com/maps/search/?api=1&query=${room.latitud},${room.longitud}")
                            )
                            context.startActivity(browserIntent)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4285F4)
                    )
                ) {
                    Text("Abrir en Google Maps")
                }
            }
        }
    }
}

@Composable
fun AddCommentDialog(
    showCommentDialog: Boolean,
    onDismiss: () -> Unit,
    selectedRating: Int,
    onRatingChange: (Int) -> Unit,
    commentText: String,
    onCommentTextChange: (String) -> Unit,
    submitError: String?,
    onSubmit: () -> Unit
) {
    if (showCommentDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Deja tu comentario") },
            text = {
                Column {
                    Text("Calificación *", fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = if (i <= selectedRating) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "Estrella $i",
                                tint = if (i <= selectedRating) Color(0xFFFCD34D) else Color.Gray,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable { onRatingChange(i) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Comentario *", fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = onCommentTextChange,
                        placeholder = { Text("Comparte tu experiencia...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5
                    )
                    
                    if (submitError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = submitError,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = onSubmit) {
                    Text("Publicar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun DownloadCardDialog(
    showDownloadDialog: Boolean,
    onDismiss: () -> Unit,
    isDownloading: Boolean,
    onDownloadClick: () -> Unit,
    room: Room,
    context: Context,
    captureController: CaptureController,
    onCaptured: (androidx.compose.ui.graphics.ImageBitmap?, Throwable?) -> Unit
) {
    if (showDownloadDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier.wrapContentSize(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Descargar Tarjeta",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Capturable(
                        controller = captureController,
                        onCaptured = onCaptured
                    ) {
                        DownloadableRoomCard(room = room)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            enabled = !isDownloading
                        ) {
                            Text("Cancelar")
                        }
                        
                        Button(
                            onClick = onDownloadClick,
                            modifier = Modifier.weight(1f),
                            enabled = !isDownloading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B981)
                            )
                        ) {
                            if (isDownloading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Descargar",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Descargar")
                            }
                        }
                    }
                }
            }
        }
    }
}
"""

components_file_path = "/home/juanchis/Documentos/RANTU/RANTU-AppAndroid/app/src/main/java/com/example/rantu/Components/RoomDetailComponents.kt"
with open(components_file_path, "w", encoding="utf-8") as f:
    f.write(components_code)

screen_file_path = "/home/juanchis/Documentos/RANTU/RANTU-AppAndroid/app/src/main/java/com/example/rantu/Components/RoomDetailScreen.kt"

with open(screen_file_path, "r", encoding="utf-8") as f:
    content = f.read()

# Refactor the screen file content
content = re.sub(
    r'    // Estado para modal de imagen.*?    if \(showImageModal\) \{.*?    \}\n\n    Scaffold\(',
    r'''    // Estado para modal de imagen
    var showImageModal by remember { mutableStateOf(false) }
    var selectedImageUrl by remember { mutableStateOf("") }
    
    val pagerState = rememberPagerState()

    ImageGalleryModal(
        showImageModal = showImageModal,
        selectedImageUrl = selectedImageUrl,
        images = images,
        onDismiss = { showImageModal = false }
    )

    Scaffold(''',
    content,
    flags=re.DOTALL
)

content = re.sub(
    r'            // Carrusel de imágenes.*?            // Información principal',
    r'''            // Carrusel de imágenes
            item {
                RoomImageCarousel(
                    images = images,
                    pagerState = pagerState,
                    onImageClick = { url ->
                        selectedImageUrl = url
                        showImageModal = true
                    }
                )
            }
            
            // Información principal''',
    content,
    flags=re.DOTALL
)

content = re.sub(
    r'            // Mapa de ubicación.*?            // Sección de Comentarios y Calificaciones',
    r'''            // Mapa de ubicación
            if (room.latitud != null && room.longitud != null) {
                item {
                    RoomMap(room = room, context = context)
                }
            }
            
            // Sección de Comentarios y Calificaciones''',
    content,
    flags=re.DOTALL
)

content = re.sub(
    r'    // Diálogo para agregar comentario.*?    // Diálogo de descarga de tarjeta',
    r'''    // Diálogo para agregar comentario
    AddCommentDialog(
        showCommentDialog = showCommentDialog,
        onDismiss = {
            showCommentDialog = false
            selectedRating = 0
            commentText = ""
            submitError = null
        },
        selectedRating = selectedRating,
        onRatingChange = { selectedRating = it },
        commentText = commentText,
        onCommentTextChange = { commentText = it },
        submitError = submitError,
        onSubmit = {
            if (selectedRating == 0) {
                submitError = "Selecciona una calificación"
            } else if (commentText.length < 10) {
                submitError = "El comentario debe tener al menos 10 caracteres"
            } else {
                // Assuming viewModel exists in scope or is passed. It is in RoomDetailScreen.
                viewModel.addComment(room.id, commentText, selectedRating)
                showCommentDialog = false
                selectedRating = 0
                commentText = ""
                submitError = null
            }
        }
    )
    
    // Diálogo de descarga de tarjeta''',
    content,
    flags=re.DOTALL
)

content = re.sub(
    r'    // Diálogo de descarga de tarjeta.*?    if \(showDownloadDialog\) \{.*?    \}\n\}',
    r'''    // Diálogo de descarga de tarjeta
    DownloadCardDialog(
        showDownloadDialog = showDownloadDialog,
        onDismiss = { showDownloadDialog = false },
        isDownloading = isDownloading,
        onDownloadClick = {
            isDownloading = true
            coroutineScope.launch {
                captureController.capture()
            }
        },
        room = room,
        context = context,
        captureController = captureController,
        onCaptured = { bitmap, err ->
            if (bitmap != null && err == null) {
                val filename = "RANTU_${room.title?.replace(" ", "_")}_${System.currentTimeMillis()}.png"
                val androidBitmap = bitmap.asAndroidBitmap()
                // Assuming saveBitmapToGallery is in scope.
                val success = saveBitmapToGallery(context, androidBitmap, filename)
                
                isDownloading = false
                showDownloadDialog = false
                
                if (success) {
                    Toast.makeText(context, "¡Tarjeta guardada en Galería!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
                }
            } else {
                isDownloading = false
                Toast.makeText(context, "Error al capturar: ${err?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    )
}''',
    content,
    flags=re.DOTALL
)

with open(screen_file_path, "w", encoding="utf-8") as f:
    f.write(content)

print("Refactoring complete.")
