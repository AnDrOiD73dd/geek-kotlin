package ru.geekbrains.geekkotlin.ui.main

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_note.view.*
import ru.geekbrains.geekkotlin.R
import ru.geekbrains.geekkotlin.common.getColorInt
import ru.geekbrains.geekkotlin.data.entity.Note

class NotesRVAdapter(
        private val presenter: NotesRVPresenterContract,
        val onItemClick: ((Note) -> Unit)? = null) : RecyclerView.Adapter<NotesRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return presenter.getItemCount()
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        presenter.bind(viewHolder, position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), NotesRVView {
        override fun setTitle(title: String) {
            with(itemView) { tv_title.text = title }
        }

        override fun setNoteText(text: String) {
            with(itemView) { tv_text.text = text }
        }

        override fun setBackgroundColor(color: Note.Color) {
            itemView.setBackgroundColor(color.getColorInt(itemView.context))
        }

        override fun setOnClickListener(note: Note) {
            itemView.setOnClickListener {
                onItemClick?.invoke(note)
            }
        }
    }
}