package ru.geekbrains.geekkotlin.data.provider

import kotlinx.coroutines.channels.ReceiveChannel
import ru.geekbrains.geekkotlin.data.entity.Note
import ru.geekbrains.geekkotlin.data.entity.User
import ru.geekbrains.geekkotlin.model.NoteResult

interface RemoteDataProvider {
    suspend fun saveNote(note: Note): Note
    suspend fun deleteNote(noteId: String)
    suspend fun getNoteById(id: String): Note
    fun subscribeToAllNotes(): ReceiveChannel<NoteResult>
    suspend fun getCurrentUser(): User?
}