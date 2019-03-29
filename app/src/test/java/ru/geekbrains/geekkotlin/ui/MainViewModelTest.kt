package ru.geekbrains.geekkotlin.ui

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.geekbrains.geekkotlin.data.NotesRepository
import ru.geekbrains.geekkotlin.data.entity.Note
import ru.geekbrains.geekkotlin.model.NoteResult
import ru.geekbrains.geekkotlin.ui.main.MainViewModel

class MainViewModelTest {

    @get :Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val mockRepository: NotesRepository = mockk()
    private val notesLiveData = MutableLiveData<NoteResult>()
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        every { mockRepository.getNotes() } returns notesLiveData
        viewModel = MainViewModel(mockRepository)
    }

    @Test
    fun `should call getNotes once`() {
        verify(exactly = 1) { mockRepository.getNotes() }
    }

    @Test
    fun `should return error`() {
        var result: Throwable? = null
        val testData = Throwable("error")
        viewModel.getViewState().observeForever { result = it?.error }
        notesLiveData.value = NoteResult.Error(testData)
        assertEquals(result, testData)
    }

    @Test
    fun `should return Notes`() {
        var result: List<Note>? = null
        val testData = listOf(Note(id = "1"), Note(id = "2"), Note(id = "3"))
        viewModel.getViewState().observeForever { result = it?.data }
        notesLiveData.value = NoteResult.Success(data = testData)
        assertEquals(result, testData)
    }

    @Test
    fun `should remove observer`() {
        viewModel.onCleared()
        assertFalse(viewModel.getViewState().hasObservers())
    }
}