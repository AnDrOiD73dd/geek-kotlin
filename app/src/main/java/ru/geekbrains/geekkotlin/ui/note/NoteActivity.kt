package ru.geekbrains.geekkotlin.ui.note

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_note.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.koin.android.viewmodel.ext.android.viewModel
import ru.geekbrains.geekkotlin.R
import ru.geekbrains.geekkotlin.common.format
import ru.geekbrains.geekkotlin.common.getColorInt
import ru.geekbrains.geekkotlin.data.entity.Note
import ru.geekbrains.geekkotlin.ui.base.BaseActivity
import timber.log.Timber
import java.util.*

class NoteActivity : BaseActivity<NoteData>() {

    companion object {
        private val EXTRA_NOTE = NoteActivity::class.java.name + "extra.NOTE"
        private const val DATE_FORMAT = "dd.MM.yy HH:mm"
        private const val SAVE_DELAY = 500L

        fun start(context: Context, noteId: String? = null) = context.startActivity<NoteActivity>(EXTRA_NOTE to noteId)
    }

    override val layoutRes = R.layout.activity_note
    override val model: NoteViewModel by viewModel()

    private var color = Note.Color.WHITE
    private var note: Note? = null

    private val textChangeWatcher = object : TextWatcher {
        var job: Job? = null

        override fun afterTextChanged(s: Editable?) {
            if (job?.isCancelled == false) job?.cancel()
            job = launch {
                delay(SAVE_DELAY)
                saveNote()
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val noteId = intent.getStringExtra(EXTRA_NOTE)
        noteId?.let {
            model.loadNote(it)
        } ?: let {
            supportActionBar?.title = getString(R.string.note_title_new)
            setEditListener()
        }

        colorPicker.onColorClickListener = {
            color = it
            setToolbarColor(it)
            saveNote()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?) = menuInflater.inflate(R.menu.note, menu).let { true }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed().let { true }
            R.id.note_menu_palette -> togglePalette()
            R.id.note_menu_delete -> deleteNote()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (colorPicker.isOpen) {
            colorPicker.close()
            return
        }
        super.onBackPressed()
    }

    private fun setToolbarColor(color: Note.Color) {
        toolbar.setBackgroundColor(color.getColorInt(this@NoteActivity))
        colorPicker.setBackgroundColor(color.getColorInt(this@NoteActivity))
    }

    private fun togglePalette() {
        if (colorPicker.isOpen) {
            colorPicker.close()
        } else {
            colorPicker.open()
        }
    }

    private fun deleteNote() {
        alert {
            messageResource = R.string.note_delete_dialog_message
            negativeButton(R.string.note_delete_dialog_cancel) { dialog -> dialog.dismiss() }
            positiveButton(R.string.note_delete_dialog_ok) { model.deleteNote() }
        }.show()
    }

    override fun renderData(data: NoteData) {
        if (data.isDeleted) finish()

        this.note = data.note
        data.note?.let { color = it.color }
        initView()
    }

    private fun initView() {
        note?.run {
            supportActionBar?.title = lastChanged.format(DATE_FORMAT)
            removeEditListener()
            et_title.setText(title)
            et_body.setText(text)
            et_title.text?.let {
                et_title.setSelection(it.length)
            }
            et_body.text?.let {
                et_body.setSelection(it.length)
            }
            setEditListener()
        }
    }

    private fun saveNote() {
        Timber.d("saveNote")
        if (et_title.text.isNullOrBlank() || et_title.text!!.length < 3) return
        note = note?.copy(
                title = et_title.text.toString(),
                text = et_body.text.toString(),
                lastChanged = Date(),
                color = color
        ) ?: Note(
                UUID.randomUUID().toString(),
                et_title.text.toString(),
                et_body.text.toString()
        )

        note?.let { model.save(note!!) }
    }

    private fun setEditListener() {
        et_title.addTextChangedListener(textChangeWatcher)
        et_body.addTextChangedListener(textChangeWatcher)
    }

    private fun removeEditListener() {
        et_title.removeTextChangedListener(textChangeWatcher)
        et_body.removeTextChangedListener(textChangeWatcher)
    }
}