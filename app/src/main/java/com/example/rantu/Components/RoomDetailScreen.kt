package com.example.rantu.Components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rantu.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoomDetailScreen(onBack: () -> Unit) {
    // Lista de imágenes de ejemplo para el carrusel
    val images = listOf(
        R.drawable.cuarto_ejemplo,
        R.drawable.cuarto_ejemplo, // Reemplaza con otras imágenes
        R.drawable.cuarto_ejemplo  // Reemplaza con otras imágenes
    )
    val pagerState = rememberPagerState(pageCount = { images.size })

    Scaffold(
        topBar = { TopBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()) // Para poder hacer scroll
        ) {
            // Botón de Volver
            TextButton(onClick = onBack, modifier = Modifier.padding(start = 16.dp, top = 8.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("Volver", color = MaterialTheme.colorScheme.primary)
            }

            // Título y Precio
            Text(
                text = "Cuarto para estudiantes",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                text = "$1,550 / mes",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Carrusel de Imágenes (Pager)
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentPadding = PaddingValues(horizontal = 16.dp), // Para que se vean un poco las imágenes de los lados
                pageSpacing = 16.dp,
            ) { page ->
                Image(
                    painter = painterResource(id = images[page]),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Indicadores de página (los puntitos)
            Row(
                Modifier
                    .height(30.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else Color.LightGray
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .size(10.dp)
                            .aspectRatio(1f)
                            .background(color)
                    )
                }
            }


            Spacer(Modifier.height(24.dp))

            // Sección de Descripción
            DetailSection(title = "Descripción", content = "Este cuarto cuenta con baños para cada inquilino.")
            Spacer(Modifier.height(16.dp))
            DetailSection(title = "Características", content = "• Amueblado\n• Wi-Fi\n• Baño privado")
        }
    }
}

// Componente reutilizable para las secciones de detalle
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
    RoomDetailScreen(onBack = {})
}
