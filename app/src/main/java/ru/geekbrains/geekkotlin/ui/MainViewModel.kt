package ru.geekbrains.geekkotlin.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import ru.geekbrains.geekkotlin.data.NotesRepository
import ru.geekbrains.geekkotlin.data.entity.Note

class MainViewModel : ViewModel() {

    private val viewStateLiveData: MutableLiveData<MainViewState> = MutableLiveData()
    val presenter: NotesRVPresenterContract = NotesRVPresenter()
        get() = field

    init {
        viewStateLiveData.value = MainViewState(NotesRepository.getNotes())
    }

    fun viewState(): LiveData<MainViewState> = viewStateLiveData

    class NotesRVPresenter : NotesRVPresenterContract {

        private var notes = NotesRepository.getNotes()

        override fun getItemCount(): Int {
            return notes.size
        }

        override fun bind(view: NotesRVView, position: Int) {
            view.setTitle(notes[position].title)
            view.setNoteText(notes[position].text)
            view.setBackgroundColor(notes[position].color)
        }

        override fun updateNotesList(notes: List<Note>) {
            this.notes = notes
        }
    }
}