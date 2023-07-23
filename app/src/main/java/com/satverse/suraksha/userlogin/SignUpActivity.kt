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
import io.appwrite.services.Databases
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
        val phoneNumber = findViewById<EditText>(R.id.phone_no).text
        val age = findViewById<EditText>(R.id.age).text
        val apartment = findViewById<EditText>(R.id.apartment).text.toString()
        val area = findViewById<EditText>(R.id.area).text.toString()
        val pincode = findViewById<EditText>(R.id.pincode).text
        val city = findViewById<EditText>(R.id.city).text.toString()
        val state = findViewById<EditText>(R.id.state).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()

        val users = Account(client)
        val userData = Databases(client)

        try {
            val document = userData.createDocument(
                databaseId = "64bc1e13ca662cd39b95",
                collectionId = "64bc1e1e7465e6d3e4c2",
                documentId = ID.unique(),
                data = mapOf(
                    "fullName" to fullName,
                    "email" to email,
                    "phoneNumber" to phoneNumber,
                    "age" to age,
                    "apartment" to apartment,
                    "area" to area,
                    "pinCode" to pincode,
                    "city" to city,
                    "state" to state,
                ),
            )

            Log.d("Appwrite response", document.toString())

            val user = users.create(
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
                Toast.makeText(this, "$e" , Toast.LENGTH_SHORT).show()
            }
            e.printStackTrace()
        }
    }

    private fun isUserLoggedIn() {
        val sharedPref = getSharedPreferences("logIn", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("LoggedIn", true)
        editor.apply()
    }
}