@file:Suppress("DEPRECATION")

package com.satverse.suraksha

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.satverse.suraksha.R.id.emergency_button
import com.satverse.suraksha.dropdown.EditProfileActivity
import com.satverse.suraksha.dropdown.HowToUseActivity
import com.satverse.suraksha.dropdown.PrivacyPolicyActivity
import com.satverse.suraksha.sos.EmergencyContactsActivity
import com.satverse.suraksha.sos.contacts.DbHelper
import com.satverse.suraksha.sos.shake.SensorService
import com.satverse.suraksha.userlogin.LoginActivity
import io.appwrite.Client
import io.appwrite.services.Account
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import android.media.AudioManager
import android.net.ConnectivityManager
import android.view.KeyEvent
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

class LandingPageActivity : AppCompatActivity() {

    private var isOnCreateRunning = false
    private var mediaPlayer: MediaPlayer? = null
    private var isSirenPlaying = false
    private var sirenGIF: GifDrawable? = null
    private lateinit var audioManager: AudioManager
    private lateinit var adView: AdView

    @SuppressLint("MissingInflatedId", "UnspecifiedRegisterReceiverFlag", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isOnCreateRunning = true
        setContentView(R.layout.activity_landing_page)

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        mediaPlayer = MediaPlayer.create(this, R.raw.police_siren)
        mediaPlayer?.isLooping = true

        val sosImageView = findViewById<ImageView>(R.id.sosImage)
        val sosTextView = findViewById<TextView>(R.id.sosText)
        sirenGIF = findViewById<GifImageView>(R.id.siren).drawable as GifDrawable
        sirenGIF?.pause()

        if (isServiceRunning()) {
            sosImageView.visibility = View.VISIBLE
            sosTextView.text = ""
        } else {
            sosImageView.visibility = View.GONE
            sosTextView.text = getString(R.string.sos)
        }

        val smsPermission = ContextCompat.checkSelfPermission(this@LandingPageActivity, Manifest.permission.SEND_SMS)
        val contactPermission = ContextCompat.checkSelfPermission(this@LandingPageActivity, Manifest.permission.READ_CONTACTS)
        val locationPermission = ContextCompat.checkSelfPermission(this@LandingPageActivity, Manifest.permission.ACCESS_FINE_LOCATION)
        if (smsPermission != PackageManager.PERMISSION_GRANTED && contactPermission != PackageManager.PERMISSION_GRANTED && locationPermission != PackageManager.PERMISSION_GRANTED) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Location Permission")
            builder.setMessage("This app requires permission to access your location, contacts, and send SMS messages in the background for sharing your current location with emergency contacts.\n\nWe do not store any of your data!")
            builder.setCancelable(false)
            builder.setPositiveButton("Accept") { dialog, which ->
                ActivityCompat.requestPermissions(
                    this@LandingPageActivity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.SEND_SMS),
                    0
                )
            }
            builder.setNegativeButton("Decline") { dialog, which ->
                dialog.dismiss()
                finishAffinity()
            }

