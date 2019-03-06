package ru.geekbrains.geekkotlin.ui.note

import android.arch.lifecycle.ViewModel
import ru.geekbrains.geekkotlin.data.NotesRepository
import ru.geekbrains.geekkotlin.data.entity.Note

class NoteViewModel(private val repository: NotesRepository = NotesRepository) : ViewModel() {

    private var pendingNote: Note? = null

    fun save(note: Note) {
        pendingNote = note
    }

    override fun onCleared() {
        pendingNote?.let { repository.saveNote(it) }
    }
}
