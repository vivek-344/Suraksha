package com.satverse.suraksha

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class EmergencyHelplineActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_helpline)

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            @Suppress("DEPRECATION")
            onBackPressed()
        }

        val emergencyButtons = mapOf(
            R.id.womenButton to "1091",
            R.id.abuseButton to "181",
            R.id.policeButton to "100",
            R.id.childButton to "1098",
            R.id.ambulanceButton to "102",
            R.id.fireBrigadeButton to "101",
            R.id.accidentButton to "1073",
            R.id.LPGButton to "1906",
            R.id.railwayButton to "1072",
            R.id.touristButton to "1363",
            R.id.cyberButton to "155620",
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        mediaPlayer?.stop()
        @Suppress("DEPRECATION")
        super.onBackPressed()
    }
}
