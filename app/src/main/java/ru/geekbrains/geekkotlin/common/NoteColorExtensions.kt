package ru.geekbrains.geekkotlin.common

import android.content.Context
import android.support.v4.content.ContextCompat
import ru.geekbrains.geekkotlin.R
import ru.geekbrains.geekkotlin.data.entity.Note

fun Note.Color.getColorInt(context: Context) =
        ContextCompat.getColor(
                context, when (this) {
            Note.Color.WHITE -> R.color.white
            Note.Color.YELLOW -> R.color.yellow
            Note.Color.GREEN -> R.color.green
            Note.Color.BLUE -> R.color.blue
            Note.Color.RED -> R.color.red
            Note.Color.VIOLET -> R.color.violet
            Note.Color.PINK -> R.color.pink
        }
        )

