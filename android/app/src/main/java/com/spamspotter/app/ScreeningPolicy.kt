package com.spamspotter.app

import android.content.Context
import android.telephony.PhoneNumberUtils
import java.time.LocalTime

/** Privacy-first, on-device screening policy. No contact or call-log permissions. */
class ScreeningPolicy(context: Context) {
    private val prefs = context.getSharedPreferences("screening", Context.MODE_PRIVATE)
    private fun normalized(raw: String): String = PhoneNumberUtils.normalizeNumber(raw).orEmpty()
    private fun numbers(key: String): Set<String> = prefs.getStringSet(key, emptySet())?.toSet() ?: emptySet()
    private fun valid(raw: String) = normalized(raw).count { it.isDigit() } in 10..15

    fun addTrusted(raw: String): Boolean = add("trusted", raw)
    fun addBlocked(raw: String): Boolean = add("blocked", raw)
    private fun add(key: String, raw: String): Boolean {
        if (!valid(raw)) return false
        return prefs.edit().putStringSet(key, numbers(key) + normalized(raw)).commit()
    }
    fun removeTrusted(raw: String) = remove("trusted", raw)
    fun removeBlocked(raw: String) = remove("blocked", raw)
    private fun remove(key: String, raw: String) {
        prefs.edit().putStringSet(key, numbers(key) - normalized(raw)).apply()
    }
    fun trustedNumbers() = numbers("trusted")
    fun blockedNumbers() = numbers("blocked")
    fun isTrusted(raw: String?): Boolean = raw != null && normalized(raw) in numbers("trusted")
    fun isBlocked(raw: String?): Boolean = raw != null && !isTrusted(raw) && normalized(raw) in numbers("blocked")

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
    fun quietHoursEnabled() = prefs.getBoolean("quiet_enabled", false)
}
