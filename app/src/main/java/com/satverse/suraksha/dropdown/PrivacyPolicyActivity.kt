package com.satverse.suraksha.dropdown

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.satverse.suraksha.R

class PrivacyPolicyActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            @Suppress("DEPRECATION")
            onBackPressed()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        mediaPlayer?.stop()
        @Suppress("DEPRECATION")
        super.onBackPressed()
    }
}