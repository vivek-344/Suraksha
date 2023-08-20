package com.satverse.suraksha.sos

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log

/**
 * Services :- Services in Android are a special component that facilitates an application to run in the background in order
 * to perform long-running operation tasks. The prime aim of a service is to ensure that the application remains
 * active in the background so that the user can operate multiple applications at the same time.
 * A service is started when an application component, such as an activity, starts it by calling startService().
 */
class ScreenOnOffBackgroundService : Service() {
    /**
     * BroadcastReceiver :- Broadcast in android is the system-wide events that can occur when the device starts,
     * when a message is received on the device or when incoming calls are received, or when a device goes to airplane mode,
     * etc. Broadcast Receivers are used to respond to these system-wide events.
     */
    var screenOnOffReceiver: BroadcastReceiver? = null
    private val myBlog = "http://www.cs.dartmouth.edu/~campbell/cs65/cs65.html"
    /**
     * IBinder :- Base interface for a remotable object, the core part of a lightweight remote procedure call mechanism designed for
     * high performance when performing in-process and cross-process calls.
     * This interface describes the abstract protocol for interacting with a remotable object.
     * Do not implement this interface directly, instead extend from Binder.
     * blog link :- https://developer.android.com/reference/android/os/IBinder.
     */
    /**
     * A bound service is the server in a client-server interface.
     * It allows components (such as activities) to bind to the service, send requests, receive responses,
     * and perform interprocess communication (IPC).
     * blog link :- https://developer.android.com/guide/components/bound-services.
     */
    /**
     * onBind() :- A bound service is an implementation of the Service class that allows other applications to bind to it
     * and interact with it. To provide binding for a service, onBind() callback method must be implemented.
     * This method returns an IBinder object that defines the programming interface that clients can use to interact with
     * the service.
     */
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /**
     * onStartCommand():- it is called every time a client starts the service using startService(Intent intent) .
     * This means that onStartCommand() can get called multiple times.
     * You should do the things in this method that are needed each time a client requests something from your service.
     */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        /**
         * IntentFilter :- Implicit intent uses the intent filter to serve the user request.
         * The intent filter specifies the types of intents that an activity, service, or broadcast receiver can
         * respond. Generally intent filters are declared in the Android manifest file.
         */
        // Create an IntentFilter instance.
        val intentFilter = IntentFilter()

        // Add network connectivity change action.
        intentFilter.addAction("android.intent.action.SCREEN_ON")
        intentFilter.addAction("android.intent.action.SCREEN_OFF")

        // Set broadcast receiver priority.
        intentFilter.priority = 100
        // Create a network change broadcast receiver.
        screenOnOffReceiver = ScreenOnOffReceiver()


        // Register the broadcast receiver with the intent filter object.
        registerReceiver(screenOnOffReceiver, intentFilter)


//        // Send Notification
//        String notificationTitle = "Demo of Notification!";
//        String notificationText = "Course Website";
//        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(myBlog));
//        @SuppressLint("WrongConstant") PendingIntent pendingIntent
//                = PendingIntent.getActivity(getBaseContext(),
//                0, myIntent,
//                Intent.FLAG_ACTIVITY_NEW_TASK);
//
//
//
//
//        Notification notification = new Notification.Builder(this)
//                .setContentTitle(notificationTitle)
//                .setContentText(notificationText).setSmallIcon(R.mipmap.ic_launcher)
//                .setContentIntent(pendingIntent).build();
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notification.flags = notification.flags
//                | Notification.FLAG_ONGOING_EVENT;
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//
//        notificationManager.notify(0, notification);
        Log.d(
            ScreenOnOffReceiver.SCREEN_TOGGLE_TAG,
            "Service onCreate: screenOnOffReceiver is registered."
        )
    }

    override fun onDestroy() {
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