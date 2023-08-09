package com.satverse.suraksha

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.satverse.suraksha.dropdown.EditProfileActivity
import com.satverse.suraksha.userlogin.LoginActivity
import io.appwrite.Client
import io.appwrite.services.Account
import kotlinx.coroutines.launch

class LandingPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        val editProfile = findViewById<ImageView>(R.id.sosButton)
        editProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        val logOut = findViewById<ImageView>(R.id.emergencyButton)
        logOut.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            lifecycleScope.launch {
                logOut()
            }
            userLoggedOut()
        }
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
        } catch (e: Exception) {
            runOnUiThread {
                Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show()
            }
        } finally {
            progressDialog.dismiss()
        }
    }

    private fun userLoggedOut() {
        val sharedPref = getSharedPreferences("logIn", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("LoggedIn", false)
        editor.apply()
    }

    @Deprecated("Deprecated in Java", ReplaceWith("finishAffinity()"))
    override fun onBackPressed() {
        finishAffinity()
    }
}