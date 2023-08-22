package com.satverse.suraksha.sos

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log

class ScreenOnOffBackgroundService : Service() {
    var mediaPlayer: MediaPlayer? = null

    var screenOnOffReceiver: BroadcastReceiver? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            priority = 100
        }

//        val intentFilter = IntentFilter()
//
//        intentFilter.addAction("android.intent.action.SCREEN_ON")
//        intentFilter.addAction("android.intent.action.SCREEN_OFF")
//
//        intentFilter.priority = 100
//        screenOnOffReceiver = ScreenOnOffReceiver()

        // Register the broadcast receiver with the intent filter object.
        registerReceiver(screenOnOffReceiver, intentFilter)
        Log.d(
            ScreenOnOffReceiver.SCREEN_TOGGLE_TAG,
            "Service onCreate: screenOnOffReceiver is registered."
        )
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        super.onDestroy()

        // Unregister screenOnOffReceiver when destroy.
        if (screenOnOffReceiver != null) {
            unregisterReceiver(screenOnOffReceiver)
            Log.d(
                ScreenOnOffReceiver.SCREEN_TOGGLE_TAG,
                "Service onDestroy: screenOnOffReceiver is unregistered."
            )
        }
    }

    companion object {
        const val ACTION = "NotifyServiceAction"
        const val STOP_SERVICE_BROADCAST_KEY = "StopServiceBroadcastKey"
        const val RQS_STOP_SERVICE = 1
    }
}