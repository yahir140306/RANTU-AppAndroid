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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import com.example.rantu.di.ViewModelFactory
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
    viewModel: RoomDetailViewModel = viewModel(factory = ViewModelFactory)
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
    
    // Estado para modal de imagen
    var showImageModal by remember { mutableStateOf(false) }
    var selectedImageUrl by remember { mutableStateOf("") }
    
    val pagerState = rememberPagerState()

    ImageGalleryModal(
        showImageModal = showImageModal,
        selectedImageUrl = selectedImageUrl,
        images = images,
        onDismiss = { showImageModal = false }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Cuarto") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
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
                RoomImageCarousel(
                    images = images,
                    pagerState = pagerState,
                    onImageClick = { url ->
                        selectedImageUrl = url
                        showImageModal = true
                    }
                )
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
                    
                    // Indicador de disponibilidad
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        val isAvailable = room.isAvailable ?: true
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isAvailable) Color(0xFF16A34A) else Color(0xFFDC2626),
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isAvailable) Icons.Default.Check else Icons.Default.Close,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isAvailable) "Disponible" else "No disponible",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
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
                val isAvailable = room.isAvailable ?: true
                
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
                            enabled = !room.celular.isNullOrEmpty() && isAvailable
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
                        ),
                        enabled = isAvailable
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
            
            // Mapa de ubicación
            if (room.latitud != null && room.longitud != null) {
                item {
                    RoomMap(room = room, context = context)
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
                        val isAvailableForComments = room.isAvailable ?: true
                        Button(
                            onClick = { showCommentDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3B82F6)
                            ),
                            enabled = isAvailableForComments
                        ) {
                            Text(if (isAvailableForComments) "Deja tu comentario" else "No disponible para comentar")
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
                val isAvailableBottom = room.isAvailable ?: true
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
                    enabled = !room.celular.isNullOrBlank() && isAvailableBottom
                ) {
                    Text("📱 Contactar al Propietario", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
    
    // Diálogo para agregar comentario
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
    
    // Diálogo de descarga de tarjeta
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
        // Supabase devuelve fechas en formato ISO 8601 UTC
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "ES"))
        formatter.timeZone = TimeZone.getDefault()
        val date = parser.parse(dateString)
        date?.let { formatter.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}