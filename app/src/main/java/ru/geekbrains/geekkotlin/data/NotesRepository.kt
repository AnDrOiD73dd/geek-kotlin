package ru.geekbrains.geekkotlin.data

import ru.geekbrains.geekkotlin.data.entity.Note
import ru.geekbrains.geekkotlin.data.provider.RemoteDataProvider

class NotesRepository(val remoteDataProvider: RemoteDataProvider) {

    suspend fun getCurrentUser() = remoteDataProvider.getCurrentUser()
    suspend fun saveNote(note: Note) = remoteDataProvider.saveNote(note)
    suspend fun deleteNote(noteId: String) = remoteDataProvider.deleteNote(noteId)
    suspend fun getNoteById(id: String) = remoteDataProvider.getNoteById(id)
    fun getNotes() = remoteDataProvider.subscribeToAllNotes()
}