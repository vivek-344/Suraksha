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

@Suppress("DEPRECATION")
class SensorService : Service() {
    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    private lateinit var mShakeDetector: ShakeDetector
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var isShakeDetectionEnabled = false
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        // For foreground notification
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
                    sendEmergencyMessage()
                }
            }
        })

        mSensorManager.registerListener(
            mShakeDetector,
            mAccelerometer,
            SensorManager.SENSOR_DELAY_UI
        )

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                currentLocation = locationResult.lastLocation
            }
        }

        enableShakeDetection()
        startLocationUpdates()
    }

    private fun enableShakeDetection() {
        isShakeDetectionEnabled = true
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun sendEmergencyMessage() {
        if (isGPSEnabled()) {
            if (currentLocation != null) {
                sendEmergencySMS(currentLocation!!)
            } else {
                sendEmergencySMSWithLastKnownLocation()
            }
        } else {
            sendEmergencySMSWhenGPSOff()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendEmergencySMSWithLastKnownLocation() {
        fusedLocationProviderClient?.lastLocation?.addOnSuccessListener { lastLocation ->
            if (lastLocation != null) {
                sendEmergencySMS(lastLocation)
            } else {
                sendEmergencySMSWithoutLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendEmergencySMSWhenGPSOff() {
        fusedLocationProviderClient?.lastLocation?.addOnSuccessListener { lastLocation ->
            val db = DbHelper(this@SensorService)
            val contacts: List<ContactModel> = db.allContacts

            val message: String = if (lastLocation != null) {
                val locationUrl = "http://maps.google.com/?q=${lastLocation.latitude},${lastLocation.longitude}"
                "GPS is off. But this is the last known location of the device:\n$locationUrl"
            } else {
                "I am in danger and need help. Please urgently reach me out. GPS was turned off. Couldn't find location. Call your nearest Police Station."
            }

            for (contact in contacts) {
                sendSMS(contact.getPhoneNumber(), message)
            }
        }
    }

    private fun sendEmergencySMS(location: Location) {
        val db = DbHelper(this@SensorService)
        val contacts: List<ContactModel> = db.allContacts

        val locationUrl = "http://maps.google.com/?q=${location.latitude},${location.longitude}"

        for (contact in contacts) {
            val message = "Hey ${contact.getContact()}, I am in danger and need help. Please urgently reach me out. Here are my coordinates:\n$locationUrl"
            sendSMS(contact.getPhoneNumber(), message)
        }
    }

    private fun sendEmergencySMSWithoutLocation() {
        val db = DbHelper(this@SensorService)
        val contacts: List<ContactModel> = db.allContacts

        val message = "I am in danger and need help. Please urgently reach me out. GPS was turned off. Couldn't find location. Call your nearest Police Station."

        for (contact in contacts) {
            sendSMS(contact.getPhoneNumber(), message)
        }
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
    }

    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(vibrationEffect)
        } else {
            vibrator.vibrate(500)
        }
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
        mSensorManager.unregisterListener(mShakeDetector)
        stopLocationUpdates()
        super.onDestroy()
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
    }
}