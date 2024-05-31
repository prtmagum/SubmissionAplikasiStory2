package com.dicoding.submissionaplikasistoryapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.dicoding.submissionaplikasistoryapp.R
import com.dicoding.submissionaplikasistoryapp.view.main.MainActivity

class SplashActivity : AppCompatActivity() {

    companion object {
        const val SPLASH_SCREEN_DELAY_MILLIS = 3000L // 3 seconds
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Use the constant variable for the delay
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_SCREEN_DELAY_MILLIS)
    }
}