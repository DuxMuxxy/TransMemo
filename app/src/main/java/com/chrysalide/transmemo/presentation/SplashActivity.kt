package com.chrysalide.transmemo.presentation

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.ComponentActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity: ComponentActivity() {

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
