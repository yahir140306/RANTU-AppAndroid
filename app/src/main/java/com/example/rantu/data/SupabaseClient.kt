package com.example.rantu.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    const val SUPABASE_URL = "https://hkwvtpexiobfectukjmx.supabase.co"
    const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imhrd3Z0cGV4aW9iZmVjdHVram14Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDkyNjY4OTYsImV4cCI6MjA2NDg0Mjg5Nn0.tCAgqdQUVUs3SP4w7tEkXdZXJHmdcad42ORqZ0rVKOE"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Postgrest)
        install(Auth) {
            scheme = "rantu"
            host = "login"
        }
    }
}
