package com.example.rantu.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerScreen(
    initialLatitude: Double? = null,
    initialLongitude: Double? = null,
    onLocationSelected: (Double, Double) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    
    // Estado del marcador seleccionado
    var selectedLocation by remember { 
        mutableStateOf(
            if (initialLatitude != null && initialLongitude != null) {
                LatLng(initialLatitude, initialLongitude)
            } else {
                null // Sin ubicación por defecto, esperamos GPS
            }
        )
    }
    
    // Estado de si ya se obtuvo la ubicación inicial
    var initialLocationLoaded by remember { mutableStateOf(initialLatitude != null && initialLongitude != null) }
    
    // Estado del mapa
    val cameraPositionState = rememberCameraPositionState {
        if (selectedLocation != null) {
            position = CameraPosition.fromLatLngZoom(selectedLocation!!, 15f)
        }
    }
    
    // Estado de permisos
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    // Obtener ubicación automáticamente al cargar
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission && !initialLocationLoaded) {
            getCurrentLocation(context) { lat, lng ->
                selectedLocation = LatLng(lat, lng)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(lat, lng), 15f)
                initialLocationLoaded = true
            }
        }
    }
    
    // Solicitud de permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        if (isGranted) {
            getCurrentLocation(context) { lat, lng ->
                selectedLocation = LatLng(lat, lng)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(selectedLocation!!, 15f)
            }
        }
    }
    
    // Solicitar permiso automáticamente si no lo tiene
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seleccionar Ubicación") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            selectedLocation?.let { location ->
                                onLocationSelected(location.latitude, location.longitude)
                                onNavigateBack()
                            }
                        },
                        enabled = selectedLocation != null
                    ) {
                        Icon(Icons.Default.Check, "Confirmar ubicación")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (hasLocationPermission) {
                        getCurrentLocation(context) { lat, lng ->
                            val newLocation = LatLng(lat, lng)
                            selectedLocation = newLocation
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(newLocation, 15f)
                        }
                    } else {
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }
            ) {
                Icon(Icons.Default.LocationOn, "Mi ubicación")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Información de coordenadas
            if (selectedLocation != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Coordenadas seleccionadas:",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Latitud: ${"%.6f".format(selectedLocation!!.latitude)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Longitud: ${"%.6f".format(selectedLocation!!.longitude)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Obteniendo tu ubicación...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            // Mapa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (selectedLocation != null) {
                    val markerState = rememberMarkerState(position = selectedLocation!!)
                    
                    // Actualizar selectedLocation cuando se arrastra el marcador
                    LaunchedEffect(markerState.position) {
                        if (markerState.position != selectedLocation) {
                            selectedLocation = markerState.position
                        }
                    }
                    
                    // Actualizar marcador cuando selectedLocation cambia (por click en mapa)
                    LaunchedEffect(selectedLocation) {
                        if (selectedLocation != null && markerState.position != selectedLocation) {
                            markerState.position = selectedLocation!!
                        }
                    }
                    
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        onMapClick = { latLng ->
                            selectedLocation = latLng
                        },
                        properties = MapProperties(
                            isMyLocationEnabled = hasLocationPermission
                        ),
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            myLocationButtonEnabled = false
                        )
                    ) {
                        Marker(
                            state = markerState,
                            title = "Ubicación del cuarto",
                            draggable = true
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                // Instrucciones
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(
                        "Toca el mapa o arrastra el marcador para seleccionar la ubicación",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

private fun getCurrentLocation(
    context: android.content.Context,
    onLocationReceived: (Double, Double) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    
    try {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                onLocationReceived(it.latitude, it.longitude)
            }
        }
    } catch (e: SecurityException) {
        println("[LocationPicker] Error getting location: ${e.message}")
    }
}
