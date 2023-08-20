package com.satverse.suraksha

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import com.google.android.material.textfield.TextInputEditText
import com.satverse.suraksha.AboutUsActivity
import com.satverse.suraksha.EmergencyHelplineActivity
import com.satverse.suraksha.R
import com.satverse.suraksha.dropdown.EditProfileActivity
import com.satverse.suraksha.dropdown.HowToUseActivity
import com.satverse.suraksha.sos.EmergencyContactsActivity
import com.satverse.suraksha.sos.ScreenOnOffBackgroundService
import com.satverse.suraksha.sos.ScreenOnOffReceiver
import com.satverse.suraksha.userlogin.LoginActivity
import io.appwrite.Client
import io.appwrite.models.User
import io.appwrite.services.Account
import io.appwrite.services.Databases
import kotlinx.coroutines.launch

class LandingPageActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var isSirenPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        lifecycleScope.launch {
            val name = fetchData()
            val welcomeTextView = findViewById<TextView>(R.id.welcome)
            val welcomeMessage = "Hello, $name!"
            welcomeTextView.text = welcomeMessage
        }


        val backgroundService = Intent(
            applicationContext,
            ScreenOnOffBackgroundService::class.java
        )
        startService(backgroundService)
        Log.d(ScreenOnOffReceiver.SCREEN_TOGGLE_TAG, "Activity onCreate")
        val permissionCheck =
            ContextCompat.checkSelfPermission(this@LandingPageActivity, Manifest.permission.SEND_SMS)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this@LandingPageActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this@LandingPageActivity,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@LandingPageActivity,
                arrayOf<String>(
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.CALL_PHONE,
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

        val policeButton = findViewById<ImageView>(R.id.call100Button)
        policeButton.setOnClickListener {
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

        val helplineButton = findViewById<ImageView>(R.id.emergencyButton)
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
            }
        } finally {
            progressDialog.dismiss()
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith("finishAffinity()"))
    override fun onBackPressed() {
        stopSiren()
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

    private fun openSmsSettings() {

        Toast.makeText(this, "Enable SMS Permission!", Toast.LENGTH_LONG).show()

        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun userLoggedOut() {
        val sharedPref = getSharedPreferences("logIn", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("LoggedIn", false)
        editor.apply()
    }

    private suspend fun fetchData(): String {
        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("64bb859f2d53d0d44e9c")
            .setSelfSigned(true)

        val account = Account(client)

        try {
            val userData = account.get()

            val name = userData.name

            return name

        } catch (e: Exception) {
            Log.e("EditProfileActivity", "Error while listing sessions", e)
        }

        return "User"
    }
}