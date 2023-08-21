package com.satverse.suraksha

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AboutUsActivity : AppCompatActivity() {

    var mediaPlayer: MediaPlayer? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            onBackPressed()
        }

        fun openURL(url: String) {
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)

            // Special handling for LinkedIn URLs
            if (url.contains("linkedin.com")) {
                intent.setDataAndType(uri, "text/html")
                intent.addCategory(Intent.CATEGORY_BROWSABLE)
            }

            val chooser = Intent.createChooser(intent, "Open with")
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(chooser)
            } else {
                Toast.makeText(this, "No application available to handle the request", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<ImageView>(R.id.logo_image).setOnClickListener {
            openURL("https://www.github.com/Satverse")
        }

        findViewById<TextView>(R.id.textSuraksha).setOnClickListener {
            openURL("https://www.instagram.com/satverse.tech")
        }

        findViewById<Button>(R.id.twitterButton).setOnClickListener {
            openURL("https://www.twitter.com/satverse_tech")
        }

        findViewById<Button>(R.id.websiteButton).setOnClickListener {
            openURL("https://www.satverse.co/suraksha")
        }

        findViewById<Button>(R.id.rateusButton).setOnClickListener {
            openURL("https://play.google.com/store/apps/details?id=com.satverse.suraksha")
        }

        findViewById<Button>(R.id.vivekTwitterButton).setOnClickListener {
            openURL("https://twitter.com/vivekraj344")
        }

        findViewById<Button>(R.id.vivekGitHubButton).setOnClickListener {
            openURL("https://www.github.com/vivek-344")
        }

        findViewById<ImageView>(R.id.vivek_image).setOnClickListener {
            openURL("https://www.instagram.com/vivek.4343/")
        }

        findViewById<Button>(R.id.satyamLinkedinButton).setOnClickListener {
            openURL("https://linkedin.com/in/iamsatyam17")
        }

        findViewById<Button>(R.id.satyamGitHubButton).setOnClickListener {
            openURL("https://www.github.com/satyamsharma17")
        }

        findViewById<ImageView>(R.id.satyam_image).setOnClickListener {
            openURL("https://www.instagram.com/iamsatyam17/")
        }

        findViewById<Button>(R.id.shreyaLinkedinButton).setOnClickListener {
            openURL("https://linkedin.com/in/shreya-shukla-8b87a3231")
        }

        findViewById<Button>(R.id.shreyaGitHubButton).setOnClickListener {
            openURL("https://www.github.com/Shreya984")
        }

        findViewById<Button>(R.id.yashTwitterButton).setOnClickListener {
            openURL("https://www.twitter.com/yash_darbar16")
        }

        findViewById<Button>(R.id.yashGitHubButton).setOnClickListener {
            openURL("https://www.github.com/yashdarbar")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        mediaPlayer?.stop()
        super.onBackPressed()
    }
}