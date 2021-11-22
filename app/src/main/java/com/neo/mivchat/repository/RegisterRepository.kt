package com.neo.mivchat.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.snapshot.BooleanNode
import com.neo.mivchat.Constants
import com.neo.mivchat.dataSource.firebase.FirebaseDataSource
import com.neo.mivchat.model.User
import com.neo.mivchat.util.Helper

class RegisterRepository constructor(application: Application) {

    var showProgressBar: MutableLiveData<Boolean> = MutableLiveData() // true, then show progress
    var isRegistered: MutableLiveData<Boolean> = MutableLiveData()   // true if reg, finish this activity
    private val mContext = application


    fun registerUser(email: String, password: String) {
        showProgressBar.value = true

        FirebaseDataSource.getAuth().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // SendVerification Email
                sendVerificationEmail()

                val user = User()
                user.name = email.substring(0, email.indexOf("@"))
                user.profile_image = ""
                user.user_id = FirebaseDataSource.getAuth().currentUser?.uid!!
                user.bio = ""
                user.friends = mutableListOf()  // empty list on account creation


                FirebaseDataSource.getFirebaseFirestore().collection(Constants.USERS_COLLECTION)
                    .document(FirebaseDataSource.getAuth().currentUser?.uid!!)
                    .set(user)
                    .addOnSuccessListener {
                        isRegistered.value = true
                        FirebaseDataSource.getAuth().signOut()
                    }
            }.addOnFailureListener {
                showProgressBar.value = false
                Helper.showMessage(mContext, "Something went wrong. please check your internet and try again")
                FirebaseDataSource.getAuth().signOut()
            }

    }

    private fun sendVerificationEmail() {
        val user: FirebaseUser? = FirebaseDataSource.getAuth().currentUser

        user?.sendEmailVerification()
            ?.addOnCompleteListener {
                showProgressBar.value = false
                if (it.isSuccessful)
                    Helper.showMessage(mContext, "check your mail to verify this account")
                else
                    Helper.showMessage(
                        mContext,
                        "could not send verification mail to your email address"
                    )
            }
    }

    private fun createCometUser(){

    }


}