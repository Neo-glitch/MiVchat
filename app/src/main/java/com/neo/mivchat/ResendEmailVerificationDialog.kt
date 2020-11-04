package com.neo.mivchat

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.dialog_resend_email_verification.*
import kotlinx.android.synthetic.main.dialog_resend_email_verification.view.*


class ResendEmailVerificationDialog : DialogFragment() {
    private val TAG = "ResendEmailVerification"
    private lateinit var mContext: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View =
            inflater.inflate(R.layout.dialog_resend_email_verification, container, false)
        Log.d(TAG, "onCreateView: resendEmail starts")


        view.tv_confirm_resend.setOnClickListener {
            val email = et_resend_email.text.toString()
            val password = et_resend_password.text.toString()
            if (email.isNotEmpty() && password
                    .isNotEmpty()
            ) {
                //temp auth user using the email and password and then send verification mail
                authenticateAndResendVerification(email, password)
            }
        }

        view.tv_cancel_resend.setOnClickListener {
            dialog?.dismiss()
        }

        return view
    }

    private fun authenticateAndResendVerification(email: String, password: String) {
        // gets auth credential via email and password passed
        val credential: AuthCredential = EmailAuthProvider.getCredential(
            email,
            password
        )

        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    resendVerificationEmail()
                    FirebaseAuth.getInstance().signOut()
                    dialog?.dismiss()
                }
            }.addOnFailureListener {
                Toast.makeText(
                    mContext,
                    "Invalid credentials.\ncheck the email and password entered",
                    Toast.LENGTH_SHORT
                ).show()
                dialog?.dismiss()
            }
    }

    private fun resendVerificationEmail() {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            user.reload()
            user.sendEmailVerification().addOnCompleteListener { task ->
                val text: String = if (task.isSuccessful) {
                    "Verification email has been sent"
                } else {
                    "couldn't send the verification email, try again"
                }
                Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show()
            }
        }

    }

}