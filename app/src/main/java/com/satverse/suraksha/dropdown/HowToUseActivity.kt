package com.satverse.suraksha.dropdown

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.satverse.suraksha.R


class HowToUseActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_use)

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