package com.example.rantu.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rantu.ui.AddRoomViewModel
import com.example.rantu.ui.EditRoomViewModel
import com.example.rantu.ui.RoomDetailViewModel
import com.example.rantu.ui.RoomViewModel

object ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RoomViewModel::class.java) -> {
                RoomViewModel(AppModule.roomRepository) as T
            }
            modelClass.isAssignableFrom(RoomDetailViewModel::class.java) -> {
                RoomDetailViewModel(AppModule.roomRepository) as T
            }
            modelClass.isAssignableFrom(AddRoomViewModel::class.java) -> {
                AddRoomViewModel(AppModule.roomRepository) as T
            }
            modelClass.isAssignableFrom(EditRoomViewModel::class.java) -> {
                EditRoomViewModel(AppModule.roomRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
