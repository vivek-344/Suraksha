package com.satverse.suraksha

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.widget.ImageView

class EmergencyHelplineActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_helpline)

        val emergencyButtons = mapOf(
            R.id.womenButton to "1091",
            R.id.abuseButton to "181",
            R.id.policeButton to "100",
            R.id.childButton to "1098",
            R.id.ambulanceButton to "102",
            R.id.fireBrigadeButton to "101",
            R.id.seniorButton to "14567"
        )

        emergencyButtons.forEach { (buttonId, phoneNumber) ->
            val button = findViewById<ImageView>(buttonId)
            button.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$phoneNumber")
                startActivity(intent)
            }
        }
    }
}
