package com.satverse.suraksha.dropdown

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.satverse.suraksha.LandingPageActivity
import com.satverse.suraksha.R

class HowToUseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_use)

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            val Intent = Intent(this@HowToUseActivity, LandingPageActivity::class.java)
            startActivity(Intent)
        }
    }
}