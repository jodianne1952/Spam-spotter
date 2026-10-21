package com.spamspotter.app

import android.content.Context
import android.telephony.PhoneNumberUtils
import java.time.LocalTime

/**
 * Local, conservative rules. Only explicitly blocked numbers are rejected.
 * A trusted number always wins over the blocked list.
 */
class ScreeningPolicy(private val context: Context) {
    private val prefs = context.getSharedPreferences("screening", Context.MODE_PRIVATE)

    private fun normalized(raw: String): String =
        PhoneNumberUtils.normalizeNumber(raw).orEmpty()

    private fun set(key: String): Set<String> = prefs.getStringSet(key, emptySet()) ?: emptySet()

    fun addTrusted(raw: String): Boolean = add("trusted", raw)
    fun addBlocked(raw: String): Boolean = add("blocked", raw)
    fun removeTrusted(raw: String) = remove("trusted", raw)
    fun removeBlocked(raw: String) = remove("blocked", raw)
    fun trustedNumbers(): Set<String> = set("trusted").toSet()
    fun blockedNumbers(): Set<String> = set("blocked").toSet()

    private fun add(key: String, raw: String): Boolean {
        val number = normalized(raw)
        if (number.length < 7) return false
        return prefs.edit().putStringSet(key, set(key) + number).commit()
    }

    private fun remove(key: String, raw: String) {
        prefs.edit().putStringSet(key, set(key) - normalized(raw)).apply()
    }

    fun isBlocked(raw: String?): Boolean {
        if (raw.isNullOrBlank()) return false
        val number = normalized(raw)
        if (number.isBlank() || number in set("trusted")) return false
        return number in set("blocked")
    }

    fun quietHoursActive(now: LocalTime = LocalTime.now()): Boolean {
        if (!prefs.getBoolean("quiet_enabled", false)) return false
        val start = LocalTime.of(prefs.getInt("quiet_start", 22), 0)
        val end = LocalTime.of(prefs.getInt("quiet_end", 7), 0)
        return if (start == end) true else if (start < end)
            !now.isBefore(start) && now.isBefore(end)
        else !now.isBefore(start) || now.isBefore(end)
    }
    fun setQuietHours(enabled: Boolean, start: Int = 22, end: Int = 7) {
        require(start in 0..23 && end in 0..23)
        prefs.edit().putBoolean("quiet_enabled", enabled)
            .putInt("quiet_start", start).putInt("quiet_end", end).apply()
    }
}
