package com.example.rantu.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun ViewFist(
    roomViewModel: RoomViewModel = viewModel(),
    isLoggedIn: Boolean = false,
    userEmail: String? = null,
    onLoginClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val rooms = roomViewModel.rooms.value
    val isLoading = roomViewModel.isLoading.value
    val errorMsg = roomViewModel.error.value

    // El cuarto actualmente seleccionado (null = lista)
    var selectedRoom by remember { mutableStateOf<Room?>(null) }
    
    // Estado para mostrar "Mis Cuartos"
    var showMyRooms by remember { mutableStateOf(false) }

    if (showMyRooms) {
        // Mostrar pantalla de Mis Cuartos
        MyRoomsScreen(
            onBack = { showMyRooms = false }
        )
    } else if (selectedRoom != null) {
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
                    roomViewModel = roomViewModel,
                    onRoomClick = { room -> selectedRoom = room },
                    isLoggedIn = isLoggedIn,
                    userEmail = userEmail,
                    onLoginClick = onLoginClick,
                    onLogoutClick = onLogoutClick,
                    onProfileClick = { showMyRooms = true }
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
fun RoomListScreen(
    rooms: List<Room>,
    roomViewModel: RoomViewModel,
    onRoomClick: (Room) -> Unit,
    isLoggedIn: Boolean,
    userEmail: String? = null,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onProfileClick: () -> Unit = {}
) {
    var isFilterExpanded by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopBar(
                isLoggedIn = isLoggedIn,
                userEmail = userEmail,
                onLoginClick = onLoginClick,
                onLogoutClick = onLogoutClick,
                onProfileClick = onProfileClick
            )
        },
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
            
            item {
                FilterBar(
                    isExpanded = isFilterExpanded,
                    onToggle = { isFilterExpanded = !isFilterExpanded },
                    minPrice = roomViewModel.minPrice.value,
                    maxPrice = roomViewModel.maxPrice.value,
                    onMinPriceChange = { value ->
                        roomViewModel.updateMinPrice(value)
                    },
                    onMaxPriceChange = { value ->
                        roomViewModel.updateMaxPrice(value)
                    },
                    onApplyFilter = {
                        roomViewModel.applyFilter()
                        isFilterExpanded = false
                    },
                    onClearFilter = {
                        roomViewModel.clearFilter()
                        isFilterExpanded = false
                    },
                    isFilterActive = roomViewModel.isFilterActive.value,
                    filteredCount = rooms.size,
                    totalCount = roomViewModel.totalRoomsCount
                )
            }

            items(rooms) { room ->
                RoomCard(
                    isAvailable = room.isAvailable ?: false,
                    imageUrl = room.resolvedImageUrl(),
                    title = room.title ?: "Sin título",
                    description = room.description ?: "Sin descripción",
                    price = "$${room.price?.toInt() ?: 0}",
                    onViewMoreClick = { onRoomClick(room) }
                )
            }
            
            // Mensaje cuando no hay resultados
            if (rooms.isEmpty() && roomViewModel.isFilterActive.value) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFEF3C7)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(
                                        Color(0xFFFDE68A),
                                        shape = RoundedCornerShape(50)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("⚠️", fontSize = 32.sp)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No se encontraron cuartos",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF92400E)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No hay cuartos disponibles en el rango de precios seleccionado.",
                                fontSize = 14.sp,
                                color = Color(0xFF92400E),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    roomViewModel.clearFilter()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD97706)
                                )
                            ) {
                                Text("Ver todos los cuartos")
                            }
                        }
                    }
                }
            }
        }
    }
}

