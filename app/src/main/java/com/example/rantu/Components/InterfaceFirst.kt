package com.example.rantu.Components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rantu.data.Room
import com.example.rantu.ui.RoomViewModel
import java.net.URLEncoder
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

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
    onLogoutClick: () -> Unit = {},
    deepLinkRoomId: Int? = null,
    onDeepLinkHandled: () -> Unit = {},
    isDarkMode: Boolean = false,
    onThemeToggle: () -> Unit = {}
) {
    val rooms = roomViewModel.rooms.value
    val isLoading = roomViewModel.isLoading.value
    val errorMsg = roomViewModel.error.value

    // El cuarto actualmente seleccionado (null = lista)
    var selectedRoom by remember { mutableStateOf<Room?>(null) }
    
    // Estado para mostrar "Mis Cuartos"
    var showMyRooms by remember { mutableStateOf(false) }
    
    // Estado para mostrar "Agregar Cuarto"
    var showAddRoom by remember { mutableStateOf(false) }
    
    // Estado para mostrar "Editar Cuarto"
    var roomToEdit by remember { mutableStateOf<Room?>(null) }
    
    // Estado para Location Picker desde AddRoom
    var showLocationPicker by remember { mutableStateOf(false) }
    var locationPickerInitialLat by remember { mutableStateOf<Double?>(null) }
    var locationPickerInitialLng by remember { mutableStateOf<Double?>(null) }
    var onLocationSelected by remember { mutableStateOf<((Double, Double) -> Unit)?>(null) }
    
    // Manejar deep link
    LaunchedEffect(deepLinkRoomId) {
        if (deepLinkRoomId != null) {
            // Buscar el cuarto por ID
            val room = rooms.find { it.id == deepLinkRoomId }
            if (room != null) {
                selectedRoom = room
            }
            onDeepLinkHandled()
        }
    }

    when {
        showLocationPicker -> {
            BackHandler { 
                showLocationPicker = false
                onLocationSelected = null
            }
            com.example.rantu.ui.screens.LocationPickerScreen(
                initialLatitude = locationPickerInitialLat,
                initialLongitude = locationPickerInitialLng,
                onLocationSelected = { lat, lng ->
                    onLocationSelected?.invoke(lat, lng)
                    showLocationPicker = false
                    onLocationSelected = null
                },
                onNavigateBack = { 
                    showLocationPicker = false
                    onLocationSelected = null
                }
            )
        }
        showAddRoom -> {
            BackHandler { showAddRoom = false }
            // Mostrar pantalla de Agregar Cuarto
            AddRoomScreen(
                onBack = { showAddRoom = false },
                onSuccess = {
                    showAddRoom = false
                    showMyRooms = true
                    // Recargar cuartos
                    roomViewModel.fetchRooms()
                },
                onOpenLocationPicker = { initialLat, initialLng, callback ->
                    locationPickerInitialLat = initialLat
                    locationPickerInitialLng = initialLng
                    onLocationSelected = callback
                    showLocationPicker = true
                }
            )
        }
        roomToEdit != null -> {
            BackHandler { roomToEdit = null }
            // Mostrar pantalla de Editar Cuarto
            EditRoomScreen(
                room = roomToEdit!!,
                onBack = { roomToEdit = null },
                onSuccess = {
                    roomToEdit = null
                    showMyRooms = true
                    // Recargar cuartos
                    roomViewModel.fetchRooms()
                },
                onOpenLocationPicker = { initialLat, initialLng, callback ->
                    locationPickerInitialLat = initialLat
                    locationPickerInitialLng = initialLng
                    onLocationSelected = callback
                    showLocationPicker = true
                }
            )
        }
        showMyRooms -> {
            BackHandler { showMyRooms = false }
            // Mostrar pantalla de Mis Cuartos
            MyRoomsScreen(
                onBack = { showMyRooms = false },
                onAddRoom = { showAddRoom = true },
                onEditRoom = { room -> roomToEdit = room },
                onRoomUpdated = {
                    // Recargar la lista principal cuando se actualiza un cuarto
                    roomViewModel.fetchRooms()
                }
            )
        }
        selectedRoom != null -> {
            BackHandler { selectedRoom = null }
            // Mostrar pantalla de detalle para el cuarto seleccionado
            RoomDetailScreen(room = selectedRoom!!, onBack = { selectedRoom = null })
        }
        else -> {
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
    val context = LocalContext.current
    var isFilterExpanded by remember { mutableStateOf(false) }
    var isMapMode by remember { mutableStateOf(false) }
    
    val firstLocation = rooms.firstOrNull { it.latitud != null && it.longitud != null }
    val defaultLocation = LatLng(firstLocation?.latitud ?: 23.6345, firstLocation?.longitud ?: -102.5528)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, if (firstLocation != null) 12f else 5f)
    }
    
    Scaffold(
        topBar = {
            TopBar(
                isLoggedIn = isLoggedIn,
                userEmail = userEmail,
                onLoginClick = onLoginClick,
                onLogoutClick = onLogoutClick,
                onProfileClick = onProfileClick,
                isDarkMode = isDarkMode,
                onThemeToggle = onThemeToggle
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cuartos Disponibles",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { isMapMode = !isMapMode },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                ) {
                    Text(if (isMapMode) "Ver en Lista" else "Ver Mapa")
                }
            }
            
            FilterBar(
                isExpanded = isFilterExpanded,
                onToggle = { isFilterExpanded = !isFilterExpanded },
                minPrice = roomViewModel.minPrice.value,
                maxPrice = roomViewModel.maxPrice.value,
                soloDisponibles = roomViewModel.soloDisponibles.value,
                onMinPriceChange = { value ->
                    roomViewModel.updateMinPrice(value)
                },
                onMaxPriceChange = { value ->
                    roomViewModel.updateMaxPrice(value)
                },
                onSoloDisponiblesChange = { value ->
                    roomViewModel.soloDisponibles.value = value
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

            if (isMapMode) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = com.google.maps.android.compose.MapProperties(isMyLocationEnabled = false),
                    uiSettings = com.google.maps.android.compose.MapUiSettings(zoomControlsEnabled = true)
                ) {
                    rooms.forEach { room ->
                        if (room.latitud != null && room.longitud != null) {
                            val markerState = rememberMarkerState(position = LatLng(room.latitud, room.longitud))
                            MarkerInfoWindow(
                                state = markerState,
                                title = room.title ?: "Cuarto",
                                snippet = "$${room.price?.toInt() ?: 0}/mes",
                                onInfoWindowClick = { onRoomClick(room) }
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(rooms) { room ->
                        RoomCard(
                            isAvailable = room.isAvailable ?: false,
                            imageUrl = room.resolvedImageUrl(),
                            title = room.title ?: "Sin título",
                            description = room.description ?: "Sin descripción",
                            price = "$${room.price?.toInt() ?: 0}",
                            roomId = room.id,
                            latitude = room.latitud,
                            longitude = room.longitud,
                            onViewMoreClick = { onRoomClick(room) },
                            onShareClick = { roomId ->
                                val webUrl = "https://prototype-delta-vert.vercel.app/cuarto/$roomId"
                                val deepLinkUrl = "rantu://cuarto/$roomId"
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
                            }
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
    }
}

