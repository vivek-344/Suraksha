package com.satverse.suraksha.sos.shake

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat

class ReactivateService : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("Check", "Receiver Started")

        val serviceIntent = Intent(context, SensorService::class.java)

        // Always start the service as a foreground service
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}
