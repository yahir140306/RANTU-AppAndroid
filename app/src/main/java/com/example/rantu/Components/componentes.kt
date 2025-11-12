package com.example.rantu.Components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import com.example.rantu.R // Asegúrate de tener una imagen de ejemplo en res/drawable

// Componente para la Barra Superior
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
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
            IconButton(onClick = { /* TODO: Acción para el menú */ }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menú",
                    tint = Color.Black
                )
            }
        },
       // colors = TopAppBarDefaults.topAppBarColors(
        //    containerColor = Color(0xFF1E1E1E) // Color de fondo oscuro
        //)
    )
}

// Componente para el filtro
@Composable
fun FilterBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_filter), // Necesitarás un ícono de filtro
                contentDescription = "Filtrar",
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Filtrar por Precio", color = Color.Black)
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Desplegar",
            tint = Color.Black
        )
    }
}

// Componente para cada tarjeta de cuarto
@Composable
fun RoomCard(
    isAvailable: Boolean,
    imageRes: Int,
    title: String,
    description: String,
    price: String,
    onViewMoreClick: () -> Unit // <-- AÑADE ESTE PARÁMETRO
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
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )
                if (isAvailable) {
                    StatusChip(text = "Disponible")
                }
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
                    //if (isAvailable) {
                    //    StatusChip("Disponible")
                    //} else {
                    //    StatusChip("No disponible")
                    //}
                    // Modifica el Button
                    Button(
                        onClick = onViewMoreClick, // <-- USA LA LAMBDA AQUÍ
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


// Pequeño chip para el estado "Disponible"
@Composable
fun StatusChip(text: String) {
    Row(
        modifier = Modifier
            .padding(12.dp)
            .background(Color(0xFF16A34A), shape = RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

