package com.satverse.suraksha

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.w3c.dom.Node
import java.util.Collections.swap
import java.util.LinkedList

class LandingPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)
    }

    override fun onBackPressed() {

        finishAffinity()
    }
}