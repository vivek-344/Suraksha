package com.satverse.suraksha

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class AboutUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        val twitter = findViewById<Button>(R.id.twitterButton)
        twitter.setOnClickListener {
            val url = "https://www.example.com"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }

        val github = findViewById<Button>(R.id.githubButton)
        github.setOnClickListener {
            val url = "https://www.example.com"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }

        val rateus = findViewById<Button>(R.id.rateusButton)
        rateus.setOnClickListener {
            val url = "https://www.example.com"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }

        val vivekTwitter = findViewById<Button>(R.id.vivekTwitterButton)
        vivekTwitter.setOnClickListener {
            val url = "https://www.example.com"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }

        val vivekGitHub = findViewById<Button>(R.id.vivekGitHubButton)
        vivekGitHub.setOnClickListener {
            val url = "https://www.example.com"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }

        val satyamTwitter = findViewById<Button>(R.id.satyamTwitterButton)
        satyamTwitter.setOnClickListener {
            val url = "https://www.example.com"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }

        val satyamGitHub = findViewById<Button>(R.id.satyamGitHubButton)
        satyamGitHub.setOnClickListener {
            val url = "https://www.example.com"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }

        val shreyaTwitter = findViewById<Button>(R.id.shreyaTwitterButton)
        shreyaTwitter.setOnClickListener {
            val url = "https://www.example.com"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }

        val shreyaGitHub = findViewById<Button>(R.id.shreyaGitHubButton)
        shreyaGitHub.setOnClickListener {
            val url = "https://www.example.com"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No web browser available", Toast.LENGTH_SHORT).show()
            }
        }
    }
}