package ru.geekbrains.geekkotlin.ui.note

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import kotlinx.android.synthetic.main.activity_note.*
import ru.geekbrains.geekkotlin.R
import ru.geekbrains.geekkotlin.common.format
import ru.geekbrains.geekkotlin.common.getColorInt
import ru.geekbrains.geekkotlin.data.entity.Note
import ru.geekbrains.geekkotlin.ui.base.BaseActivity
import timber.log.Timber
import java.util.*

class NoteActivity : BaseActivity<Note?, NoteViewState>() {

    companion object {
        private val EXTRA_NOTE = NoteActivity::class.java.name + "extra.NOTE"
        private const val DATE_FORMAT = "dd.MM.yy HH:mm"
        private const val SAVE_DELAY = 500L

        fun start(context: Context, noteId: String? = null) {
            val intent = Intent(context, NoteActivity::class.java)
            intent.putExtra(EXTRA_NOTE, noteId)
            context.startActivity(intent)
        }
    }

    private var note: Note? = null
    override val layoutRes = R.layout.activity_note
    override val viewModel: NoteViewModel by lazy { ViewModelProviders.of(this).get(NoteViewModel::class.java) }

    val textChangeWatcher = object : TextWatcher {
        private var timer = Timer()

        override fun afterTextChanged(s: Editable?) {
            timer.cancel()
            timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    saveNote()
                }
            }, SAVE_DELAY)
            saveNote()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val noteId = intent.getStringExtra(EXTRA_NOTE)
        noteId?.let {
            viewModel.loadNote(it)
        } ?: let {
            supportActionBar?.title = getString(R.string.new_note_title)
        }
        et_title.addTextChangedListener(textChangeWatcher)
        et_body.addTextChangedListener(textChangeWatcher)
    }

    override fun renderData(data: Note?) {
        this.note = data
        supportActionBar?.title = if (this.note != null) {
            this.note!!.lastChanged.format(DATE_FORMAT)
        } else {
            getString(R.string.new_note_title)
        }

        initView()
    }

    private fun initView() {
        note?.let {
            et_title.setText(it.title)
            et_body.setText(it.text)
            toolbar.setBackgroundColor(it.color.getColorInt(this@NoteActivity))
        }
    }

    private fun saveNote() {
        Timber.d("saveNote")
        if (et_title.text.isNullOrBlank() || et_title.text!!.length < 3) return
        note = note?.copy(
                title = et_title.text.toString(),
                text = et_body.text.toString(),
                lastChanged = Date()
        ) ?: Note(
                UUID.randomUUID().toString(),
                et_title.text.toString(),
                et_body.text.toString()
        )

        note?.let { viewModel.save(note!!) }
    }
}