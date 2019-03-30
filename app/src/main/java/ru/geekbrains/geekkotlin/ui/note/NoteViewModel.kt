package ru.geekbrains.geekkotlin.ui.note

import kotlinx.coroutines.launch
import ru.geekbrains.geekkotlin.data.NotesRepository
import ru.geekbrains.geekkotlin.data.entity.Note
import ru.geekbrains.geekkotlin.ui.base.BaseViewModel

class NoteViewModel(private val repository: NotesRepository) : BaseViewModel<NoteData>() {

    private var note: Note? = null

    fun save(note: Note) {
        this.note = note
    }

    fun loadNote(noteId: String) {
        launch {
            try {
                repository.getNoteById(noteId).let {
                    note = it
                    setData(NoteData(note = it))
                }
            } catch (e: Throwable) {
                setError(e)
            }
        }
    }

    fun deleteNote() {
        launch {
            try {
                note?.let { repository.deleteNote(it.id) }
                note = null
                setData(NoteData(isDeleted = true))
            } catch (e: Throwable) {
                setError(e)
            }
        }
    }

    override fun onCleared() {
        launch { note?.let { repository.saveNote(it) } }
    }
}
