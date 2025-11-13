package com.example.rantu.Components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Importación importante
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // <<-- SOLUCIÓN 1
import com.example.rantu.data.Room // Importa tu modelo de datos
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

    var showDetailScreen by remember { mutableStateOf(false) }

    if (showDetailScreen) {
        RoomDetailScreen(onBack = { showDetailScreen = false })
    } else {
        // <<-- SOLUCIÓN 2: Lógica de carga corregida
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
                        Button(onClick = { roomViewModel.fetchRooms() }, colors = ButtonDefaults.buttonColors()) {
                            Text("Reintentar")
                        }
                    }
                }
            } else {
                RoomListScreen(
                    rooms = rooms,
                    onRoomClick = { showDetailScreen = true }
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
            Button(onClick = onRetry, colors = ButtonDefaults.buttonColors()) {
                Text("Reintentar")
            }
        }
    }
}

// <<-- SOLUCIÓN 3: Firma de la función corregida
@Composable
fun RoomListScreen(rooms: List<Room>, onRoomClick: () -> Unit) {
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

            // <<-- SOLUCIÓN 3: Uso correcto de 'items' y parámetros
            items(rooms) { room ->
                RoomCard(
                    isAvailable = room.isAvailable,
                    imageUrl = room.resolvedImageUrl(),
                    title = room.title,
                    description = room.description,
                    price = "$${room.price.toInt()}",
                    onViewMoreClick = onRoomClick
                )
            }
        }
    }
}
