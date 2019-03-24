package ru.geekbrains.geekkotlin.ui.splash

import ru.geekbrains.geekkotlin.data.NotesRepository
import ru.geekbrains.geekkotlin.data.errors.NoAuthException
import ru.geekbrains.geekkotlin.ui.base.BaseViewModel

class SplashViewModel(private val repository: NotesRepository) : BaseViewModel<Boolean?, SplashViewState>() {

    fun requestUser() {
        repository.getCurrentUser().observeForever {
            viewStateLiveData.value = if (it != null) {
                SplashViewState(isAuth = true)
            } else {
                SplashViewState(error = NoAuthException())
            }
        }
    }
}