package ru.geekbrains.geekkotlin.data

import ru.geekbrains.geekkotlin.R
import ru.geekbrains.geekkotlin.data.entity.Note

class NoteColorAdapter {

    companion object {
        fun getColor(noteColor: Note.Color): Int {
            return when (noteColor) {
                Note.Color.WHITE -> R.color.white
                Note.Color.YELLOW -> R.color.yellow
                Note.Color.GREEN -> R.color.green
                Note.Color.BLUE -> R.color.blue
                Note.Color.RED -> R.color.red
                Note.Color.VIOLET -> R.color.violet
                Note.Color.PINK -> R.color.pink
            }
        }
    }
}