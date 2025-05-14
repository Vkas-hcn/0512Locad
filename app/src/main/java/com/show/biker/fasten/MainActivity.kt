package com.show.biker.fasten

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bamboo.cane.shoes.horses.tool.time.AdLimiter3

class MainActivity : AppCompatActivity() {
    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//        val button1 = findViewById<Button>(R.id.button2)
//        val button2 = findViewById<Button>(R.id.button3)
//        val button3 = findViewById<Button>(R.id.button4)
//
//        val adLimiter = AdLimiter3(this)
//
//        button1.setOnClickListener {
//            val state = adLimiter.canShowAd(false)
//            Log.e("TAG", "onCreate: state=$state", )
//        }
//        button2.setOnClickListener {
//            adLimiter.recordAdShown()
//        }
//        button3.setOnClickListener {
//            adLimiter.recordAdClicked()
//        }
    }
}