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
import androidx.compose.material.icons.filled.Info
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
