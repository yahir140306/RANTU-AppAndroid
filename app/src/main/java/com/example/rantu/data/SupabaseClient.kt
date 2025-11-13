package com.example.rantu.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// Cliente Ktor simple para usar PostgREST de Supabase sin depender de la librería supabase-kt.
object SupabaseClient {
    // Reemplaza con tus propias claves de Supabase
    const val SUPABASE_URL = "https://hkwvtpexiobfectukjmx.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imhrd3Z0cGV4aW9iZmVjdHVram14Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDkyNjY4OTYsImV4cCI6MjA2NDg0Mjg5Nn0.tCAgqdQUVUs3SP4w7tEkXdZXJHmdcad42ORqZ0rVKOE"

    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }

        // Añade las cabeceras de Supabase por defecto (apikey y Authorization)
        defaultRequest {
            header("apikey", SUPABASE_KEY)
            header("Authorization", "Bearer $SUPABASE_KEY")
        }
    }

    // URL base para las llamadas a PostgREST
    val postgrestUrl: String = "${SUPABASE_URL}/rest/v1"
}
