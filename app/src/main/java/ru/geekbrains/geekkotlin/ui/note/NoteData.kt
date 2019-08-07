package ru.geekbrains.geekkotlin.ui.note

import ru.geekbrains.geekkotlin.data.entity.Note

data class NoteData(val isDeleted: Boolean = false, val note: Note? = null)