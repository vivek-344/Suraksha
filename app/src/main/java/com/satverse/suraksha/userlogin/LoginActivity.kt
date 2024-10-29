@file:Suppress("DEPRECATION")

package com.satverse.suraksha.userlogin

import android.app.ProgressDialog
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
            val email = findViewById<EditText>(R.id.email).text.toString().trim()
            if (isValidEmail(email)) {
                lifecycleScope.launch {
                    logInUser()
                }
            } else {
                Toast.makeText(this, "Invalid Email!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun logInUser() {

        val progressDialog = ProgressDialog(this@LoginActivity)
        progressDialog.setMessage("Logging In...")
        progressDialog.show()

        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("65fa760fe93c38713a0f")
            .setSelfSigned(true)

        val email = findViewById<EditText>(R.id.email).text.toString().trim()
        val password = findViewById<EditText>(R.id.password).text.toString()

        val users = Account(client)

        try {
            val user = users.createEmailSession(
                email = email,
                password = password,
            )
            Log.d("Appwrite response", user.toString())

            val response = users.get()

            val fullName = response.name
            val name = getFirstName(fullName)

            val sharedPreferences = getSharedPreferences("Name", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("Name", name)
            editor.apply()

            val isEmailVerified = response.emailVerification

            if (isEmailVerified) {
                val intent = Intent(this, LandingPageActivity::class.java)
                startActivity(intent)

                isUserLoggedIn()
            }
            else {
                users.createVerification(url = "https://localhost/suraksha")

                Toast.makeText(this, "Verify your email!" , Toast.LENGTH_SHORT).show()

                val intent = Intent(this, VerifyEmailActivity::class.java)
                startActivity(intent)
            }
        }

        catch(e : AppwriteException) {
            runOnUiThread {
                Toast.makeText(this, "Invalid credentials!" , Toast.LENGTH_SHORT).show()
            }
        }
        finally {
            progressDialog.dismiss()
        }
    }

    private fun isUserLoggedIn() {
        val sharedPref = getSharedPreferences("logIn", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("LoggedIn", true)
        editor.apply()
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex(this.getString(R.string.email_check))
        return email.matches(emailRegex)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("finishAffinity()"))
    override fun onBackPressed() {
        finishAffinity()
    }

    private fun getFirstName(fullName: String): String {
        val parts = fullName.split(" ")
        if (parts.isNotEmpty()) {
            return parts[0]
        }
        return fullName
    }
}