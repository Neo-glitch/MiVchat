package com.neo.mivchat.ui.activities.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.neo.mivchat.R
import com.neo.mivchat.model.User
import com.neo.mivchat.util.Helper
import com.neo.mivchat.viewmodel.RegisterViewModel
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private val mViewModel by lazy {
        ViewModelProvider(this)[RegisterViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mViewModel.showProgressBar.observe(this, Observer { showProgress ->
            showProgressBar(showProgress)
        })

        mViewModel.isRegistered.observe(this, Observer {
            if(it){
                finish()
            }
        })

        register_btn.setOnClickListener {
            if (editText_email.text.toString().isNotEmpty()
                && editText_password.text.toString().isNotEmpty()
                && editText_confirmpassword.text.toString().isNotEmpty()
            ) {

                if (checkPasswords(editText_password.text.toString(), editText_confirmpassword.text.toString())) {
                    // init reg task
                    registerNewUser(
                        editText_email.text.toString(),
                        editText_password.text.toString()
                    )
                } else {
                    Helper.showMessage(this, "Passwords do not match, please check")
                }
            } else{
                Helper.showMessage(this, "All input fields must be filled out")
            }
        }

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

    }

    private fun registerNewUser(email: String, password: String) {
        mViewModel.registerUser(email, password)
//        showProgressBar()
//
//        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    // sendVerification mail
//                    sendVerificationEmail()
//
//                    val user = User()
//                    user.name = email.substring(0, email.indexOf("@"))
//                    user.profile_image = ""
//                    user.user_id = FirebaseAuth.getInstance().currentUser?.uid!!
//                    user.bio = ""
//
//                    FirebaseDatabase.getInstance().reference.child("users")
//                        .child(FirebaseAuth.getInstance().currentUser?.uid!!)
//                        .setValue(user).addOnCompleteListener { task1 ->
//                            redirectToLoginActivity()
//                        }
//                }
//            }.addOnFailureListener {
//                Toast.makeText(this@RegisterActivity, "something went wrong.", Toast.LENGTH_SHORT)
//                    .show()
//                FirebaseAuth.getInstance().signOut()
//
//                redirectToLoginActivity()
//            }

    }

    private fun redirectToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun sendVerificationEmail() {
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "check your mail to verify this account",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "could not send verification mail to your email address",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    private fun checkPasswords(s1: String, s2: String): Boolean {
        // checks if password and confirm password match
        return mViewModel.checkPassWords(s1,s2)
    }

    private fun showProgressBar(showProgress: Boolean) {
        progressBar.visibility = if(showProgress) View.VISIBLE else View.INVISIBLE
        register_btn.visibility = if(showProgress) View.INVISIBLE else View.VISIBLE
    }
}