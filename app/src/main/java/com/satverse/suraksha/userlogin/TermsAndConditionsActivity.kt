package com.satverse.suraksha.userlogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.satverse.suraksha.R

class TermsAndConditionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_conditions)

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            @Suppress("DEPRECATION")
            onBackPressed()
        }
    }
}