package com.example.rantu.Components

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.rantu.data.Room
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DownloadableRoomCard(
    room: Room,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(400.dp)
            .height(500.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF667EEA),
                            Color(0xFF764BA2)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Column {
                    Text(
                        text = "RANTU",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Encuentra tu espacio ideal",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                
                // Imagen del cuarto
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    AsyncImage(
                        model = room.resolvedImageUrl(),
                        contentDescription = room.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                
                // Información del cuarto
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.95f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = room.title ?: "Cuarto disponible",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937),
                        maxLines = 2
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "$${room.price?.toInt() ?: 0}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3B82F6)
                        )
                        Text(
                            text = "/mes",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (!room.ubicacion.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "📍",
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = room.ubicacion ?: "",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                maxLines = 1
                            )
                        }
                    }
                    
                    if (!room.caracteristicas.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "✨",
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = room.caracteristicas ?: "",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                maxLines = 1
                            )
                        }
                    }
                }
                
                // Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    
                    Text(
                        text = "Escanea para más info →",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

// Función para guardar bitmap
fun saveBitmapToGallery(context: Context, bitmap: Bitmap, filename: String): Boolean {
    return try {
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val rantuDir = File(picturesDir, "RANTU")
        
        if (!rantuDir.exists()) {
            rantuDir.mkdirs()
        }
        
        val file = File(rantuDir, filename)
        val outputStream = FileOutputStream(file)
        
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        
        // Notificar a la galería
        android.media.MediaScannerConnection.scanFile(
            context,
            arrayOf(file.absolutePath),
            arrayOf("image/png"),
            null
        )
        
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
