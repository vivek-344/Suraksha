package com.satverse.suraksha

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
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
import com.satverse.suraksha.sos.EmergencyContactsActivity
import com.satverse.suraksha.sos.ScreenOnOffBackgroundService
import com.satverse.suraksha.sos.contacts.DbHelper
import com.satverse.suraksha.sos.shake.SensorService
import com.satverse.suraksha.userlogin.LoginActivity
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView

class LandingPageActivity : AppCompatActivity() {

    private var isOnCreateRunning = false
    private var mediaPlayer: MediaPlayer? = null
    private var isSirenPlaying = false
    private var sirenGIF: GifDrawable? = null

    @SuppressLint("MissingInflatedId", "UnspecifiedRegisterReceiverFlag", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isOnCreateRunning = true
        setContentView(R.layout.activity_landing_page)

        val imageView = findViewById<ImageView>(R.id.sosImage)
        val sosTextView = findViewById<TextView>(R.id.sosText)
        sirenGIF = findViewById<GifImageView>(R.id.siren).drawable as GifDrawable

        if (isServiceRunning()) {
            imageView.visibility = View.VISIBLE
            sosTextView.text = ""
        } else {
            imageView.visibility = View.GONE
            sosTextView.text = getString(R.string.sos)
        }

        if (mediaPlayer?.isPlaying == false) {
            sirenGIF?.start()
        } else {
            sirenGIF?.pause()
        }

        val backgroundService = Intent(applicationContext, ScreenOnOffBackgroundService::class.java)
        startService(backgroundService)

        val permissionCheck = ContextCompat.checkSelfPermission(this@LandingPageActivity, Manifest.permission.SEND_SMS)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this@LandingPageActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@LandingPageActivity,
                arrayOf<String>(
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_CONTACTS
                ),
                0
            )
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
                imageView.visibility = View.VISIBLE
                sosTextView.text = ""
            } else {
                imageView.visibility = View.GONE
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

        isOnCreateRunning = false
    }

    private fun toggleSensorService() {
        val intent = Intent(this, SensorService::class.java)

        if (isServiceRunning()) {
            stopService(intent)
            val imageView = findViewById<ImageView>(R.id.sosImage)
            imageView.visibility = View.GONE
            val sosTextView = findViewById<TextView>(R.id.sosText)
            sosTextView.text = getString(R.string.sos)
            servicePaused()
        } else {
            val db = DbHelper(this)
            if (db.count() > 0) {
                ContextCompat.startForegroundService(this, intent)
                val imageView = findViewById<ImageView>(R.id.sosImage)
                imageView.visibility = View.VISIBLE
                val sosTextView = findViewById<TextView>(R.id.sosText)
                sosTextView.text = ""
                serviceRunning()
            } else
                Toast.makeText(this, "Add contacts to start the SOS service!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isOnCreateRunning) {
            lifecycleScope.launch {
                val name = fetchName()
                val firstName = getFirstName(name)
                val welcomeTextView = findViewById<TextView>(R.id.welcome)
                val welcomeMessage = "Hello, $firstName!"
                startTypingAnimation(welcomeTextView, welcomeMessage)
            }
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
            .setProject("64bb859f2d53d0d44e9c")
            .setSelfSigned(true)

        val account = Account(client)

        try {
            account.deleteSessions()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            runOnUiThread {
                Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        } finally {
            progressDialog.dismiss()
        }
    }

    override fun onBackPressed() {
        stopSiren()
        mediaPlayer?.stop()
        finishAffinity()
    }

    private fun startSiren() {
        mediaPlayer = MediaPlayer.create(this, R.raw.police_siren)
        mediaPlayer?.start()
        mediaPlayer?.isLooping = true
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

    private fun getFirstName(fullName: String): String {
        val parts = fullName.split(" ")
        if (parts.isNotEmpty()) {
            return parts[0]
        }
        return fullName
    }

    private suspend fun fetchName(): String {
        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("64bb859f2d53d0d44e9c")
            .setSelfSigned(true)

        val account = Account(client)
        val userDatabase = Databases(client)

        try {
            val userData = account.listSessions()
            val userId = userData.sessions.firstOrNull()?.userId

            if (userId != null) {
                val data = userDatabase.getDocument(
                    databaseId = "64bc1e13ca662cd39b95",
                    collectionId = "64bc1e1e7465e6d3e4c2",
                    documentId = userId
                )

                return data.data["fullName"].toString().trim()
            }
        } catch (e: Exception) {
            Log.e("LandingPageActivity", "Error while fetching user's name", e)
        }

        return "User"
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

    override fun onDestroy() {
        mediaPlayer?.stop()
        super.onDestroy()
    }
}