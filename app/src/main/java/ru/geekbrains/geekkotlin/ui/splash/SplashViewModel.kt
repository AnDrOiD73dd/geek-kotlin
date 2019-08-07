package ru.geekbrains.geekkotlin.ui.splash

import kotlinx.coroutines.launch
import ru.geekbrains.geekkotlin.data.NotesRepository
import ru.geekbrains.geekkotlin.data.errors.NoAuthException
import ru.geekbrains.geekkotlin.ui.base.BaseViewModel

class SplashViewModel(private val repository: NotesRepository) : BaseViewModel<Boolean?>() {

    fun requestUser() {
        launch {
            repository.getCurrentUser()?.let {
                setData(true)
            } ?: setError(NoAuthException())
        }
    }
}