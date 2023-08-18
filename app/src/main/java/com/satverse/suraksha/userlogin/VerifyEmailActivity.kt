package com.satverse.suraksha.userlogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.satverse.suraksha.R

class VerifyEmailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_email)

        Toast.makeText(this, "Verify your email!" , Toast.LENGTH_SHORT).show()
    }
}