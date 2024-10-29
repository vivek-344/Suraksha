package com.satverse.suraksha.userlogin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.satverse.suraksha.LandingPageActivity
import com.satverse.suraksha.R
import io.appwrite.Client
import io.appwrite.services.Account
import kotlinx.coroutines.*

class EmailVerificationActivity : AppCompatActivity() {

    private lateinit var uriString: String

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_verification)

        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("65fa760fe93c38713a0f")
            .setSelfSigned(true)

        val account = Account(client)

        val intentData = intent.data

        if (intentData != null) {
            uriString = intentData.toString()
            val userId = intentData.getQueryParameter("userId")
            val secret = intentData.getQueryParameter("secret")

            GlobalScope.launch(Dispatchers.Main) {
                account.updateVerification(
                    userId = userId ?: "",
                    secret = secret ?: ""
                )

                val response = account.get()
                val isEmailVerified = response.emailVerification

                if (isEmailVerified) {
                    val intent = Intent(this@EmailVerificationActivity, LandingPageActivity::class.java)
                    startActivity(intent)

                    userLoggedIn()
                } else {
                    Toast.makeText(this@EmailVerificationActivity, "Email not Verified!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun userLoggedIn() {
        val sharedPref = getSharedPreferences("logIn", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("LoggedIn", true)
        editor.apply()
    }
}
