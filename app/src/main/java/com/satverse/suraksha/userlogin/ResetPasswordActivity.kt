package com.satverse.suraksha.userlogin

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.satverse.suraksha.R
import io.appwrite.Client
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Account
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
                finishAffinity()
            }
        }
    }

    suspend fun changePassword() {
        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("64bb859f2d53d0d44e9c")
            .setSelfSigned(true)

        val users = Account(client)

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

                Toast.makeText(this@ResetPasswordActivity, "Password Changed!", Toast.LENGTH_LONG)
                    .show()

            } catch (e: AppwriteException) {
                Toast.makeText(this@ResetPasswordActivity, "$e", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }
}
