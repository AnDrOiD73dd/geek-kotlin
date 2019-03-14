package ru.geekbrains.geekkotlin.ui.main

import ru.geekbrains.geekkotlin.data.entity.Note
import ru.geekbrains.geekkotlin.ui.base.BaseViewState

class MainViewState(val notes: List<Note>? = null, error: Throwable? = null) : BaseViewState<List<Note>?>(notes, error)