package ru.geekbrains.geekkotlin.common

import java.text.SimpleDateFormat
import java.util.*

fun Date.format(formmat: String) = SimpleDateFormat(formmat, Locale.getDefault()).format(this)