package com.neo.mivchat.ui.activities.mainActivity

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.neo.mivchat.R
import com.neo.mivchat.dataSource.database.FragmentTag

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val mContext = application

    // firebase
    private val mFirebaseDatabaseRef by lazy{
        FirebaseDatabase.getInstance().reference
    }

    private val mFriendRequestsRef by lazy {
        mFirebaseDatabaseRef.child("friend_requests")
    }
    val mFriendsRef by lazy {
        mFirebaseDatabaseRef.child("friends")
    }

    val mCurrentUserId by lazy {
        FirebaseAuth.getInstance().currentUser!!.uid
    }

    val mUsersRef by lazy {
        mFirebaseDatabaseRef.child("users")
    }



    fun acceptRequest(requestUserId: String) {  // id of user se
//        mFriendsRef.child(mCurrentUserId).child(requestUserId).child("friends").setValue("saved")
        mFriendsRef.child(mCurrentUserId).child(requestUserId).setValue("friend")
            .addOnSuccessListener {
//                mFriendsRef.child(requestUserId).child(mCurrentUserId).child("friends").setValue("saved")
                mFriendsRef.child(requestUserId).child(mCurrentUserId).setValue("friend")
                    .addOnSuccessListener {
                        mFriendRequestsRef.child(mCurrentUserId).child(requestUserId)
                            .removeValue()
                            .addOnSuccessListener {
                                mFriendRequestsRef.child(requestUserId).child(mCurrentUserId)
                                    .removeValue().addOnSuccessListener {
                                        displayToast("Friend Request Accepted")
                                    }
                            }
                    }
            }
    }

    fun declineRequest(requestUserId: String) {
        mFriendRequestsRef.child(mCurrentUserId).child(requestUserId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mFriendRequestsRef.child(requestUserId).child(mCurrentUserId).removeValue()
                    displayToast("Friend request rejected")
                }
            }
    }

//    val mUsersRef = mSource.mUsersRef
//    val mFriendsRef = mSource.mFriendsRef
//    val mCurrentUserId = mSource.mCurrentUserId


    private fun displayToast(message: String){
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }
}