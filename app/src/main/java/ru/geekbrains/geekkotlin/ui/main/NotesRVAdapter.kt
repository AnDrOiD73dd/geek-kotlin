package ru.geekbrains.geekkotlin.ui.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_note.*
import ru.geekbrains.geekkotlin.R
import ru.geekbrains.geekkotlin.common.getColorInt
import ru.geekbrains.geekkotlin.data.entity.Note

class NotesRVAdapter(val onItemClick: ((Note) -> Unit)? = null) : RecyclerView.Adapter<NotesRVAdapter.NoteViewHolder>() {

    var notes: List<Note> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(viewHolder: NoteViewHolder, position: Int) {
        viewHolder.bind(notes[position])
    }

    inner class NoteViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(note: Note) = with(note) {
            tv_title.text = title
            tv_text.text = text

            itemView.setBackgroundColor(note.color.getColorInt(containerView.context))
            itemView.setOnClickListener {
                onItemClick?.invoke(note)
            }
        }
    }
}