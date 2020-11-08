package com.neo.mivchat.fragments.findFriendsFragment

import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.neo.mivchat.model.User

class FindFriendsFragmentSource {
    // firebase
    private val mUsersRef by lazy {
        FirebaseDatabase.getInstance().reference.child("users")
    }

    fun getAllUsers(): FirebaseRecyclerOptions<User>  =
        FirebaseRecyclerOptions.Builder<User>().setQuery(
            mUsersRef.orderByChild("name"), User::class.java)
            .build()


}