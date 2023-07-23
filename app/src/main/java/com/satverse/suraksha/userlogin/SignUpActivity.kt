package com.satverse.suraksha.userlogin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.satverse.suraksha.LandingPageActivity
import com.satverse.suraksha.R
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Account
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val logInButton = findViewById<Button>(R.id.login_now)
        logInButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val createAccountButton = findViewById<Button>(R.id.create_account)
        createAccountButton.setOnClickListener {
            lifecycleScope.launch {
                createUser()
            }
        }

    }
    private suspend fun createUser() {
        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("64bb859f2d53d0d44e9c")
            .setSelfSigned(true)

        val fullName = findViewById<EditText>(R.id.name).text.toString()
        val email = findViewById<EditText>(R.id.email).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()

        val users = Account(client)

        try {
            var user = users.create(
                userId = ID.unique(),
                email = email,
                password = password,
                name = fullName,
            )
            Log.d("Appwrite response", user.toString())

            val intent = Intent(this, LandingPageActivity::class.java)
            startActivity(intent)

            isUserLoggedIn()
        }

        catch(e : AppwriteException) {
            runOnUiThread {
                Toast.makeText(this, "Account Creation Failed!" , Toast.LENGTH_SHORT).show()
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