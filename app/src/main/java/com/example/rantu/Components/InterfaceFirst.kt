package com.example.rantu.Components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rantu.data.Room
import com.example.rantu.ui.RoomViewModel

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ViewFirstPreview() {
    ViewFist()
}

@Composable
fun ViewFist(roomViewModel: RoomViewModel = viewModel()) {
    val rooms = roomViewModel.rooms.value
    val isLoading = roomViewModel.isLoading.value
    val errorMsg = roomViewModel.error.value

    // El cuarto actualmente seleccionado (null = lista)
    var selectedRoom by remember { mutableStateOf<Room?>(null) }

    if (selectedRoom != null) {
        // Mostrar pantalla de detalle para el cuarto seleccionado
        RoomDetailScreen(room = selectedRoom!!, onBack = { selectedRoom = null })
    } else {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Si hay error, mostrar pantalla de error con opción de reintentar
            if (errorMsg != null) {
                ErrorScreen(message = errorMsg, onRetry = { roomViewModel.fetchRooms() })
            } else if (rooms.isEmpty()) {
                // Lista vacía pero sin error -> informar que no hay cuartos publicados
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No hay cuartos disponibles.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Razones comunes: no hay publicaciones, falla de sincronización o permisos.")
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { roomViewModel.fetchRooms() }) {
                            Text("Reintentar")
                        }
                    }
                }
            } else {
                RoomListScreen(
                    rooms = rooms,
                    onRoomClick = { room -> selectedRoom = room }
                )
            }
        }
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Error al cargar cuartos:")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = message, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onRetry) {
                Text("Reintentar")
            }
        }
    }
}

@Composable
fun RoomListScreen(rooms: List<Room>, onRoomClick: (Room) -> Unit) {
    Scaffold(
        topBar = { TopBar() },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Text(
                    text = "Cuartos Disponibles",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
            item { FilterBar() }

            items(rooms) { room ->
                RoomCard(
                    isAvailable = room.isAvailable,
                    imageUrl = room.resolvedImageUrl(),
                    title = room.title,
                    description = room.description,
                    price = "$${room.price.toInt()}",
                    onViewMoreClick = { onRoomClick(room) }
                )
            }
        }
    }
}

