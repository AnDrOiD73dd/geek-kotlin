package ru.geekbrains.geekkotlin.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.random.Random

@Parcelize
data class Note(
        val id: String = "",
        val title: String = "",
        val text: String = "",
        val color: Color = Color.getRandomColor(),
        val lastChanged: Date = Date()) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Note
        if (id != other.id) return false
        return true
    }

    override fun hashCode() = id.hashCode()

    enum class Color {
        WHITE,
        YELLOW,
        GREEN,
        BLUE,
        RED,
        VIOLET,
        PINK;

        companion object {
            fun getRandomColor() = Note.Color.values().get(Random.nextInt(0, Note.Color.values().size))
        }
    }
}
