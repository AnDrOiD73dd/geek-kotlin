package ru.geekbrains.geekkotlin.data.provider

import android.arch.lifecycle.LiveData
import ru.geekbrains.geekkotlin.data.entity.Note
import ru.geekbrains.geekkotlin.data.entity.User
import ru.geekbrains.geekkotlin.model.NoteResult

interface RemoteDataProvider {
    fun getNoteById(id: String): LiveData<NoteResult>
    fun saveNote(note: Note): LiveData<NoteResult>
    fun subscribeToAllNotes(): LiveData<NoteResult>
    fun getCurrentUser(): LiveData<User?>
}