package com.example.mc_jjikdan

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({ // 메인 액티비티로 전환
            val mainIntent = Intent(
                this@SplashActivity,
                MainActivity::class.java
            )
            startActivity(mainIntent)
            finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }

    companion object {
        private const val SPLASH_DISPLAY_LENGTH = 2000 // 2초
    }
}
