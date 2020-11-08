package com.neo.mivchat.fragments.notificationsFrament

import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.neo.mivchat.model.User

class NotificationsSource {
    val mUsersRef by lazy {
        FirebaseDatabase.getInstance().reference.child("users")
    }
    private val mFriendRequestsRef by lazy {
        FirebaseDatabase.getInstance().reference.child("friend_requests")
    }
    private val mFriendsRef by lazy {
        FirebaseDatabase.getInstance().reference.child("friends")
    }

    private val mCurrentUserId by lazy {
        FirebaseAuth.getInstance().currentUser!!.uid
    }


    fun getAllFriendRequests(): FirebaseRecyclerOptions<User> =
        FirebaseRecyclerOptions.Builder<User>()
            .setQuery(mFriendRequestsRef.child(mCurrentUserId), User::class.java)
            .build()

    fun acceptRequest(listUserId: String) {
        mFriendsRef.child(mCurrentUserId).child(listUserId).child("friends").setValue("saved")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mFriendsRef.child(listUserId).child(mCurrentUserId).child("friends")
                        .setValue("saved")
                        .addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                mFriendRequestsRef.child(mCurrentUserId).child(listUserId)
                                    .removeValue()
                                    .addOnCompleteListener { task3 ->
                                        mFriendRequestsRef.child(listUserId).child(mCurrentUserId)
                                            .removeValue()
                                    }
                            }
                        }
                }
            }
    }

    fun declineRequest(listUserId: String) {
        mFriendRequestsRef.child(mCurrentUserId).child(listUserId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mFriendRequestsRef.child(listUserId).child(mCurrentUserId).removeValue()
                }
            }
    }
}