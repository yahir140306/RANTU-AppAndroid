package com.example.rantu.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.rantu.data.Room
import com.example.rantu.ui.UserRoomsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRoomsScreen(
    onBack: () -> Unit,
    onAddRoom: () -> Unit = {},
    onEditRoom: (Room) -> Unit = {},
    userRoomsViewModel: UserRoomsViewModel = viewModel()
) {
    val rooms = userRoomsViewModel.rooms.value
    val isLoading = userRoomsViewModel.isLoading.value
    val errorMsg = userRoomsViewModel.error.value
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var roomToDelete by remember { mutableStateOf<Room?>(null) }
    var deleteError by remember { mutableStateOf<String?>(null) }
    
    var showToggleDialog by remember { mutableStateOf(false) }
    var roomToToggle by remember { mutableStateOf<Room?>(null) }
    var toggleError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Mis Cuartos", fontWeight = FontWeight.Bold)
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddRoom,
                containerColor = Color(0xFF3B82F6)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar cuarto",
                    tint = Color.White
                )
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMsg != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Error al cargar tus cuartos",
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMsg, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { userRoomsViewModel.loadUserRooms() }) {
                        Text("Reintentar")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                // Header con estadísticas
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Gestiona los cuartos que has publicado",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Estadísticas
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Total",
                            value = userRoomsViewModel.totalRooms.toString(),
                            icon = Icons.Default.Home,
                            backgroundColor = Color(0xFF3B82F6),
                            modifier = Modifier.weight(1f)
                        )
                        
                        StatCard(
                            title = "Activos",
                            value = userRoomsViewModel.totalRooms.toString(),
                            icon = Icons.Default.Home,
                            backgroundColor = Color(0xFF10B981),
                            modifier = Modifier.weight(1f)
                        )
                        
                        StatCard(
                            title = "Precio Prom.",
                            value = "$${userRoomsViewModel.averagePrice.toInt()}",
                            icon = Icons.Default.Home,
                            backgroundColor = Color(0xFFF59E0B),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Lista vacía
                if (rooms.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
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
                                Text("📦", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "No tienes cuartos publicados",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "¡Empieza agregando tu primer cuarto!",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = onAddRoom,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF3B82F6)
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Agregar Cuarto")
                                }
                            }
                        }
                    }
                } else {
                    // Lista de cuartos
                    items(rooms) { room ->
                        RoomManagementCard(
                            room = room,
                            onEdit = { onEditRoom(room) },
                            onDelete = {
                                roomToDelete = room
                                showDeleteDialog = true
                            },
                            onToggleActive = {
                                roomToToggle = room
                                showToggleDialog = true
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Diálogo de confirmación de eliminación
    if (showDeleteDialog && roomToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteDialog = false
                deleteError = null
            },
            title = {
                Text("Eliminar cuarto")
            },
            text = {
                Column {
                    Text("¿Estás seguro de que quieres eliminar \"${roomToDelete?.title}\"?")
                    if (deleteError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = deleteError!!,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        roomToDelete?.id?.let { roomId ->
                            userRoomsViewModel.deleteRoom(
                                roomId = roomId,
                                onSuccess = {
                                    showDeleteDialog = false
                                    roomToDelete = null
                                    deleteError = null
                                },
                                onError = { error ->
                                    deleteError = error
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showDeleteDialog = false
                    deleteError = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo de activar/desactivar cuarto
    if (showToggleDialog && roomToToggle != null) {
        val isCurrentlyActive = roomToToggle?.isAvailable ?: true
        val actionText = if (isCurrentlyActive) "desactivar" else "activar"
        
        AlertDialog(
            onDismissRequest = { 
                showToggleDialog = false
                toggleError = null
            },
            title = {
                Text("${actionText.capitalize()} cuarto")
            },
            text = {
                Column {
                    Text(
                        if (isCurrentlyActive) {
                            "¿Deseas desactivar \"${roomToToggle?.title}\"? El cuarto dejará de mostrarse en la lista pública."
                        } else {
                            "¿Deseas activar \"${roomToToggle?.title}\"? El cuarto volverá a mostrarse en la lista pública."
                        }
                    )
                    if (toggleError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = toggleError!!,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        roomToToggle?.id?.let { roomId ->
                            userRoomsViewModel.toggleRoomActive(
                                roomId = roomId,
                                isActive = !isCurrentlyActive,
                                onSuccess = {
                                    showToggleDialog = false
                                    roomToToggle = null
                                    toggleError = null
                                },
                                onError = { error ->
                                    toggleError = error
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCurrentlyActive) Color(0xFFF59E0B) else Color(0xFF10B981)
                    )
                ) {
                    Text(actionText.capitalize())
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showToggleDialog = false
                    toggleError = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(backgroundColor, shape = RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun RoomManagementCard(
    room: Room,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleActive: () -> Unit
) {
    val isActive = room.isAvailable ?: true
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) Color.White else Color(0xFFF9FAFB)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (!isActive) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Imagen con indicador de estado
            Box {
                AsyncImage(
                    model = room.resolvedImageUrl(),
                    contentDescription = room.title,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Badge de estado
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = if (isActive) Color(0xFF10B981) else Color(0xFFEF4444)
                ) {
                    Text(
                        text = if (isActive) "✓" else "✗",
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Información
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = room.title ?: "Sin título",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier.weight(1f),
                        color = if (isActive) Color.Black else Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = room.description ?: "",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${room.price?.toInt() ?: 0}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) Color(0xFF3B82F6) else Color.Gray
                    )
                    Text(
                        text = "/mes",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    
                    if (!isActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Inactivo",
                            fontSize = 10.sp,
                            color = Color.White,
                            modifier = Modifier
                                .background(Color(0xFFEF4444), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Botones de acción
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Botón editar
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Botón activar/desactivar
                IconButton(
                    onClick = onToggleActive,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isActive) Icons.Default.Close else Icons.Default.Check,
                        contentDescription = if (isActive) "Desactivar" else "Activar",
                        tint = if (isActive) Color(0xFFF59E0B) else Color(0xFF10B981),
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Botón eliminar
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
