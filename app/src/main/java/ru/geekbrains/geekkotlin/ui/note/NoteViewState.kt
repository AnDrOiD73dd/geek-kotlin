package ru.geekbrains.geekkotlin.ui.note

import ru.geekbrains.geekkotlin.data.entity.Note
import ru.geekbrains.geekkotlin.ui.base.BaseViewState

class NoteViewState(date: Data = Data(), error: Throwable? = null) : BaseViewState<NoteViewState.Data>(date, error){
    data class Data(val isDeleted: Boolean = false, val note: Note? = null)
}
