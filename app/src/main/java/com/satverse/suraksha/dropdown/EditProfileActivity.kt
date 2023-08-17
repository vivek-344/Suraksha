package com.satverse.suraksha.dropdown

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.satverse.suraksha.LandingPageActivity
import com.satverse.suraksha.R
import com.satverse.suraksha.userlogin.ForgotPasswordActivity
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        lifecycleScope.launch {
            fetchData()
        }
        val updateProfileButton = findViewById<Button>(R.id.update)
        updateProfileButton.setOnClickListener {
            lifecycleScope.launch {
                updateProfile()
            }
        }
    }

    private suspend fun fetchData() {
        val progressDialog = ProgressDialog(this@EditProfileActivity)
        progressDialog.setMessage("Fetching Profile...")
        progressDialog.show()

        val nameEditText = findViewById<TextInputEditText>(R.id.name)
        val emailEditText = findViewById<TextInputEditText>(R.id.email)
        val phoneEditText = findViewById<TextInputEditText>(R.id.phone_no)
        val apartmentEditText = findViewById<TextInputEditText>(R.id.apartment)
        val areaEditText = findViewById<TextInputEditText>(R.id.area)
        val pincodeEditText = findViewById<TextInputEditText>(R.id.pincode)
        val cityEditText = findViewById<TextInputEditText>(R.id.city)
        val stateEditText = findViewById<TextInputEditText>(R.id.state)

        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("64bb859f2d53d0d44e9c")
            .setSelfSigned(true)

        val account = Account(client)
        val userDatabase = Databases(client)

        try {
            val userData = account.listSessions()
            val userId = userData.sessions.firstOrNull()?.userId

            if (userId != null) {
                val data = userDatabase.getDocument(
                    databaseId = "64bc1e13ca662cd39b95",
                    collectionId = "64bc1e1e7465e6d3e4c2",
                    documentId = userId
                )

                val fullName = data.data["fullName"].toString()
                val email = data.data["email"].toString()
                val phoneNumberString = data.data["phoneNumber"].toString()
                val apartment = data.data["apartment"].toString()
                val area = data.data["area"].toString()
                val pincodeString = data.data["pincode"].toString()
                val city = data.data["city"].toString()
                val state = data.data["state"].toString()

                val phoneNumber = phoneNumberString.toDouble().toLong().toString()
                val pincode = pincodeString.toDouble().toLong().toString()

                nameEditText.setText(fullName)
                emailEditText.setText(email)
                phoneEditText.setText(phoneNumber)
                apartmentEditText.setText(apartment)
                areaEditText.setText(area)
                pincodeEditText.setText(pincode)
                cityEditText.setText(city)
                stateEditText.setText(state)
            }
        } catch (e: Exception) {
            Log.e("EditProfileActivity", "Error while listing sessions", e)
        } finally {
            progressDialog.dismiss()
        }
    }

    private suspend fun updateProfile() {
        val progressDialog = ProgressDialog(this@EditProfileActivity)
        progressDialog.setMessage("Updating Profile...")
        progressDialog.show()

        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("64bb859f2d53d0d44e9c")
            .setSelfSigned(true)

        val fullName = findViewById<TextInputEditText>(R.id.name).text.toString()
        val apartment = findViewById<TextInputEditText>(R.id.apartment).text.toString()
        val area = findViewById<TextInputEditText>(R.id.area).text.toString()
        val pincode = findViewById<TextInputEditText>(R.id.pincode).text.toString()
        val city = findViewById<TextInputEditText>(R.id.city).text.toString()
        val state = findViewById<TextInputEditText>(R.id.state).text.toString()

        val account = Account(client)
        val userDatabase = Databases(client)

        try {
            val userData = account.listSessions()
            val userId = userData.sessions.firstOrNull()?.userId

            if (userId != null) {

                userDatabase.updateDocument(
                    databaseId = "64bc1e13ca662cd39b95",
                    collectionId = "64bc1e1e7465e6d3e4c2",
                    documentId = userId,
                    data = mapOf(
                        "fullName" to fullName,
                        "apartment" to apartment,
                        "area" to area,
                        "pincode" to pincode,
                        "city" to city,
                        "state" to state
                    )
                )

                val landingPageIntent = Intent(this@EditProfileActivity, EditProfileActivity::class.java)
                startActivity(landingPageIntent)
                finish()
            }
            runOnUiThread {
                Toast.makeText(this, "Profile Updated!" , Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("EditProfileActivity", "Error while updating profile", e)
        } finally {
            progressDialog.dismiss()
        }
    }
}