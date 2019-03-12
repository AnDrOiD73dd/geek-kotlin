package ru.geekbrains.geekkotlin.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.geekbrains.geekkotlin.R
import ru.geekbrains.geekkotlin.ui.note.NoteActivity

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel
    lateinit var adapter: NotesRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        rv_notes.layoutManager = GridLayoutManager(this, 2)
        adapter = NotesRVAdapter(viewModel.presenter) { NoteActivity.start(this, it) }
        rv_notes.adapter = adapter

        viewModel.viewState().observe(this, Observer<MainViewState> { state ->
            state?.let {
                viewModel.presenter.updateNotesList(it.notes)
                adapter.notifyDataSetChanged()
            }
        })

        fab.setOnClickListener {
            NoteActivity.start(this)
        }
    }

}
