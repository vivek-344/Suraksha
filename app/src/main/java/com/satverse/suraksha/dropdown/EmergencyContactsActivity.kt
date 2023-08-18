package com.satverse.suraksha.dropdown

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.satverse.suraksha.LandingPageActivity
import com.satverse.suraksha.R

class EmergencyContactsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_contacts)

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            val Intent = Intent(this@EmergencyContactsActivity, LandingPageActivity::class.java)
            startActivity(Intent)
        }
    }
}