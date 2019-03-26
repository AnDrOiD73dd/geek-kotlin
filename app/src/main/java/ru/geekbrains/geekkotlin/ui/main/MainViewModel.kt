package ru.geekbrains.geekkotlin.ui.main

import android.arch.lifecycle.Observer
import android.support.annotation.VisibleForTesting
import ru.geekbrains.geekkotlin.data.NotesRepository
import ru.geekbrains.geekkotlin.data.entity.Note
import ru.geekbrains.geekkotlin.model.NoteResult
import ru.geekbrains.geekkotlin.ui.base.BaseViewModel

class MainViewModel(private val repository: NotesRepository) :
        BaseViewModel<List<Note>?, MainViewState>() {

    private val notesObserver = Observer<NoteResult> { result ->
        result ?: let { return@Observer }

        when (result) {
            is NoteResult.Success<*> -> {
                viewStateLiveData.value = MainViewState(result.data as? List<Note>)
            }
            is NoteResult.Error -> {
                viewStateLiveData.value = MainViewState(error = result.error)
            }
        }
    }

    private val repositoryNotes = repository.getNotes()

    init {
        viewStateLiveData.value = MainViewState()
        repositoryNotes.observeForever(notesObserver)
    }

    @VisibleForTesting
    public override fun onCleared() {
        repositoryNotes.removeObserver(notesObserver)
    }
}