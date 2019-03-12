package ru.geekbrains.geekkotlin.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import ru.geekbrains.geekkotlin.data.NotesRepository
import ru.geekbrains.geekkotlin.data.entity.Note

class MainViewModel(private val repository: NotesRepository = NotesRepository) : ViewModel() {

    private val viewStateLiveData: MutableLiveData<MainViewState> = MutableLiveData()
    val presenter: NotesRVPresenterContract = NotesRVPresenter()
        get() = field

    init {
        repository.getNotes().observeForever { notes ->
            viewStateLiveData.value = viewStateLiveData.value?.copy(notes = notes!!)
                    ?: MainViewState(notes!!)
        }
    }

    fun viewState(): LiveData<MainViewState> = viewStateLiveData

    inner class NotesRVPresenter : NotesRVPresenterContract {

        private var notes = NotesRepository.getNotes().value

        override fun getItemCount(): Int {
            return notes?.size ?: 0
        }

        override fun bind(view: NotesRVView, position: Int) {
            val note = notes!![position]
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