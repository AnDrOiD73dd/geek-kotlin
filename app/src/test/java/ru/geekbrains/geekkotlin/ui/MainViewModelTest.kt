package ru.geekbrains.geekkotlin.ui

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.geekbrains.geekkotlin.data.NotesRepository
import ru.geekbrains.geekkotlin.data.entity.Note
import ru.geekbrains.geekkotlin.model.NoteResult
import ru.geekbrains.geekkotlin.ui.main.MainViewModel

class MainViewModelTest {

    private val mockRepository = mockk<NotesRepository>()
    private val notesReceiveChannel = Channel<NoteResult>(Channel.CONFLATED)
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        clearMocks(mockRepository)
        every { mockRepository.getNotes() } returns notesReceiveChannel
        viewModel = MainViewModel(mockRepository)
    }

    @Test
    fun `should call getNotes once`() {
        verify(exactly = 1) { mockRepository.getNotes() }
    }

    @Test
    fun `should return Notes`() {
        val testData = listOf(Note("1"), Note("2"))
        runBlocking {
            notesReceiveChannel.send(NoteResult.Success(testData))
            val result = viewModel.getViewState().receive()
            assertEquals(testData, result)
        }
    }

    @Test
    fun `should return error`() {
        val testData = Throwable("error")
        runBlocking {
            notesReceiveChannel.send(NoteResult.Error(testData))
            val result = viewModel.getErrorChannel().receive()
            assertEquals(testData, result)
        }
    }

    @Test
    fun `should cancel channel`(){
        viewModel.onCleared()
        assertTrue(notesReceiveChannel.isClosedForReceive)
    }
}