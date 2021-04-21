package de.westnordost.streetcomplete.ktx

fun Double.toShortString() = if (this % 1 == 0.0) toInt().toString() else toString()

fun Double.format(digits: Int) = "%.${digits}f".format(this)
