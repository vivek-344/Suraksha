@file:Suppress("DEPRECATION")

package com.satverse.suraksha.userlogin

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.checkbox.MaterialCheckBox
import com.satverse.suraksha.R
import com.satverse.suraksha.dropdown.PrivacyPolicyActivity
import io.appwrite.Client
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Account
import io.appwrite.services.Databases
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var termsCheckbox: MaterialCheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val logInButton = findViewById<Button>(R.id.login_now)
        logInButton.setOnClickListener {
            onBackPressed()
        }

        termsCheckbox = findViewById(R.id.termsCheckbox)
        val checkBoxText = "I agree with all Terms of Use & Privacy Policy."
        val spannableString = SpannableString(checkBoxText)
        Log.d("spannable", "$spannableString")

        val termsOfUseClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Handle the click action for "Terms of Use", e.g., open a website
                val intent = Intent(this@SignUpActivity, TermsAndConditionsActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(this@SignUpActivity, R.color.suraksha) // Change to the desired color
                ds.isUnderlineText = false // Optional: Disable underline if not needed
            }
        }

        val privacyPolicyClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Handle the click action for "Privacy Policy", e.g., open a website
                val intent = Intent(this@SignUpActivity, PrivacyPolicyActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(this@SignUpActivity, R.color.suraksha) // Change to the desired color
                ds.isUnderlineText = false // Optional: Disable underline if not needed
            }
        }


        // Apply the clickable spans to specific text portions
        spannableString.setSpan(termsOfUseClickableSpan, 17, 29, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(privacyPolicyClickableSpan, 32, 46, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)

        termsCheckbox.text = spannableString
        termsCheckbox.movementMethod = LinkMovementMethod.getInstance()

        val createAccountButton = findViewById<Button>(R.id.create_account)
        createAccountButton.setOnClickListener {
            createAccountButton.setOnClickListener {
                val emailEditText = findViewById<EditText>(R.id.email)
                val phoneNumberEditText = findViewById<EditText>(R.id.phone_no)
                val pincodeEditText = findViewById<EditText>(R.id.pincode)
                val ageEditText = findViewById<EditText>(R.id.age)
                val fullNameEditText = findViewById<EditText>(R.id.name)

                val fullName = fullNameEditText.text.toString().trim()
                val email = emailEditText.text.toString().trim()
                val phoneNumber = phoneNumberEditText.text.toString().trim()
                val ageText = ageEditText.text.toString().trim()
                val age = ageText.toIntOrNull()
                val pincode = pincodeEditText.text.toString().trim()

                val isTermsAccepted = termsCheckbox.isChecked

                if (!isValidEmail(email)) {
                    Toast.makeText(this, "Invalid Email!", Toast.LENGTH_SHORT).show()
                } else if (age == null || age !in 13..99) {
                    Toast.makeText(this, "Minimum age to register is 13!", Toast.LENGTH_SHORT).show()
                } else if (phoneNumber.length < 10) {
                    Toast.makeText(this, "Invalid Phone Number", Toast.LENGTH_SHORT).show()
                } else if (pincode.length < 6) {
                    Toast.makeText(this, "Invalid Pin Code", Toast.LENGTH_SHORT).show()
                } else if (!isTermsAccepted) {
                    Toast.makeText(this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show()
                } else {
                    lifecycleScope.launch {
                        val createUserJob = async {
                            createUser()
                        }
                        createUserJob.await()

                        val name = getFirstName(fullName)

                        val sharedPreferences = getSharedPreferences("Name", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("Name", name)
                        editor.apply()

                        logInUser()
                    }
                }
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
            .setProject("65fa760fe93c38713a0f")
            .setSelfSigned(true)

        val fullName = findViewById<EditText>(R.id.name).text.toString().trim()
        val email = findViewById<EditText>(R.id.email).text.toString().trim()
        val phoneNumber = findViewById<EditText>(R.id.phone_no).text.toString().trim()
        val age = findViewById<EditText>(R.id.age).text.toString().trim()
        val apartment = findViewById<EditText>(R.id.apartment).text.toString().trim()
        val area = findViewById<EditText>(R.id.area).text.toString().trim()
        val pincode = findViewById<EditText>(R.id.pincode).text.toString().trim()
        val city = findViewById<EditText>(R.id.city).text.toString().trim()
        val state = findViewById<EditText>(R.id.state).text.toString().trim()
        val password = findViewById<EditText>(R.id.password).text.toString()

        val validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.-_"
        val uID = (1..36).map { validChars.random() }.joinToString("")

        try {
            val document = Databases(client).createDocument(
                databaseId = "65fa7775b4fd1085e3e6",
                collectionId = "65fa77f362e01ec73987",
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
                    "password" to password
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
        } finally {
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

            val verificationResponse =
                users.createVerification(url = "https://localhost/suraksha")

            Log.d("Verification response", verificationResponse.toString())

            val intent = Intent(this@SignUpActivity, VerifyEmailActivity::class.java)
            startActivity(intent)

        } catch (e: AppwriteException) {
            runOnUiThread {
                Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show()
            }
            e.printStackTrace()
            progressDialog.dismiss()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex(getString(R.string.email_check))
        return email.matches(emailRegex)
    }

    private fun getFirstName(fullName: String): String {
        val parts = fullName.split(" ")
        if (parts.isNotEmpty()) {
            return parts[0]
        }
        return fullName
    }
}