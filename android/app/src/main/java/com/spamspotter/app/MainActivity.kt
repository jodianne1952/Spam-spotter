package com.spamspotter.app

import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.os.Bundle
import android.telecom.TelecomManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var status: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 48, 36, 36)
        }
        layout.addView(TextView(this).apply { text = "Spam Spotter"; textSize = 30f })
        layout.addView(TextView(this).apply {
            text = "Early Android test build. Call screening requires your approval. No calls are blocked by default."
            textSize = 18f
        })
        status = TextView(this).apply { textSize = 18f }
        layout.addView(status)
        layout.addView(Button(this).apply {
            text = "Enable call screening"
            setOnClickListener {
                val roles = getSystemService(RoleManager::class.java)
                if (roles.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) {
                    startActivityForResult(roles.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING), 100)
                } else status.text = "Call screening role unavailable on this device."
            }
        })
        setContentView(layout)
    }
    override fun onResume() {
        super.onResume()
        val roles = getSystemService(RoleManager::class.java)
        status.text = if (roles.isRoleHeld(RoleManager.ROLE_CALL_SCREENING))
            "Call screening enabled (monitor-only mode)." else "Call screening not enabled."
    }
}
