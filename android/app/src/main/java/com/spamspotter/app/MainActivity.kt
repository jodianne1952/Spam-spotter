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
        fun label(value: String, size: Float = 18f): TextView = TextView(this).apply {
            text = value
            textSize = size
            setPadding(0, 12, 0, 12)
            content.addView(this)
        }
        fun button(value: String, action: () -> Unit) {
            content.addView(Button(this).apply { text = value; setOnClickListener { action() } })
        }
        label("Spam Spotter", 30f)
        label("Private testing version. Trusted numbers ring normally. Only numbers you block are rejected. Quiet hours silence other calls without rejecting them.")
        status = label("Checking screening status...")
        button("Enable call screening") {
            val roles = getSystemService(RoleManager::class.java)
            if (roles.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING))
                startActivityForResult(roles.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING), 100)
            else status.text = "Call screening is unavailable on this device."
        }
        val number = EditText(this).apply {
            hint = "Phone number with area code"
            contentDescription = "Phone number to trust or block"
            inputType = InputType.TYPE_CLASS_PHONE
            textSize = 20f
            content.addView(this)
        }
        lists = label("")
        fun refresh() {
            lists.text = "Trusted numbers: ${policy.trustedNumbers().sorted().joinToString().ifEmpty { "None" }}\n" +
                "Blocked numbers: ${policy.blockedNumbers().sorted().joinToString().ifEmpty { "None" }}\n" +
                "Quiet hours (10 PM–7 AM): ${if (policy.quietHoursEnabled()) "ON" else "OFF"}"
        }
        fun add(action: (String) -> Boolean) {
            status.text = if (action(number.text.toString())) "Number saved on this device."
                else "Enter a valid number including area code (10–15 digits)."
            refresh()
        }
        button("Trust this number") { add(policy::addTrusted) }
        button("Block this number") { add(policy::addBlocked) }
        button("Remove from trusted") { policy.removeTrusted(number.text.toString()); refresh() }
        button("Remove from blocked") { policy.removeBlocked(number.text.toString()); refresh() }
        label("Quiet hours: untrusted calls are silenced, not blocked. Your phone can still show missed calls.")
        button("Turn quiet hours ON (10 PM–7 AM)") {
            policy.setQuietHours(true); refresh()
        }
        button("Turn quiet hours OFF") {
            policy.setQuietHours(false); refresh()
        }
        label("Privacy: Lists stay on this phone. No SMS, contacts, call history, or numbers are uploaded.")
        refresh()
        setContentView(ScrollView(this).apply { addView(content) })
    }

    override fun onResume() {
        super.onResume()
        if (::status.isInitialized) {
            val roles = getSystemService(RoleManager::class.java)
            status.text = if (roles.isRoleHeld(RoleManager.ROLE_CALL_SCREENING))
                "Call screening is enabled." else "Call screening is not enabled."
        }
    }
}
