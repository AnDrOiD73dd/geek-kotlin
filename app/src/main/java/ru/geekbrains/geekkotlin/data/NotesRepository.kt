package ru.geekbrains.geekkotlin.data

import ru.geekbrains.geekkotlin.data.entity.Note
import ru.geekbrains.geekkotlin.data.provider.FireStoreProvider
import ru.geekbrains.geekkotlin.data.provider.RemoteDataProvider

object NotesRepository {

    private val remoteDataProvider: RemoteDataProvider = FireStoreProvider()

    fun getNoteById(id: String) = remoteDataProvider.getNoteById(id)
    fun saveNote(note: Note) = remoteDataProvider.saveNote(note)
    fun getNotes() = remoteDataProvider.subscribeToAllNotes()

    fun getCurrentUser() = remoteDataProvider.getCurrentUser()
}