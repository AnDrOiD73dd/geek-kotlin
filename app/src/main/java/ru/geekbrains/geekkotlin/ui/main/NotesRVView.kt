package ru.geekbrains.geekkotlin.ui.main

import ru.geekbrains.geekkotlin.data.entity.Note

interface NotesRVView {

    fun setTitle(title: String)

    fun setNoteText(text: String)

    fun setBackgroundColor(color: Note.Color)

    fun setOnClickListener(note: Note)
}