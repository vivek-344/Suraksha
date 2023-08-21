package com.satverse.suraksha

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.satverse.suraksha.R.id.emergency_button
import com.satverse.suraksha.dropdown.EditProfileActivity
import com.satverse.suraksha.dropdown.HowToUseActivity
import com.satverse.suraksha.sos.EmergencyContactsActivity
import com.satverse.suraksha.sos.ScreenOnOffBackgroundService
import com.satverse.suraksha.userlogin.LoginActivity
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases
import kotlinx.coroutines.launch

class LandingPageActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var isSirenPlaying = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        lifecycleScope.launch {
            val name = fetchName()
            val firstName = getFirstName(name)
            val welcomeTextView = findViewById<TextView>(R.id.welcome)
            val welcomeMessage = "Hello, $firstName!"

            // Start the typing animation
            startTypingAnimation(welcomeTextView, welcomeMessage)
        }

        val backgroundService = Intent(
            applicationContext,
            ScreenOnOffBackgroundService::class.java
        )

        startService(backgroundService)

        val permissionCheck =
            ContextCompat.checkSelfPermission(this@LandingPageActivity, Manifest.permission.SEND_SMS)
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
            val Intent = Intent(this@LandingPageActivity, EmergencyContactsActivity::class.java)
            startActivity(Intent)
        }

        val sosButton = findViewById<ImageView>(R.id.sosButton)
        sosButton.setOnClickListener {
        }

        val sirenButton = findViewById<ImageView>(R.id.sirenButton)
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
    }

    private fun startTypingAnimation(textView: TextView, text: String) {
        val delayMillis = 100L // Delay between each character reveal
        val typingSpeed = 50L // Speed of typing animation

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

                    mediaPlayer!!.stop()

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

    @Deprecated("Deprecated in Java", ReplaceWith("finishAffinity()"))
    override fun onBackPressed() {
        stopSiren()
        mediaPlayer!!.stop()
        finishAffinity()
    }

    private fun startSiren() {
        mediaPlayer = MediaPlayer.create(this, R.raw.police_siren)
        mediaPlayer?.start()
        mediaPlayer?.isLooping = true
        isSirenPlaying = true
    }

    private fun stopSiren() {
        mediaPlayer?.apply {
            if (isPlaying) {
                pause()
                seekTo(0)
            }
        }
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
}