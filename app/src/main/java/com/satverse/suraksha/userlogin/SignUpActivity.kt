package com.satverse.suraksha.userlogin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.satverse.suraksha.R
import io.appwrite.Client
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Account
import io.appwrite.services.Databases
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

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
                val createUserJob = async {
                    createUser()
                }
                createUserJob.await()

                logInUser()
            }
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun createUser() {
        val progressDialog = ProgressDialog(this@SignUpActivity)
        progressDialog.setMessage("Registering Account...")
        progressDialog.show()

        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("64bb859f2d53d0d44e9c")
            .setSelfSigned(true)

        val fullName = findViewById<EditText>(R.id.name).text.toString()
        val email = findViewById<EditText>(R.id.email).text.toString()
        val phoneNumber = findViewById<EditText>(R.id.phone_no).text.toString()
        val age = findViewById<EditText>(R.id.age).text.toString()
        val apartment = findViewById<EditText>(R.id.apartment).text.toString()
        val area = findViewById<EditText>(R.id.area).text.toString()
        val pincode = findViewById<EditText>(R.id.pincode).text.toString()
        val city = findViewById<EditText>(R.id.city).text.toString()
        val state = findViewById<EditText>(R.id.state).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()

        val validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.-_"
        val uID = (1..36).map { validChars.random() }.joinToString("")

        try {
            val document = Databases(client).createDocument(
                databaseId = "64bc1e13ca662cd39b95",
                collectionId = "64bc1e1e7465e6d3e4c2",
                documentId = uID,
                data = mapOf(
                    "fullName" to fullName,
                    "email" to email,
                    "phoneNumber" to phoneNumber,
                    "age" to age,
                    "apartment" to apartment,
                    "area" to area,
                    "pincode" to pincode,
                    "city" to city,
                    "state" to state,
                ),
            )

            Log.d("Appwrite response", document.toString())

            val user = Account(client).create(
                userId = uID,
                email = email,
                password = password,
                name = fullName,
            )

            Log.d("Appwrite response", user.toString())

        } catch (e: AppwriteException) {
            runOnUiThread {
                Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show()
            }
            e.printStackTrace()
        }
        finally {
            progressDialog.dismiss()
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun logInUser() {

        val progressDialog = ProgressDialog(this@SignUpActivity)
        progressDialog.setMessage("Logging In...")
        progressDialog.show()

        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("64bb859f2d53d0d44e9c")
            .setSelfSigned(true)

        val email = findViewById<EditText>(R.id.email).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()

        val users = Account(client)

        try {
            val user = users.createEmailSession(
                email = email,
                password = password,
            )
            Log.d("Appwrite response", user.toString())

            users.createVerification(url = "https://localhost/suraksha")

            Toast.makeText(this, "Verify your Email!" , Toast.LENGTH_SHORT).show()

//            val intent = Intent(this, EmailVerificationActivity::class.java)
//            startActivity(intent)
        }

        catch(e : AppwriteException) {
            runOnUiThread {
                Toast.makeText(this, "$e" , Toast.LENGTH_SHORT).show()
            }
            e.printStackTrace()
        }
        finally {
            progressDialog.dismiss()
        }
    }
}