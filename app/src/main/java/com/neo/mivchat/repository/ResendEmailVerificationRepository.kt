package com.neo.mivchat.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.neo.mivchat.dataSource.firebase.FirebaseDataSource
import com.neo.mivchat.util.Helper

class ResendEmailVerificationRepository(application: Application) {

    private val mContext = application
    val dismissDialog: MutableLiveData<Boolean> = MutableLiveData()


    fun authAndResendVerification(email: String, password: String) {
        val credential: AuthCredential = EmailAuthProvider.getCredential(email, password)

        FirebaseDataSource.getAuth().signInWithCredential(credential)
            .addOnSuccessListener {
                // user signed in so send verification mail
                resendVerificationEmail()
            }.addOnFailureListener {
                Helper.showMessage(mContext, "Invalid credentials.\ncheck the email and password entered")
                dismissDialog.value = true
            }
    }

    private fun resendVerificationEmail() {
        val user = FirebaseDataSource.getAuth().currentUser

        user?.sendEmailVerification()
            ?.addOnCompleteListener {
                val text: String = if (it.isSuccessful) {
                    "Verification email has been sent"
                } else {
                    "couldn't send the verification email, try again"
                }
                Helper.showMessage(mContext, text)
                dismissDialog.value = true
            }
    }
}