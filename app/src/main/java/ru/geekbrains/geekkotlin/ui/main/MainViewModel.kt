package ru.geekbrains.geekkotlin.ui.main

import android.arch.lifecycle.Observer
import ru.geekbrains.geekkotlin.data.NotesRepository
import ru.geekbrains.geekkotlin.data.entity.Note
import ru.geekbrains.geekkotlin.model.NoteResult
import ru.geekbrains.geekkotlin.ui.base.BaseViewModel

class MainViewModel(private val repository: NotesRepository = NotesRepository) :
        BaseViewModel<List<Note>?, MainViewState>() {

    val presenter: NotesRVPresenterContract = NotesRVPresenter()
        get() = field

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

    override fun onCleared() {
        repositoryNotes.removeObserver(notesObserver)
    }

    inner class NotesRVPresenter : NotesRVPresenterContract {

        private var notes: List<Note> = listOf()

        override fun getItemCount(): Int {
            return notes.size
        }

        override fun bind(view: NotesRVView, position: Int) {
            val note = notes[position]
            view.setTitle(note.title)
            view.setNoteText(note.text)
            view.setBackgroundColor(note.color)
            view.setOnClickListener(note)
        }

        override fun updateNotesList(notes: List<Note>) {
            this.notes = notes
        }
    }
}