            val alert = builder.create()
            alert.show()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), 100)
        }

        val btnShowPopup: ImageView = findViewById(R.id.menu)
        btnShowPopup.setOnClickListener { view ->
            showPopupMenu(view)
        }

        val emergencyButton = findViewById<ImageView>(emergency_button)
        emergencyButton.setOnClickListener {
            val intent = Intent(this@LandingPageActivity, EmergencyContactsActivity::class.java)
            startActivity(intent)
        }

        val sosButton = findViewById<ImageView>(R.id.sosButton)
        sosButton.setOnClickListener {
            toggleSensorService()
            if (isServiceRunning()) {
                sosImageView.visibility = View.VISIBLE
                sosTextView.text = ""
            } else {
                sosImageView.visibility = View.GONE
                sosTextView.text = getString(R.string.sos)
            }
        }

        val sirenButton = findViewById<ImageView>(R.id.siren)
        sirenButton.setOnClickListener {
            if (isSirenPlaying) {
                stopSiren()
            } else {
                startSiren()
            }
        }

        val helplineButton = findViewById<ImageView>(R.id.helplineButton)
        helplineButton.setOnClickListener {
            val intent = Intent(this, EmergencyHelplineActivity::class.java)
            startActivity(intent)
        }

        val aboutUsButton = findViewById<ImageView>(R.id.aboutButton)
        aboutUsButton.setOnClickListener {
            val intent = Intent(this, AboutUsActivity::class.java)
            startActivity(intent)
        }

        MobileAds.initialize(this) {
            adView = findViewById(R.id.adView)

            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }

        isOnCreateRunning = false
    }

    private fun toggleSensorService() {
        val intent = Intent(this, SensorService::class.java)
        val locationEnabled = isLocationEnabled()
        val internetConnected = isInternetConnected()
        val boolean = locationEnabled && internetConnected

        if (isServiceRunning()) {
            val imageView = findViewById<ImageView>(R.id.sosImage)
            imageView.visibility = View.GONE
            val sosTextView = findViewById<TextView>(R.id.sosText)
            sosTextView.text = getString(R.string.sos)
            stopService(intent)
            servicePaused()
            Toast.makeText(this, "Service deactivated!", Toast.LENGTH_SHORT).show()
        } else {
            val db = DbHelper(this)
            if (db.count() > 0) {
                startService(intent)
                ContextCompat.startForegroundService(this, intent)
                if (!boolean)
                    Toast.makeText(this, "Turn on location and internet to send SOS message with location!", Toast.LENGTH_LONG).show()
                val imageView = findViewById<ImageView>(R.id.sosImage)
                imageView.visibility = View.VISIBLE
                val sosTextView = findViewById<TextView>(R.id.sosText)
                sosTextView.text = ""
                serviceRunning()
                Toast.makeText(this, "Service activated!", Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(this, "Add contacts to start the SOS service!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isOnCreateRunning) {
            val sharedPreferences = getSharedPreferences("Name", Context.MODE_PRIVATE)
            val firstName = sharedPreferences.getString("Name", "")

            val welcomeTextView = findViewById<TextView>(R.id.welcome)
            val welcomeMessage = "Hello, $firstName!"
            startTypingAnimation(welcomeTextView, welcomeMessage)
        }

        val sosImageView = findViewById<ImageView>(R.id.sosImage)
        val sosTextView = findViewById<TextView>(R.id.sosText)
        val intent = Intent(this, SensorService::class.java)

        val db = DbHelper(this)
        if (db.count() == 0) {
            stopService(intent)
            servicePaused()
            sosImageView.visibility = View.GONE
            sosTextView.text = getString(R.string.sos)
        }
    }

    private fun startTypingAnimation(textView: TextView, text: String) {
        val delayMillis = 100L
        val typingSpeed = 50L

        val handler = Handler()
        var index = 0

        val runnable = object : Runnable {
            override fun run() {
                if (index <= text.length) {
                    textView.text = text.substring(0, index)
                    index++
                    handler.postDelayed(this, typingSpeed)
                }
            }
        }

        handler.postDelayed(runnable, delayMillis)
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.howToUse -> {
                    val intent = Intent(this, HowToUseActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.editProfile -> {
                    val intent = Intent(this, EditProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.privacyPolicy -> {
                    val intent = Intent(this, PrivacyPolicyActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.logOut -> {
                    lifecycleScope.launch {
                        logOut()
                    }
                    stopSiren()
                    mediaPlayer?.stop()
                    userLoggedOut()
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private suspend fun logOut() {
        val progressDialog = ProgressDialog(this@LandingPageActivity)
        progressDialog.setMessage("Logging Out...")
        progressDialog.show()

        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("65fa760fe93c38713a0f")
            .setSelfSigned(true)

        val account = Account(client)

        try {
            account.deleteSessions()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            runOnUiThread {
                Toast.makeText(this, "Error while logging out!", Toast.LENGTH_SHORT).show()
            }
        } finally {
            progressDialog.dismiss()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        stopSiren()
        mediaPlayer?.stop()
        finishAffinity()
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        when (event?.keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN -> {
                if (isSirenPlaying) {
                    return true
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    private fun startSiren() {
        audioManager.isSpeakerphoneOn = true
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION

        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0)

        mediaPlayer?.start()
        isSirenPlaying = true
        sirenGIF?.start()
    }

    private fun stopSiren() {
        mediaPlayer?.apply {
            if (isPlaying) {
                pause()
                seekTo(0)
            }
        }
        sirenGIF?.pause()
        isSirenPlaying = false
    }

    private fun userLoggedOut() {
        val sharedPref = getSharedPreferences("logIn", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("LoggedIn", false)
        editor.apply()
    }

    private fun isServiceRunning(): Boolean {
        val sharedPref = getSharedPreferences("ShakeService", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Running", false)
    }

    private fun serviceRunning() {
        val sharedPref = getSharedPreferences("ShakeService", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Running", true)
        editor.apply()
    }

    private fun servicePaused() {
        val sharedPref = getSharedPreferences("ShakeService", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Running", false)
        editor.apply()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun isInternetConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    override fun onDestroy() {
        mediaPlayer?.release()
//        mediaPlayer?.stop()
        super.onDestroy()
    }
}