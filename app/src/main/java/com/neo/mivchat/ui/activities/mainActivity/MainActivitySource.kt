package com.neo.mivchat.ui.activities.mainActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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