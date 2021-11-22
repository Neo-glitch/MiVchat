package com.neo.mivchat.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.neo.mivchat.dataSource.firebase.FirebaseDataSource
import com.neo.mivchat.util.Helper

class LoginRepository(application: Application) {

    private val mContext = application

    val showProgress: MutableLiveData<Boolean> = MutableLiveData()
    val isLoggedIn: MutableLiveData<Boolean> =
        MutableLiveData()  // if true go to mainActivity

    private lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener

    init {
        setupAuthStateListener()
    }

    private fun setupAuthStateListener() {
        mAuthStateListener = FirebaseAuth.AuthStateListener {
            val user: FirebaseUser? = it.currentUser

            if (user != null) {
                if (user.isEmailVerified) {
                    Helper.showMessage(mContext, "Authenticated with: " + user.email)
                    isLoggedIn.value = true
                } else {
                    Helper.showMessage(
                        mContext,
                        "Email has not been verified, please check your inbox"
                    )
                    FirebaseDataSource.getAuth().signOut()
                    isLoggedIn.value = false
                }
            }
        }
    }


    fun login(email: String, password: String) {
        showProgress.value = true

        FirebaseDataSource.getAuth().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { showProgress.value = false }
            .addOnFailureListener {
                Helper.showMessage(mContext, "Login failed, please check your email and password")
                showProgress.value = false
            }

    }

    fun onStart() {
        FirebaseDataSource.getAuth().addAuthStateListener { mAuthStateListener }
    }

    fun onStop() {
        FirebaseDataSource.getAuth().removeAuthStateListener { mAuthStateListener }
    }


    // todo to check if comet account changed, so create a new user
    private fun createCometAccount(user_uid: String, user_name: String){

    }

    // todo login comet chat account for comet chat doings
    private fun loginCometAccount(user_uid: String){

    }

}