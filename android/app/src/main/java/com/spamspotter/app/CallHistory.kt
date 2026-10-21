package com.spamspotter.app


import android.content.Context


/** On-device log of calls the screening service has handled. Nothing leaves the phone. */
class CallHistory(context: Context) {

    private val prefs = context.getSharedPreferences("call_history", Context.MODE_PRIVATE)

    data class Entry(val timestampMillis: Long, val number: String, val action: String)

    fun record(rawNumber: String?, action: String) {

        val number = rawNumber?.takeIf { it.isNotBlank() } ?: "Unknown"

        val line = "${System.currentTimeMillis()}|$number|$action"

        val existing = prefs.getString(KEY, "").orEmpty()

        val lines = (listOf(line) + existing.split("\n").filter { it.isNotBlank() }).take(MAX_ENTRIES)

        prefs.edit().putString(KEY, lines.joinToString("\n")).apply()

    }

    fun entries(): List<Entry> = prefs.getString(KEY, "").orEmpty()

        .split("\n").filter { it.isNotBlank() }.mapNotNull { line ->

            val parts = line.split("|", limit = 3)

            if (parts.size == 3) Entry(parts[0].toLongOrNull() ?: 0L, parts[1], parts[2]) else null

        }

    fun clear() {

        prefs.edit().remove(KEY).apply()

    }

    private companion object {

        const val KEY = "entries"

        const val MAX_ENTRIES = 50

    }

}
