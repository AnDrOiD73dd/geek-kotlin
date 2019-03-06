package ru.geekbrains.geekkotlin.ui.note

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import kotlinx.android.synthetic.main.activity_note.*
import ru.geekbrains.geekkotlin.R
import ru.geekbrains.geekkotlin.data.NoteColorAdapter
import ru.geekbrains.geekkotlin.data.entity.Note
import java.text.SimpleDateFormat
import java.util.*

class NoteActivity : AppCompatActivity() {

    private var note: Note? = null
    lateinit var viewModel: NoteViewModel

    companion object {
        private val EXTRA_NOTE = NoteActivity::class.java.name + "extra.NOTE"
        private const val DATE_FORMAT = "dd.MM.yy HH:mm"
        private const val SAVE_DELAY = 500L

        fun start(context: Context, note: Note? = null) {
            val intent = Intent(context, NoteActivity::class.java)
            note?.let { intent.putExtra(EXTRA_NOTE, it) }
            context.startActivity(intent)
        }
    }

    val textChangeWatcher = object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            saveNote()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        viewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)
        note = intent.getParcelableExtra(EXTRA_NOTE)
        supportActionBar?.title = if (note != null) {
            SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(note!!.lastChanged)
        } else {
            getString(R.string.new_note_title)
        }

        initView()
    }

    private fun initView() {
        note?.let {
            et_title.setText(it.title)
            et_body.setText(it.text)
            toolbar.setBackgroundColor(ContextCompat.getColor(this, NoteColorAdapter.getColor(it.color)))
        }
        et_title.addTextChangedListener(textChangeWatcher)
        et_body.addTextChangedListener(textChangeWatcher)
    }

    private fun saveNote() {
        if (et_title.text.isNullOrBlank() || et_title.text!!.length < 3) return

        Handler().postDelayed({
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
        }, SAVE_DELAY)
    }
}