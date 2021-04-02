package com.neo.mivchat.ui.Activities.mainActivity

import androidx.lifecycle.OnLifecycleEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class MainActivitySource {

    val mUsersRef by lazy {
        FirebaseDatabase.getInstance().reference.child("users")
    }

    val mCurrentUserId by lazy {
        FirebaseAuth.getInstance().currentUser!!.uid
    }

    val mFriendsRef by lazy {
        FirebaseDatabase.getInstance().reference.child("friends")
    }
}