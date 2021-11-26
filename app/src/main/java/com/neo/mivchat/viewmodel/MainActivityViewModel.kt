package com.neo.mivchat.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.neo.mivchat.repository.MainActivityRepository

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val mRepository = MainActivityRepository(application)

    val isAuthenticated: MutableLiveData<Boolean> = mRepository.isAuthenticated
    val isInACall: MutableLiveData<Boolean> = mRepository.isInACall

    fun onStart(){
        mRepository.onStart()
    }

    fun onStop(){
        mRepository.onStop()
    }

//    private val mContext = application
//
//    // firebase
//    private val mFirebaseDatabaseRef by lazy{
//        FirebaseDatabase.getInstance().reference
//    }
//
//    private val mFriendRequestsRef by lazy {
//        mFirebaseDatabaseRef.child("friend_requests")
//    }
//    val mFriendsRef by lazy {
//        mFirebaseDatabaseRef.child("friends")
//    }
//
//    val mCurrentUserId by lazy {
//        FirebaseAuth.getInstance().currentUser!!.uid
//    }
//
//    val mUsersRef by lazy {
//        mFirebaseDatabaseRef.child("users")
//    }



//    fun acceptRequest(requestUserId: String) {  // id of user se
//        mFriendsRef.child(mCurrentUserId).child(requestUserId).setValue("friend")
//            .addOnSuccessListener {
//                mFriendsRef.child(requestUserId).child(mCurrentUserId).setValue("friend")
//                    .addOnSuccessListener {
//                        mFriendRequestsRef.child(mCurrentUserId).child(requestUserId)
//                            .removeValue()
//                            .addOnSuccessListener {
//                                mFriendRequestsRef.child(requestUserId).child(mCurrentUserId)
//                                    .removeValue().addOnSuccessListener {
//                                        displayToast("Friend Request Accepted")
//                                    }
//                            }
//                    }
//            }
//    }
//
//    fun declineRequest(requestUserId: String) {
//        mFriendRequestsRef.child(mCurrentUserId).child(requestUserId).removeValue()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    mFriendRequestsRef.child(requestUserId).child(mCurrentUserId).removeValue()
//                    displayToast("Friend request rejected")
//                }
//            }
//    }


}