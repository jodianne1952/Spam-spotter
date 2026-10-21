package com.spamspotter.app

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log

/**
 * Initial safe baseline: responds promptly to screening requests, but never blocks calls.
 * A future policy engine must be explicitly enabled by the user before blocking.
 */
class SpotterScreeningService : CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        Log.i("SpamSpotter", "Call screening event received")
        respondToCall(
            callDetails,
            CallResponse.Builder()
                .setDisallowCall(false)
                .setRejectCall(false)
                .setSkipCallLog(false)
                .setSkipNotification(false)
                .build()
        )
    }
}
