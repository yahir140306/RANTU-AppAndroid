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
import com.example.rantu.di.ViewModelFactory
import com.example.rantu.data.Room
import com.example.rantu.ui.RoomViewModel
import java.net.URLEncoder
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable

fun ViewFist(
    roomViewModel: RoomViewModel = viewModel(factory = ViewModelFactory),
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
