package com.satverse.suraksha

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.lifecycle.lifecycleScope
import com.satverse.suraksha.dropdown.EditProfileActivity
import com.satverse.suraksha.dropdown.EmergencyContactsActivity
import com.satverse.suraksha.dropdown.HowToUseActivity
import com.satverse.suraksha.userlogin.LoginActivity
import io.appwrite.Client
import io.appwrite.services.Account
import kotlinx.coroutines.launch

class LandingPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        val btnShowPopup: ImageView = findViewById(R.id.menu)

        btnShowPopup.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.emergencyContacts -> {

                    val intent = Intent(this, EmergencyContactsActivity::class.java)
                    startActivity(intent)

                    true
                }
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