package com.example.rantu.Components

import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.rantu.R// Asegúrate de tener una imagen de ejemplo en res/drawable

// Componente para la Barra Superior
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    isLoggedIn: Boolean = false,
    userEmail: String? = null,
    onLoginClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Inicio",
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("RANTU", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        actions = {
            if (isLoggedIn) {
                // Mostrar email del usuario como botón clickeable
                if (userEmail != null) {
                    TextButton(onClick = onProfileClick) {
                        Text(
                            text = userEmail,
                            color = Color(0xFF3B82F6),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                // Botón de Salir
                TextButton(onClick = onLogoutClick) {
                    Text("Salir", color = Color.Black)
                }
            } else {
                // Si no está logueado, mostrar botón de Entrar
                Button(
                    onClick = onLoginClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Entrar", color = Color.White)
                }
            }
        },
       // colors = TopAppBarDefaults.topAppBarColors(
        //    containerColor = Color(0xFF1E1E1E) // Color de fondo oscuro
        //)
    )
}

// Componente para el filtro
@Composable
fun FilterBar(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    minPrice: String,
    maxPrice: String,
    soloDisponibles: Boolean = true,
    onMinPriceChange: (String) -> Unit,
    onMaxPriceChange: (String) -> Unit,
    onSoloDisponiblesChange: (Boolean) -> Unit = {},
    onApplyFilter: () -> Unit,
    onClearFilter: () -> Unit,
    isFilterActive: Boolean,
    filteredCount: Int,
    totalCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Botón de toggle
        OutlinedButton(
            onClick = onToggle,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isFilterActive) Color(0xFFEFF6FF) else Color.White
            ),
            border = BorderStroke(
                1.dp,
                if (isFilterActive) Color(0xFF3B82F6) else Color(0xFFD1D5DB)
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_filter),
                        contentDescription = "Filtrar",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Filtrar por Precio", color = Color.Black)
                    if (isFilterActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "(Activo)",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Colapsar" else "Expandir",
                    tint = Color.Black,
                    modifier = Modifier.then(
                        if (isExpanded) Modifier else Modifier
                    )
                )
            }
        }
        
        // Panel expandible
        androidx.compose.animation.AnimatedVisibility(
            visible = isExpanded,
            enter = androidx.compose.animation.expandVertically() + androidx.compose.animation.fadeIn(),
            exit = androidx.compose.animation.shrinkVertically() + androidx.compose.animation.fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        "Rango de Precios",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1F2937),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Campo Precio Mínimo
                    Text(
                        "Precio Mínimo",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = minPrice,
                        onValueChange = onMinPriceChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("0") },
                        leadingIcon = {
                            Text(
                                "$",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color(0xFFD1D5DB)
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Campo Precio Máximo
                    Text(
                        "Precio Máximo",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = maxPrice,
                        onValueChange = onMaxPriceChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Sin límite") },
                        leadingIcon = {
                            Text(
                                "$",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color(0xFFD1D5DB)
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Filtro de Disponibilidad
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Solo cuartos disponibles",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF374151)
                        )
                        Switch(
                            checked = soloDisponibles,
                            onCheckedChange = onSoloDisponiblesChange,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF10B981),
                                checkedTrackColor = Color(0xFF86EFAC),
                                uncheckedThumbColor = Color(0xFF9CA3AF),
                                uncheckedTrackColor = Color(0xFFE5E7EB)
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Botones de Acción
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Botón Limpiar
                        OutlinedButton(
                            onClick = onClearFilter,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFFF3F4F6)
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_filter),
                                    contentDescription = "Limpiar",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFF374151)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Limpiar", color = Color(0xFF374151))
                            }
                        }
                        
                        // Botón Aplicar
                        Button(
                            onClick = onApplyFilter,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3B82F6)
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_filter),
                                    contentDescription = "Aplicar",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Aplicar Filtro")
                            }
                        }
                    }
                    
                    // Mensaje de resultados
                    if (isFilterActive) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (filteredCount == 0) Color(0xFFFEF3C7) else Color(0xFFEFF6FF)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (filteredCount == 0) 
                                        Icons.Default.Check 
                                    else 
                                        Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (filteredCount == 0) Color(0xFF92400E) else Color(0xFF1E40AF)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (filteredCount == 0) {
                                        "No se encontraron cuartos en este rango"
                                    } else {
                                        "Mostrando $filteredCount de $totalCount cuartos"
                                    },
                                    fontSize = 12.sp,
                                    color = if (filteredCount == 0) Color(0xFF92400E) else Color(0xFF1E40AF)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Componente para cada tarjeta de cuarto
@Composable
fun RoomCard(
    isAvailable: Boolean,
    imageUrl: String,
    title: String,
    description: String,
    price: String,
    roomId: Int? = null,
    latitude: Double? = null,
    longitude: Double? = null,
    onViewMoreClick: () -> Unit,
    onShareClick: ((Int) -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        //colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C))
    ) {
        Column {
            Box {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )

                // Mostrar chip de estado (disponible o no disponible)
                StatusChip(
                    text = if (isAvailable) "Disponible" else "No disponible",
                    isAvailable = isAvailable
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, fontSize = 14.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(price, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6))
                        Text("por mes", fontSize = 12.sp, color = Color.Black)
                    }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Botón compartir (si está disponible)
                        if (onShareClick != null && roomId != null) {
                            IconButton(
                                onClick = { onShareClick(roomId) },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    painter = androidx.compose.ui.res.painterResource(
                                        id = android.R.drawable.ic_menu_share
                                    ),
                                    contentDescription = "Compartir",
                                    tint = Color(0xFF6B7280)
                                )
                            }
                        }
                        
                        // Botón Ver más
                        Button(
                            onClick = onViewMoreClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Ver más →")
                        }
                    }
                }
            }
        }
    }
}

// Vista previa del mapa con OSMDroid
@Composable
fun MapPreview(
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val position = com.google.android.gms.maps.model.LatLng(latitude, longitude)
        val cameraPositionState = com.google.maps.android.compose.rememberCameraPositionState {
            this.position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
                position,
                14f
            )
        }
        
        com.google.maps.android.compose.GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = com.google.maps.android.compose.MapUiSettings(
                zoomControlsEnabled = false,
                scrollGesturesEnabled = false,
                zoomGesturesEnabled = false,
                tiltGesturesEnabled = false,
                rotationGesturesEnabled = false
            )
        ) {
            com.google.maps.android.compose.Marker(
                state = com.google.maps.android.compose.rememberMarkerState(position = position)
            )
        }
        
        // Overlay semi-transparente para indicar que es una vista previa
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = Color.White.copy(alpha = 0.9f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Ver ubicación completa",
                        fontSize = 12.sp,
                        color = Color(0xFF3B82F6),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}


// Pequeño chip para el estado "Disponible" o "No disponible"
@Composable
fun StatusChip(text: String, isAvailable: Boolean = true) {
    Row(
        modifier = Modifier
            .padding(12.dp)
            .background(
                if (isAvailable) Color(0xFF16A34A) else Color(0xFFDC2626),
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isAvailable) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

