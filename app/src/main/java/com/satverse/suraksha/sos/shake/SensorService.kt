package com.satverse.suraksha.sos.shake

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.*
import android.telephony.SmsManager
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.satverse.suraksha.R
import com.satverse.suraksha.sos.contacts.ContactModel
import com.satverse.suraksha.sos.contacts.DbHelper

class SensorService : Service() {
    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    private lateinit var mShakeDetector: ShakeDetector
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isGpsEnabled = false
    private var isServiceRunning = false // Track service state
    private var isShakeDetectionEnabled = false // Track shake detection state

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Set the flag to indicate that the service is running
        isServiceRunning = true
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground()
        } else {
            startForeground(1, Notification())
        }

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mShakeDetector = ShakeDetector()

        mShakeDetector.setOnShakeListener(object : ShakeDetector.OnShakeListener {
            @SuppressLint("MissingPermission")
            override fun onShake(count: Int) {
                if (isShakeDetectionEnabled && count == 5) {
                    vibrate()
                    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    if (isGpsEnabled) {
                        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@SensorService)
                        val locationRequest = LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setNumUpdates(1)
                            .setInterval(1000)

                        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult) {
                                val location = locationResult.lastLocation
                                if (location != null) {
                                    sendEmergencySMS(location)
                                } else {
                                    sendEmergencySMSWithoutLocation()
                                }
                                fusedLocationClient.removeLocationUpdates(this)
                            }
                        }, Looper.getMainLooper())
                    } else {
                        sendEmergencySMSWithoutLocation()
                    }
                }
            }

            private fun sendEmergencySMS(location: Location) {
                val smsManager = SmsManager.getDefault()
                val db = DbHelper(this@SensorService)
                val contacts: List<ContactModel> = db.allContacts

                val locationUrl = "http://maps.google.com/?q=${location.latitude},${location.longitude}"

                for (contact in contacts) {
                    val message = "Hey ${contact.getContact()}, I am in danger and need help. Please urgently reach me out. Here are my coordinates:\n$locationUrl"
                    smsManager.sendTextMessage(contact.getPhoneNumber(), null, message, null, null)
                }
            }

            private fun sendEmergencySMSWithoutLocation() {
                val smsManager = SmsManager.getDefault()
                val db = DbHelper(this@SensorService)
                val contacts: List<ContactModel> = db.allContacts

                val message = "I am in danger and need help. Please urgently reach me out. GPS was turned off. Couldn't find location. Call your nearest Police Station."

                for (contact in contacts) {
                    smsManager.sendTextMessage(contact.getPhoneNumber(), null, message, null, null)
                }
            }

            private fun vibrate() {
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (ActivityCompat.checkSelfPermission(this@SensorService, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
                        val vibrationEffect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
                        vibrator.vibrate(vibrationEffect)
                    }
                } else {
                    vibrator.vibrate(500)
                }
            }
        })

        mSensorManager.registerListener(
            mShakeDetector,
            mAccelerometer,
            SensorManager.SENSOR_DELAY_UI
        )

        // Enable shake detection
        enableShakeDetection()
    }

    private fun enableShakeDetection() {
        isShakeDetectionEnabled = true // Enable shake detection
    }

    private fun disableShakeDetection() {
        isShakeDetectionEnabled = false // Disable shake detection
    }

    companion object {
        /*
        * Shake detection configuration constants
        */
        private const val SHAKE_THRESHOLD_GRAVITY = 2.7f
        private const val SHAKE_SLOP_TIME_MS = 500
        private const val SHAKE_COUNT_RESET_TIME_MS = 3000
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val channelId = "MyServiceChannel"
        val channelName = "My Background Service"
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(this@SensorService, channelId)
            .setContentTitle("You are safe!")
            .setContentText("We are there for you.")
            .setSmallIcon(R.mipmap.ic_launcher)

        val notification = notificationBuilder.build()
        startForeground(1, notification)
    }

    override fun onDestroy() {
        disableShakeDetection() // Disable shake detection when the service is destroyed
        // Set the flag to indicate that the service is no longer running
        isServiceRunning = false
//        val broadcastIntent = Intent()
//        broadcastIntent.action = "restartservice"
//        broadcastIntent.setClass(this, ReactivateService::class.java)
//        sendBroadcast(broadcastIntent)
        super.onDestroy()
    }
}