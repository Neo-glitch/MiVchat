package com.neo.mivchat.Activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.neo.mivchat.Activities.LoginActivity.LoginActivity
import com.neo.mivchat.Activities.mainActivity.MainActivity
import com.neo.mivchat.R
import kotlinx.android.synthetic.main.activity_splash_screen.*
import kotlinx.coroutines.NonCancellable.start

class SplashScreenActivity : AppCompatActivity() {
    // widget
    private val mWindow by lazy {
        window
    }
    private var user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        Handler().postDelayed({
            fadeAnimation()
        }, 500)


        Handler().postDelayed(
            object : Runnable {
                override fun run() {
                    initUser()
                }
            }, 1600)


        // allows us to modify some things in app status bar
        mWindow.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    fun initUser() {
        if (user != null) {
            if (user!!.isEmailVerified) {
                startMainActivity()
            } else {
                startLoginActivity()
            }
        } else {
            startLoginActivity()
        }
    }

    private fun startLoginActivity() {
        var intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun fadeAnimation(){
        var fadeAnimatorLogo = ObjectAnimator.ofFloat(app_logo_splash, "alpha", 0.0f, 1.0f)
        fadeAnimatorLogo.apply {
            duration = 450
            start()
        }
        var fadeAnimatorLine = ObjectAnimator.ofFloat(line_divider_splash, "alpha", 0.0f, 1.0f)
        fadeAnimatorLine.apply {
            duration = 450
            start()
        }
        app_logo_splash.visibility = View.VISIBLE
        line_divider_splash.visibility = View.VISIBLE
    }
}