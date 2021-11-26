package com.neo.mivchat.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.neo.mivchat.R
import com.neo.mivchat.ui.activities.auth.LoginActivity
import com.neo.mivchat.viewmodel.SplashViewModel

class SplashScreenActivity : AppCompatActivity() {
    // widget
    private val mWindow by lazy {
        window
    }

    private val mViewModel: SplashViewModel by lazy {
        ViewModelProvider(this)[SplashViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        Handler().postDelayed(
            object : Runnable {
                override fun run() {
                    initUser()
                }
            }, 750)


        // allows us to modify some things in app status bar
        mWindow.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    fun initUser() {
        val user = mViewModel.mUser
        if (user != null) {
            if (user.isEmailVerified) {
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
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

}