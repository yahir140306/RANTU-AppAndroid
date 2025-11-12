package com.example.rantu.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rantu.R // Asegúrate de tener la imagen de ejemplo

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ViewFirstPreview() {
    // Para la preview, usamos un tema oscuro simulado
    //Column(modifier = Modifier
    //    .fillMaxSize()
    //    .background(Color(0xFF1E1E1E))) {
        ViewFist()
   // }

}
@Composable
fun ViewFist() {
    // ---- Lógica de Navegación Simple ----
    var showDetailScreen by remember { mutableStateOf(false) }

    if (showDetailScreen) {
        // Si es verdadero, muestra la pantalla de detalles
        RoomDetailScreen(onBack = { showDetailScreen = false })
    } else {
        // Si es falso, muestra la lista de cuartos
        RoomListScreen(onRoomClick = { showDetailScreen = true })
    }
}

// Creamos un nuevo Composable para la lista para mantenerlo ordenado
@Composable
fun RoomListScreen(onRoomClick: () -> Unit) {
    // Scaffold nos da una estructura básica con TopBar
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

            // Modificamos RoomCard para aceptar una lambda onClick
            item {
                RoomCard(
                    isAvailable = true,
                    imageRes = R.drawable.cuarto_ejemplo,
                    title = "Cuarto para estudiantes",
                    description = "Este cuarto cuenta con baños para cada inquilino",
                    price = "$1,550",
                    onViewMoreClick = onRoomClick // Pasamos la acción
                )
            }

            item {
                RoomCard(
                    isAvailable = false,
                    imageRes = R.drawable.cuarto_ejemplo,
                    title = "Habitación individual amueblada",
                    description = "Ideal para profesionistas, cerca del centro.",
                    price = "$2,100",
                    onViewMoreClick = onRoomClick
                )
            }

            item {
                RoomCard(
                    isAvailable = false,
                    imageRes = R.drawable.cuarto_ejemplo,
                    title = "Habitación individual amueblada",
                    description = "Ideal para profesionistas, cerca del centro.",
                    price = "$2,100",
                    onViewMoreClick = onRoomClick
                )
            }

            item {
                RoomCard(
                    isAvailable = false,
                    imageRes = R.drawable.cuarto_ejemplo,
                    title = "Habitación individual amueblada",
                    description = "Ideal para profesionistas, cerca del centro.",
                    price = "$2,100",
                    onViewMoreClick = onRoomClick
                )
            }
        }
    }
}
