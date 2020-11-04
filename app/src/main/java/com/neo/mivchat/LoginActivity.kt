package com.neo.mivchat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    // firebase
    private lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener   // listens for changes in auth state of user

    private lateinit var email: String
    private lateinit var password: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupFirebaseAuth()
        init()
    }

    private fun init() {
        btn_signin.setOnClickListener {
            email = textInputLayout_email.editText?.text.toString().trim()
            password = textInputLayout_password.editText?.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                showProgressBar()

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task -> hideProgressBar() }
                    .addOnFailureListener { exception ->
                        Toast.makeText(
                            this@LoginActivity,
                            "login failed, please check your email and password",
                            Toast.LENGTH_SHORT
                        ).show()
                        hideProgressBar()
                    }
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "All fields must be filled before login",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        tv_signup.setOnClickListener { _ ->
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        tv_forgotpassword.setOnClickListener {
            val dialog = PasswordResetDialog()
            dialog.show(supportFragmentManager, "dialog_reset_password")
        }

        tv_resend_verification_mail.setOnClickListener {
            val dialog = ResendEmailVerificationDialog()
            dialog.show(supportFragmentManager, "dialog_resend_verification_email")
        }

    }

    private fun showProgressBar() {
        progressBar_login.visibility = View.VISIBLE
        btn_signin.visibility = View.INVISIBLE
    }

    private fun hideProgressBar() {
        if (progressBar_login.visibility == View.VISIBLE) {
            progressBar_login.visibility = View.GONE
            btn_signin.visibility = View.VISIBLE
        }
    }


    ////////// Firebase Setup  ///////////////
    private fun setupFirebaseAuth() {

        mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            var user: FirebaseUser? = firebaseAuth.currentUser

            if (user != null) {
                if (user.isEmailVerified) {
                    // runs only when user is authenticated and user email is verified
                    Toast.makeText(this, "Authenticated with: " + user.email, Toast.LENGTH_SHORT)
                        .show()
                    var intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Email has not been verified, please check your inbox",
                        Toast.LENGTH_SHORT
                    ).show()
//                    FirebaseAuth.getInstance().signOut()
                }

            } else{
                // do nothing
            }
        }

    }
    //////////////////////////////////////////////

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener)
    }
}