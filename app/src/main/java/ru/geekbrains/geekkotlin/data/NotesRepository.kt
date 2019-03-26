package ru.geekbrains.geekkotlin.data

import ru.geekbrains.geekkotlin.data.entity.Note
import ru.geekbrains.geekkotlin.data.provider.RemoteDataProvider

class NotesRepository(val remoteDataProvider: RemoteDataProvider) {

    fun getCurrentUser() = remoteDataProvider.getCurrentUser()
    fun saveNote(note: Note) = remoteDataProvider.saveNote(note)
    fun deleteNote(noteId: String) = remoteDataProvider.deleteNote(noteId)
    fun getNoteById(id: String) = remoteDataProvider.getNoteById(id)
    fun getNotes() = remoteDataProvider.subscribeToAllNotes()
}