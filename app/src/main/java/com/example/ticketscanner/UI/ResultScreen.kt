package com.example.ticketscanner.UI

import android.content.Intent
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.ticketscanner.databinding.ActivityResultScreenBinding
import org.json.JSONObject

class ResultScreen : AppCompatActivity() {
    private var TAG: String = "TAG"



    private lateinit var binding: ActivityResultScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()

        val result:String = intent.getStringExtra("result").toString()

        binding.backBtn.setOnClickListener{
            val intent = Intent(applicationContext, HomeScreen::class.java)
            startActivity(intent)
        }


    }
}