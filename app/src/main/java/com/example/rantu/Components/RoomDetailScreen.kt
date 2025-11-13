package com.example.rantu.Components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.rantu.data.Room
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RoomDetailScreen(room: Room, onBack: () -> Unit) {
    Scaffold(
        topBar = { TopBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Botón de Volver
            TextButton(onClick = onBack, modifier = Modifier.padding(start = 16.dp, top = 8.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("Volver", color = MaterialTheme.colorScheme.primary)
            }

            // Imagen principal
            AsyncImage(
                model = room.resolvedImageUrl(),
                contentDescription = room.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(16.dp))

            // Título y Precio
            Text(
                text = room.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                text = "\$${room.price.toInt()} / mes",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Sección de Descripción
            DetailSection(title = "Descripción", content = if (room.description.isNotBlank()) room.description else "Sin descripción disponible.")
            Spacer(Modifier.height(16.dp))
            DetailSection(title = "Estado", content = if (room.isAvailable) "Disponible" else "No disponible")
        }
    }
}

@Composable
fun DetailSection(title: String, content: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(content, modifier = Modifier.padding(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RoomDetailScreenPreview() {
    val sample = Room(id = 1, title = "Cuarto ejemplo", description = "Descripción del cuarto", price = 1550.0)
    RoomDetailScreen(room = sample, onBack = {})
}
