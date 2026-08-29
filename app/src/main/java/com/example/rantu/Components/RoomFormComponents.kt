package com.example.rantu.Components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomFormBasicInfo(
    titulo: String,
    onTituloChange: (String) -> Unit,
    descripcion: String,
    onDescripcionChange: (String) -> Unit,
    precio: String,
    onPrecioChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📋 Información Básica",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            OutlinedTextField(
                value = titulo,
                onValueChange = onTituloChange,
                label = { Text("Nombre del cuarto *") },
                placeholder = { Text("Ej: Cuarto luminoso cerca del centro") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = descripcion,
                onValueChange = onDescripcionChange,
                label = { Text("Descripción *") },
                placeholder = { Text("Describe las características, amenidades, ubicación...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = precio,
                onValueChange = onPrecioChange,
                label = { Text("Precio mensual *") },
                placeholder = { Text("5000.00") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                leadingIcon = { Text("$", fontWeight = FontWeight.Bold) },
                singleLine = true
            )
        }
    }
}
