package com.spamspotter.app

import android.app.Activity
import android.app.role.RoleManager
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var status: TextView
    private lateinit var lists: TextView
    private val policy by lazy { ScreeningPolicy(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 36, 36, 36)
        }
        fun label(text: String, size: Float = 18f) = TextView(this).apply {
            this.text = text; textSize = size
            setPadding(0, 12, 0, 12)
            content.addView(this)
        }
        fun button(text: String, action: () -> Unit) {
            content.addView(Button(this).apply {
                this.text = text
                setOnClickListener { action() }
            })
        }
        label("Spam Spotter", 30f)
        label("Private Android test build. Only numbers you explicitly block are rejected. Trusted numbers are always allowed.")
        status = label("Checking screening status...")
        button("Enable call screening") {
            val roles = getSystemService(RoleManager::class.java)
            if (roles.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING))
                startActivityForResult(roles.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING), 100)
            else status.text = "Call screening is unavailable on this device."
        }
        val number = EditText(this).apply {
            hint = "Enter phone number, including area code"
            inputType = InputType.TYPE_CLASS_PHONE
            textSize = 20f
            content.addView(this)
        }
        lists = label("")
        fun refresh() {
            lists.text = "Trusted: ${policy.trustedNumbers().sorted().joinToString().ifEmpty { "None" }}\n" +
                "Blocked: ${policy.blockedNumbers().sorted().joinToString().ifEmpty { "None" }}"
        }
        fun apply(action: (String) -> Boolean) {
            val ok = action(number.text.toString())
            status.text = if (ok) "Saved locally." else "Enter a valid number (at least 7 digits)."
            refresh()
        }
        button("Add trusted number") { apply(policy::addTrusted) }
        button("Block this number") { apply(policy::addBlocked) }
        button("Remove trusted number") {
            policy.removeTrusted(number.text.toString()); refresh()
        }
        button("Unblock this number") {
            policy.removeBlocked(number.text.toString()); refresh()
        }
        label("Quiet hours (settings only; automatic quiet-hours blocking is not enabled).")
        button("Enable 10 PM–7 AM quiet-hours setting") {
            policy.setQuietHours(true); status.text = "Quiet-hours preference saved; not yet used to block calls."
        }
        button("Disable quiet-hours setting") {
            policy.setQuietHours(false); status.text = "Quiet-hours preference disabled."
        }
        refresh()
        setContentView(ScrollView(this).apply { addView(content) })
    }

    override fun onResume() {
        super.onResume()
        if (::status.isInitialized) {
            val roles = getSystemService(RoleManager::class.java)
            status.text = if (roles.isRoleHeld(RoleManager.ROLE_CALL_SCREENING))
                "Call screening enabled." else "Call screening not enabled."
        }
    }
}
