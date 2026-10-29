package com.example.assignment.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getCurrentDate(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}

fun getCurrentTime(): String {
    return SimpleDateFormat("hh:mm a", Locale.US).format(Date())
}

fun getDisplayDate(date: String): String {
    if (date.isBlank()) return "Today"

    val today = getCurrentDate()

    if (date == today) {
        return "Today"
    }

    return try {
        val input =
            SimpleDateFormat("yyyy-MM-dd", Locale.US)

        val output =
            SimpleDateFormat("MMM dd", Locale.US)

        output.format(input.parse(date)!!)
    } catch (e: Exception) {
        date
    }
}