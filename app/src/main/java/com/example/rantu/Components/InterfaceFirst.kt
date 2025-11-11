package com.example.rantu.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    // Scaffold nos da una estructura básica con TopBar
    Scaffold(
        topBar = { TopBar() },
        //containerColor = Color(0xFF1E1E1E) // Fondo oscuro para toda la pantalla
    ) { innerPadding ->
        // LazyColumn es eficiente para listas que pueden crecer
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Padding para no solaparse con la TopBar
        ) {
            // Título "Cuartos Disponibles"
            item {
                Text(
                    text = "Cuartos Disponibles",
                    //color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Barra de Filtro
            item {
                FilterBar()
            }

            // Tarjeta de Cuarto 1
            item {
                RoomCard(
                    isAvailable = true,
                    imageRes = R.drawable.cuarto_ejemplo, // Tu imagen de ejemplo
                    title = "Cuarto para estudiantes",
                    description = "Este cuarto cuenta con baños para cada inquilino",
                    price = "$1,550"
                )
            }

            // Tarjeta de Cuarto 2 (ejemplo adicional)
            item {
                RoomCard(
                    isAvailable = false, // Para mostrar cómo se ve sin el chip
                    imageRes = R.drawable.cuarto_ejemplo,
                    title = "Habitación individual amueblada",
                    description = "Ideal para profesionistas, cerca del centro.",
                    price = "$2,100"
                )
            }

            item {
                RoomCard(
                    isAvailable = false, // Para mostrar cómo se ve sin el chip
                    imageRes = R.drawable.cuarto_ejemplo,
                    title = "Habitación individual amueblada",
                    description = "Ideal para profesionistas, cerca del centro.",
                    price = "$2,100"
                )
            }

            item {
                RoomCard(
                    isAvailable = false, // Para mostrar cómo se ve sin el chip
                    imageRes = R.drawable.cuarto_ejemplo,
                    title = "Habitación individual amueblada",
                    description = "Ideal para profesionistas, cerca del centro.",
                    price = "$2,100"
                )
            }
        }
    }
}
