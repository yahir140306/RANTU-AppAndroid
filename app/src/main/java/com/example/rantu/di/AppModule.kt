package com.example.rantu.di

import com.example.rantu.data.RoomRepository

/**
 * Contenedor manual de dependencias.
 * Reemplaza la necesidad de Dagger/Hilt para un proyecto de este tamaño,
 * garantizando que los ViewModels no instancien sus propias dependencias.
 */
object AppModule {
    val roomRepository: RoomRepository by lazy {
        RoomRepository()
    }
}
