package com.satverse.suraksha

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class AboutUsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            val Intent = Intent(this@AboutUsActivity, LandingPageActivity::class.java)
            startActivity(Intent)
        }

        val satverse = findViewById<ImageView>(R.id.logo_image)
        satverse.setOnClickListener {
            val url = "https://www.github.com/Satverse"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }

        val Instagram = findViewById<TextView>(R.id.textSuraksha)
        Instagram.setOnClickListener {
            val url = "https://www.instagram.com/satverse.tech"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }

        val twitter = findViewById<Button>(R.id.twitterButton)
        twitter.setOnClickListener {
            val twitterUsername = "satverse_tech"
            val appUri = Uri.parse("twitter://user?screen_name=$twitterUsername")
            val webUrl = Uri.parse("https://www.twitter.com/$twitterUsername")

            val intent = Intent(Intent.ACTION_VIEW, appUri)
            val packageManager = packageManager

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                val webIntent = Intent(Intent.ACTION_VIEW, webUrl)

                if (webIntent.resolveActivity(packageManager) != null) {
                    startActivity(webIntent)
                } else {
                    Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
                }
            }
        }



        val github = findViewById<Button>(R.id.websiteButton)
        github.setOnClickListener {
            val url = "https://www.satverse.co/suraksha"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }

        val rateus = findViewById<Button>(R.id.rateusButton)
        rateus.setOnClickListener {
            val url = "https://play.google.com/store/apps/details?id=com.satverse.suraksha"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }

        val vivekTwitter = findViewById<Button>(R.id.vivekTwitterButton)
        vivekTwitter.setOnClickListener {
            val url = "https://twitter.com/vivekraj344"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }

        val vivekGitHub = findViewById<Button>(R.id.vivekGitHubButton)
        vivekGitHub.setOnClickListener {
            val url = "https://www.github.com/vivek-344"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }

        val vivek = findViewById<ImageView>(R.id.vivek_image)
        vivek.setOnClickListener {
            val url = "https://www.instagram.com/vivek.4343/"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }

        val satyamTwitter = findViewById<Button>(R.id.satyamLinkedinButton)
        satyamTwitter.setOnClickListener {
            val url = "https://linkedin.com/in/iamsatyam17"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }

        val satyamGitHub = findViewById<Button>(R.id.satyamGitHubButton)
        satyamGitHub.setOnClickListener {
            val url = "https://www.github.com/satyamsharma17"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }

        val satyam = findViewById<ImageView>(R.id.satyam_image)
        satyam.setOnClickListener {
            val url = "https://www.instagram.com/iamsatyam17/"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }

        val shreyaTwitter = findViewById<Button>(R.id.shreyaLinkedinButton)
        shreyaTwitter.setOnClickListener {
            val url = "https://linkedin.com/in/shreya-shukla-8b87a3231"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }

        val shreyaGitHub = findViewById<Button>(R.id.shreyaGitHubButton)
        shreyaGitHub.setOnClickListener {
            val url = "https://www.github.com/Shreya984"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }

        val yashTwitter = findViewById<Button>(R.id.yashTwitterButton)
        yashTwitter.setOnClickListener {
            val url = "https://www.twitter.com/yash_darbar16"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }

        val yashGitHub = findViewById<Button>(R.id.yashGitHubButton)
        yashGitHub.setOnClickListener {
            val url = "https://www.github.com/yashdarbar"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }
    }
}