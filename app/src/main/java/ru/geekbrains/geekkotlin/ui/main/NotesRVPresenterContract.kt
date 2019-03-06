package ru.geekbrains.geekkotlin.ui.main

import ru.geekbrains.geekkotlin.data.entity.Note

interface NotesRVPresenterContract {

    fun getItemCount(): Int

    fun bind(view: NotesRVView, position: Int)

    fun updateNotesList(notes: List<Note>)
}