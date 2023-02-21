package com.example.ticketscanner.UI

import android.animation.Animator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.airbnb.lottie.LottieAnimationView
import com.example.ticketscanner.R

class EndScreenSuccess : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_screen_success)
        supportActionBar?.hide()


        val animationView = findViewById<LottieAnimationView>(R.id.animation_view)
        animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                //after the animation ends it goes back to homescreen again
                val intent = Intent(this@EndScreenSuccess, HomeScreen::class.java)
                startActivity(intent)
                finish()
            }

            override fun onAnimationCancel(animation: Animator) {
                TODO("Not yet implemented")
            }

            override fun onAnimationRepeat(animation: Animator) {
                TODO("Not yet implemented")
            }

        })

        animationView.playAnimation()


    }
}