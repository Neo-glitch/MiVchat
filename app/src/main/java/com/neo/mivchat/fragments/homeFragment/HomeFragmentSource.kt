package com.neo.mivchat.fragments.homeFragment

import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.neo.mivchat.model.User

class HomeFragmentSource {
    // firebase
    val mUsersRef =
        FirebaseDatabase.getInstance().reference.child("users")

    private val mCurrentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    private val mFriendsRef = FirebaseDatabase.getInstance().reference.child("friends")

    fun getFriends(): FirebaseRecyclerOptions<User> = FirebaseRecyclerOptions.Builder<User>()
        .setQuery(mFriendsRef.child(mCurrentUserId), User::class.java).build()
}