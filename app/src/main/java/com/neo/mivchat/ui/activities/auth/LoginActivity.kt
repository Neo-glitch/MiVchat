package com.neo.mivchat.ui.activities.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.neo.mivchat.ui.dialog.PasswordResetDialog
import com.neo.mivchat.R
import com.neo.mivchat.ui.activities.MainActivity
import com.neo.mivchat.ui.dialog.ResendEmailVerificationDialog
import com.neo.mivchat.util.Helper
import com.neo.mivchat.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String

    // widget
    private val mWindow by lazy {
        window
    }

    private val mViewModel: LoginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // test
        mWindow.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

        mViewModel.isLoggedIn.observe(this, Observer {
            if(it){  // true then user is logged in and email verified
                goToMainActivity()
            }
        })

        mViewModel.showProgress.observe(this, Observer {
            showProgressBar(it)
        })

        init()
    }

    private fun init() {
        btn_signin.setOnClickListener {
            email = textInputLayout_email.editText?.text.toString().trim()
            password = textInputLayout_password.editText?.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                mViewModel.login(email, password)

//                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener { task -> hideProgressBar() }
//                    .addOnFailureListener { exception ->
//                        Toast.makeText(
//                            this@LoginActivity,
//                            "login failed, please check your email and password",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        hideProgressBar()
//                    }
            } else {
                Helper.showMessage(this, "All fields must be filled before login")
            }
        }

        tv_signup.setOnClickListener { _ ->
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        tv_forgotpassword.setOnClickListener {
            val dialog = PasswordResetDialog()
            dialog.show(supportFragmentManager, "dialog_reset_password")
        }

        tv_resend_verification_mail.setOnClickListener {
            val dialog =
                ResendEmailVerificationDialog()
            dialog.show(supportFragmentManager, "dialog_resend_verification_email")
        }

    }

    private fun showProgressBar(showProgress: Boolean) {
        progressBar_login.visibility = if(showProgress) View.VISIBLE else View.INVISIBLE
        btn_signin.visibility = if(showProgress) View.INVISIBLE else View.VISIBLE
    }


    ////////// Firebase Setup  ///////////////
//    private fun setupFirebaseAuth() {
//
//        mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
//            var user: FirebaseUser? = firebaseAuth.currentUser
//
//            if (user != null) {
//                if (user.isEmailVerified) {
//                    // runs only when user is authenticated and user email is verified
//                    Toast.makeText(this, "Authenticated with: " + user.email, Toast.LENGTH_SHORT)
//                        .show()
//                    var intent = Intent(this, MainActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    startActivity(intent)
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//                    finish()
//                } else {
//                    Toast.makeText(
//                        this,
//                        "Email has not been verified, please check your inbox",
//                        Toast.LENGTH_SHORT
//                    ).show()
////                    FirebaseAuth.getInstance().signOut()
//                }
//
//            } else{
//                // do nothing
//            }
//        }
//
//    }

    private fun goToMainActivity() {
        var intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
    //////////////////////////////////////////////

    override fun onStart() {
        super.onStart()
        mViewModel.onStart()
    }

    override fun onStop() {
        super.onStop()
        mViewModel.onStop()
    }
}