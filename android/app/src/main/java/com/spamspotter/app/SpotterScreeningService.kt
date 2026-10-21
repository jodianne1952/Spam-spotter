package com.spamspotter.app

import android.telecom.Call
import android.telecom.CallScreeningService

/**
 * Only exact, user-entered blocklist matches are blocked.
 * Unknown and private numbers pass through. No number is uploaded.
 */
class SpotterScreeningService : CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        val number = callDetails.handle?.schemeSpecificPart
        val blocked = callDetails.callDirection == Call.Details.DIRECTION_INCOMING &&
            ScreeningPolicy(this).isBlocked(number)
        respondToCall(callDetails, CallResponse.Builder()
            .setDisallowCall(blocked)
            .setRejectCall(blocked)
            .setSkipCallLog(false)
            .setSkipNotification(false)
            .build())
    }
}
