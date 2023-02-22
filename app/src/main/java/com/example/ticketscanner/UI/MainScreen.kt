package com.example.ticketscanner.UI

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ticketscanner.R
import com.example.ticketscanner.databinding.ActivityMainScreenBinding

class MainScreen : AppCompatActivity() {
    private lateinit var binding: ActivityMainScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()
        var fabVisible = false


        binding.fabAdd.setOnClickListener {
            if (!fabVisible) {

                binding.fabBooking.show()
                binding.fabScanner.show()
                binding.fabBooking.visibility = View.VISIBLE
                binding.fabScanner.visibility = View.VISIBLE


                binding.fabAdd.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_add_circle_24))
                fabVisible = true
            } else {
                binding.fabBooking.hide()
                binding.fabScanner.hide()
                binding.fabBooking.visibility = View.GONE
                binding.fabScanner.visibility = View.GONE

                binding.fabAdd.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_add_circle_24))
                fabVisible = false
            }
        }
        binding.fabBooking.setOnClickListener {
            // on below line we are displaying a toast message.
            Toast.makeText(this@MainScreen, "Home clicked..", Toast.LENGTH_SHORT).show()
        }

        binding.fabScanner.setOnClickListener {
            val intent = Intent(this@MainScreen, ScannerScreen::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        }

    }

    override fun onBackPressed() {
        if (!shouldAllowBack()) {
            return
        }
        super.onBackPressed()
    }

    private fun shouldAllowBack(): Boolean {
        return false
    }
}