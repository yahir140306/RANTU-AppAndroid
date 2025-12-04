package com.example.rantu.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: String,  // UUID from Supabase
    
    @SerialName("cuarto_id")
    val cuartoId: Int,
    
    @SerialName("user_id")
    val userId: String,
    
    @SerialName("comentario")
    val comentario: String,
    
    @SerialName("calificacion")
    val calificacion: Int, // 1-5 estrellas
    
    @SerialName("created_at")
    val createdAt: String
) {
    fun getUserInitial(): String {
        return userId.firstOrNull()?.uppercaseChar()?.toString() ?: "U"
    }
    
    fun getFormattedEmail(): String {
        return "usuario-${userId.take(8)}@example.com"
    }
}

@Serializable
data class CommentInsert(
    @SerialName("cuarto_id")
    val cuartoId: Int,
    
    @SerialName("user_id")
    val userId: String,
    
    @SerialName("comentario")
    val comentario: String,
    
    @SerialName("calificacion")
    val calificacion: Int
)

@Serializable
data class CommentStatistics(
    val total: Int,
    val promedio: Double
)

data class CommentsResponse(
    val comentarios: List<Comment>,
    val estadisticas: CommentStatistics
)
