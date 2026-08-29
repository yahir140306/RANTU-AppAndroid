package com.example.rantu.ui

import com.example.rantu.data.Comment
import com.example.rantu.data.CommentResponse
import com.example.rantu.data.CommentStatistics
import com.example.rantu.data.RoomRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RoomDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit val repository: RoomRepository
    private lateinit val viewModel: RoomDetailViewModel

    @Before
    fun setUp() {
        // Necesario para testear corrutinas de ViewModels en JUnit local
        Dispatchers.setMain(testDispatcher)
        
        // Creamos un "Mock" (Simulador) del Repositorio de Supabase
        repository = mockk()
        
        // Inyectamos el repositorio simulado al ViewModel
        viewModel = RoomDetailViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadComments cuando es exitoso, actualiza el estado de comentarios y limpia errores`() = runTest {
        // Arrange (Preparar)
        val mockComments = listOf(
            Comment(id = 1, room_id = 10, user_id = "u1", comentario = "Excelente", calificacion = 5, created_at = "2024-01-01")
        )
        val mockStats = CommentStatistics(promedio = 5.0, total = 1)
        val mockResponse = CommentResponse(mockComments, mockStats)
        
        // Configuramos el mock para que responda con éxito
        coEvery { repository.getComments(10) } returns mockResponse

        // Act (Actuar)
        viewModel.loadComments(10)
        
        // Avanzamos el tiempo virtual de la corrutina
        testScheduler.advanceUntilIdle()

        // Assert (Verificar)
        assertEquals(false, viewModel.isLoading.value)
        assertNull(viewModel.error.value)
        assertEquals(1, viewModel.comments.value.size)
        assertEquals("Excelente", viewModel.comments.value[0].comentario)
        assertEquals(5.0, viewModel.statistics.value?.promedio)
    }

    @Test
    fun `loadComments cuando falla, muestra mensaje de error y limpia estado de carga`() = runTest {
        // Arrange
        coEvery { repository.getComments(10) } throws Exception("Error de red")

        // Act
        viewModel.loadComments(10)
        testScheduler.advanceUntilIdle()

        // Assert
        assertEquals(false, viewModel.isLoading.value)
        assertEquals("Error de red", viewModel.error.value)
        assertEquals(0, viewModel.comments.value.size)
    }
}
