package com.satverse.suraksha.userlogin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.satverse.suraksha.R
import io.appwrite.Client
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Account
import kotlinx.coroutines.*

class ForgotPasswordActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val sendMail = findViewById<Button>(R.id.sendMail)
        sendMail.setOnClickListener {
            lifecycleScope.launch {
                createRecovery()
            }
        }
    }

    suspend fun createRecovery() {
        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("65fa760fe93c38713a0f")
            .setSelfSigned(true)

        val email = findViewById<EditText>(R.id.email).text.toString().trim()

        val users = Account(client)

        try {
            users.createRecovery(email = email, url = "https://localhost/resetpassword")

            Toast.makeText(this@ForgotPasswordActivity, "Reset link has been sent!", Toast.LENGTH_LONG).show()

            val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
            startActivity(intent)

        } catch (e: AppwriteException) {

            Toast.makeText(this@ForgotPasswordActivity, "$e", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}