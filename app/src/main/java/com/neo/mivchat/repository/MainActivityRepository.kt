package com.neo.mivchat.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.neo.mivchat.Constants
import com.neo.mivchat.dataSource.firebase.FirebaseDataSource

class MainActivityRepository(application: Application) {

    private val mContext = application
    val isAuthenticated: MutableLiveData<Boolean> = MutableLiveData() // if null close go back to loginPage
    val isInACall:MutableLiveData<Boolean> = MutableLiveData()

    private lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener

    init {
        isInACall.value = false
        setupFirebaseAuth()
        checkIfInACall()
    }

    private fun setupFirebaseAuth(){
        mAuthStateListener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            if(user == null){
                // not authenticated due to a reason
                isAuthenticated.value = false
            }
        }

    }



    fun onStart(){
        FirebaseDataSource.getAuth().addAuthStateListener(mAuthStateListener)
    }

    fun onStop(){
        FirebaseDataSource.getAuth().removeAuthStateListener(mAuthStateListener)
    }


    private fun checkIfInACall(){
        val user = FirebaseDataSource.getAuth().currentUser

        if(user != null){
            val availabledocRef = FirebaseDataSource.getUsersCollection().document(user.uid)
                .collection(Constants.RING_STATE_COLLECTION)
                .document(Constants.AVAILABLE_DOC)
            availabledocRef.addSnapshotListener { value, error ->
                if(error != null){
                    // error occurred in listening for this snapshot
                    return@addSnapshotListener
                }

                if(value != null && value.exists()){

                    // get the state
                    val state: Boolean? = value.getBoolean("state")
                    isInACall.value = state
                }
            }
        }
    }

}