package com.satverse.suraksha.userlogin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.satverse.suraksha.LandingPageActivity
import com.satverse.suraksha.R
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Account
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val forgetPasswordButton = findViewById<Button>(R.id.forget_password)
        forgetPasswordButton.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        val signUpButton = findViewById<Button>(R.id.sign_up_now)
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val landingPageButton = findViewById<Button>(R.id.go_login)
        landingPageButton.setOnClickListener {
            lifecycleScope.launch {
                logInUser()
            }
        }
    }

    private suspend fun logInUser() {

        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("64bb859f2d53d0d44e9c")
            .setSelfSigned(true)

        val email = findViewById<EditText>(R.id.email).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()

        val users = Account(client)

        try {
            var user = users.createEmailSession(
                email = email,
                password = password,
            )
            Log.d("Appwrite response", user.toString())

            val intent = Intent(this, LandingPageActivity::class.java)
            startActivity(intent)

            isUserLoggedIn()
        }

        catch(e : AppwriteException) {
            runOnUiThread {
                Toast.makeText(this, "Login Failed!" , Toast.LENGTH_SHORT).show()
            }
            e.printStackTrace()
        }


    }

    private fun isUserLoggedIn() {
        val sharedPref = getSharedPreferences("loggedIn", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("LoggedIn", true)
        editor.apply()
    }
}