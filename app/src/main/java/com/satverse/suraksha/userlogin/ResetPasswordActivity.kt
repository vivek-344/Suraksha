package com.satverse.suraksha.userlogin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.satverse.suraksha.R
import io.appwrite.Client
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Account
import io.appwrite.services.Databases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var uriString: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val changePassword = findViewById<Button>(R.id.change_password)
        changePassword.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                changePassword()
            }
            val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private suspend fun changePassword() {
        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("65fa760fe93c38713a0f")
            .setSelfSigned(true)

        val users = Account(client)
        val userDatabase = Databases(client)

        val password = findViewById<EditText>(R.id.password).text.toString()
        val confirmPassword = findViewById<EditText>(R.id.confirm_password).text.toString()

        val intentData = intent.data

        if (intentData != null) {
            uriString = intentData.toString()
            val userId = intentData.getQueryParameter("userId")
            val secret = intentData.getQueryParameter("secret")

            try {
                users.updateRecovery(
                    userId = userId ?: "",
                    secret = secret ?: "",
                    password = password,
                    passwordAgain = confirmPassword
                )

                userDatabase.updateDocument(
                    databaseId = "65fa7775b4fd1085e3e6",
                    collectionId = "65fa77f362e01ec73987",
                    documentId = userId ?: "",
                    data = mapOf(
                        "password" to password
                    )
                )

                Toast.makeText(this@ResetPasswordActivity, "Password Changed!", Toast.LENGTH_LONG).show()

            } catch (e: AppwriteException) {
                Toast.makeText(this@ResetPasswordActivity, "$e", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }
}
