package com.spamspotter.app

import android.telecom.Call
import android.telecom.CallScreeningService

/** Block only explicit user entries; quiet hours silence (do not reject) untrusted calls. */
class SpotterScreeningService : CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        val policy = ScreeningPolicy(this)
        val incoming = callDetails.callDirection == Call.Details.DIRECTION_INCOMING
        val number = callDetails.handle?.schemeSpecificPart
        val blocked = incoming && policy.isBlocked(number)
        // Withheld or unknown caller IDs are not silently rejected.
        val silence = incoming && !blocked && !policy.isTrusted(number) &&
            policy.quietHoursActive()
        respondToCall(callDetails, CallResponse.Builder()
            .setDisallowCall(blocked)
            .setRejectCall(blocked)
            .setSilenceCall(silence)
            .setSkipCallLog(false)
            .setSkipNotification(false)
            .build())
    }
}
