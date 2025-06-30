package com.example.christ_international

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import com.bumptech.glide.Glide

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val gifImageView: ImageView = findViewById(R.id.gifImageView)

        // Load GIF using Glide
        Glide.with(this)
            .asGif()
            .load(R.raw.splash_gifnew)
            .into(gifImageView)

        // Delay for 3 seconds, then go to MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 7000)
    }
}
