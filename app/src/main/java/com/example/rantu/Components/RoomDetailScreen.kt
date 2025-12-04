package com.example.rantu.Components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.rantu.data.Comment
import com.example.rantu.data.Room
import com.example.rantu.ui.RoomDetailViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun RoomDetailScreen(
    room: Room,
    onBack: () -> Unit,
    viewModel: RoomDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val comments = viewModel.comments.value
    val statistics = viewModel.statistics.value
    val isLoading = viewModel.isLoading.value
    val error = viewModel.error.value
    
    val captureController = rememberCaptureController()
    val coroutineScope = rememberCoroutineScope()
    var showDownloadDialog by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    
    var selectedRating by remember { mutableStateOf(0) }
    var commentText by remember { mutableStateOf("") }
    var showCommentDialog by remember { mutableStateOf(false) }
    var submitError by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(room.id) {
        viewModel.loadComments(room.id)
    }
    
    // Preparar imágenes
    val images = remember(room) {
        listOfNotNull(
            room.imagen1?.takeIf { it.isNotBlank() },
            room.imagen2?.takeIf { it.isNotBlank() },
            room.imagen3?.takeIf { it.isNotBlank() }
        ).ifEmpty { listOf(room.resolvedImageUrl()) }
    }
    
    val pagerState = rememberPagerState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Cuarto") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Carrusel de imágenes
            item {
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
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    // Indicador de página
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
            
            // Información principal
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = room.title ?: "Sin título",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$${room.price?.toInt() ?: 0} / mes",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3B82F6)
                    )
                }
            }
            
            // Botones de acción (WhatsApp, Compartir y Descargar)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Botón de WhatsApp
                        Button(
                            onClick = {
                                val celular = room.celular ?: ""
                                if (celular.isNotEmpty()) {
                                    val mensaje = "Hola, me interesa el cuarto: ${room.title} - $${room.price}/mes. " +
                                            "Lo vi en RANTU. ¿Está disponible?"
                                    val mensajeCodificado = URLEncoder.encode(mensaje, "UTF-8")
                                    val whatsappUrl = "https://wa.me/$celular?text=$mensajeCodificado"
                                    
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse(whatsappUrl)
                                    }
                                    context.startActivity(intent)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF25D366)
                            ),
                            enabled = !room.celular.isNullOrEmpty()
                        ) {
                            Icon(
                                painter = androidx.compose.ui.res.painterResource(
                                    id = android.R.drawable.stat_notify_chat
                                ),
                                contentDescription = "WhatsApp",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("WhatsApp")
                        }
                        
                        // Botón de Compartir
                        Button(
                            onClick = {
                                val webUrl = "https://prototype-delta-vert.vercel.app/cuarto/${room.id}"
                                val deepLinkUrl = "rantu://cuarto/${room.id}"
                                val shareText = """
                                    🏠 ${room.title}
                                    💰 $${room.price}/mes
                                    
                                    ${room.description?.take(100)}...
                                    
                                    Ver más detalles:
                                    $webUrl
                                    
                                    O abre en la app:
                                    $deepLinkUrl
                                """.trimIndent()
                                
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    putExtra(Intent.EXTRA_TITLE, "Compartir cuarto")
                                    type = "text/plain"
                                }
                                
                                context.startActivity(
                                    Intent.createChooser(shareIntent, "Compartir cuarto via")
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6B7280)
                            )
                        ) {
                            Icon(
                                painter = androidx.compose.ui.res.painterResource(
                                    id = android.R.drawable.ic_menu_share
                                ),
                                contentDescription = "Compartir",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Compartir")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Botón de Descargar Tarjeta
                    Button(
                        onClick = { showDownloadDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Descargar",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Descargar Tarjeta")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Descripción
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Descripción",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            room.description ?: "Sin descripción",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            // Características y Ubicación
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Características",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                room.caracteristicas ?: "No especificadas",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Ubicación",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                room.ubicacion ?: "No especificada",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
            
            // Sección de Comentarios y Calificaciones
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Comentarios y Calificaciones",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            if (statistics != null) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        StarRating(rating = statistics.promedio, size = 20.dp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            String.format("%.1f", statistics.promedio),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        "${statistics.total} comentarios",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Botón para agregar comentario
                        Button(
                            onClick = { showCommentDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3B82F6)
                            )
                        ) {
                            Text("Deja tu comentario")
                        }
                    }
                }
            }
            
            // Lista de comentarios
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (comments.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF9FAFB)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("💬", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No hay comentarios aún",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "¡Sé el primero en comentar!",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            } else {
                items(comments) { comment ->
                    CommentCard(comment = comment)
                }
            }
            
            // Botón de WhatsApp
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        room.celular?.let { celular ->
                            val numero = celular.replace(Regex("[\\s\\-()]"), "")
                            val mensaje = "Hola! Me interesa el cuarto \"${room.title}\" con precio de $${room.price?.toInt()}. ¿Podrías darme más información?"
                            val mensajeCodificado = URLEncoder.encode(mensaje, "UTF-8")
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$numero?text=$mensajeCodificado"))
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF25D366)
                    ),
                    enabled = !room.celular.isNullOrBlank()
                ) {
                    Text("📱 Contactar al Propietario", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
    
    // Diálogo para agregar comentario
    if (showCommentDialog) {
        AlertDialog(
            onDismissRequest = {
                showCommentDialog = false
                selectedRating = 0
                commentText = ""
                submitError = null
            },
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
                                    .clickable { selectedRating = i }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Comentario *", fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Comparte tu experiencia...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5
                    )
                    
                    if (submitError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = submitError!!,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedRating == 0) {
                            submitError = "Selecciona una calificación"
                            return@Button
                        }
                        if (commentText.length < 10) {
                            submitError = "El comentario debe tener al menos 10 caracteres"
                            return@Button
                        }
                        
                        viewModel.addComment(
                            roomId = room.id,
                            comentario = commentText,
                            calificacion = selectedRating,
                            onSuccess = {
                                showCommentDialog = false
                                selectedRating = 0
                                commentText = ""
                                submitError = null
                            },
                            onError = { error ->
                                submitError = error
                            }
                        )
                    }
                ) {
                    Text("Publicar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCommentDialog = false
                    selectedRating = 0
                    commentText = ""
                    submitError = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo de descarga de tarjeta
    if (showDownloadDialog) {
        Dialog(onDismissRequest = { showDownloadDialog = false }) {
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
                    
                    // Vista previa capturable
                    Capturable(
                        controller = captureController,
                        onCaptured = { bitmap, error ->
                            if (bitmap != null && error == null) {
                                val filename = "RANTU_${room.title?.replace(" ", "_")}_${System.currentTimeMillis()}.png"
                                val androidBitmap = bitmap.asAndroidBitmap()
                                val success = saveBitmapToGallery(context, androidBitmap, filename)
                                
                                isDownloading = false
                                showDownloadDialog = false
                                
                                if (success) {
                                    Toast.makeText(
                                        context,
                                        "¡Tarjeta guardada en Galería!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Error al guardar la imagen",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                isDownloading = false
                                Toast.makeText(
                                    context,
                                    "Error al capturar: ${error?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    ) {
                        DownloadableRoomCard(room = room)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showDownloadDialog = false },
                            modifier = Modifier.weight(1f),
                            enabled = !isDownloading
                        ) {
                            Text("Cancelar")
                        }
                        
                        Button(
                            onClick = {
                                isDownloading = true
                                coroutineScope.launch {
                                    captureController.capture()
                                }
                            },
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

@Composable
fun StarRating(rating: Double, size: androidx.compose.ui.unit.Dp = 16.dp) {
    Row {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Estrella",
                tint = if (i <= rating) Color(0xFFFCD34D) else Color.Gray,
                modifier = Modifier.size(size)
            )
        }
    }
}

@Composable
fun CommentCard(comment: Comment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF3B82F6), shape = RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        comment.getUserInitial(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        comment.getFormattedEmail().split("@")[0],
                        fontWeight = FontWeight.Medium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StarRating(rating = comment.calificacion.toDouble())
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            formatDate(comment.createdAt),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                comment.comentario,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }
}

fun formatDate(dateString: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
        val date = parser.parse(dateString)
        date?.let { formatter.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}