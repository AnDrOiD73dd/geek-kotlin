package ru.geekbrains.geekkotlin.ui.note

import ru.geekbrains.geekkotlin.data.NotesRepository
import ru.geekbrains.geekkotlin.data.entity.Note
import ru.geekbrains.geekkotlin.model.NoteResult
import ru.geekbrains.geekkotlin.ui.base.BaseViewModel

class NoteViewModel(private val repository: NotesRepository = NotesRepository) : BaseViewModel<Note?, NoteViewState>() {

    private var pendingNote: Note? = null

    fun save(note: Note) {
        pendingNote = note
    }

    override fun onCleared() {
        pendingNote?.let { repository.saveNote(it) }
    }

    fun loadNote(noteId: String) {
        repository.getNoteById(noteId).observeForever { result ->
            result ?: let { return@observeForever }
            when (result) {
                is NoteResult.Success<*> -> {
                    viewStateLiveData.value = NoteViewState(result.data as? Note)
                }
                is NoteResult.Error -> {
                    viewStateLiveData.value = NoteViewState(error = result.error)
                }
            }
        }
    }
}
