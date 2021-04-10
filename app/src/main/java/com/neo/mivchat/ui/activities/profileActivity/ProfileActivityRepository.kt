package com.neo.mivchat.ui.activities.profileActivity

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivityRepository(application: Application) {

    companion object{
        private const val TAG = "ProfileActivityReposito"
    }


    private val mContext: Application = application

    // firebase
    private val mSenderId by lazy {
        FirebaseAuth.getInstance().uid!!
    }  // current app userId(currentUser), used to send friendReq
    private lateinit var mReceiverUserId: String  // receiver of friendsRequest

    private val mFriendsRef by lazy {
        FirebaseDatabase.getInstance().reference.child("friends")
    }
    private val mFriendRequestsRef by lazy {
        FirebaseDatabase.getInstance().reference.child("friend_requests")
    }

    /*
    case that UI observes for action
    0 = friend request SENT
    1 = friend request ACCEPTED
    2 = friend request RECEIVED
    3 = friend request NOT YET SENT TO USER i.e NEW OR DECLINED PREVIOUSLY
     */
    val case: MutableLiveData<Int> = MutableLiveData()

    /*
    0 = hide btn since mCurrentUser viewing own profile
    1 = show btnAdd since mCurrentUser viewing other user profile
     */
    val showBtnAdd: MutableLiveData<Int> = MutableLiveData()
    val showBtnDecline: MutableLiveData<Int> = MutableLiveData()

    init{
        showBtnDecline.value = 0
    }

    fun getReceiverUserId(receiverUserId: String){
        mReceiverUserId = receiverUserId
    }

    fun sendFriendRequest() {
        Log.d(TAG, "sendFriendRequest: starts")
        mFriendRequestsRef.child(mSenderId).child(mReceiverUserId).child("request_type")
            .setValue("sent")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mFriendRequestsRef.child(mReceiverUserId).child(mSenderId).child("request_type")
                        .setValue("received")
                        .addOnSuccessListener {
                            case.value = 0
                            displayToast("Friend request sent successfully")
                        }
                }
            }
    }

    fun cancelFriendRequest() {
        Log.d(TAG, "cancelFriendRequest: starts")
        mFriendRequestsRef.child(mSenderId).child(mReceiverUserId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mFriendRequestsRef.child(mReceiverUserId).child(mSenderId).removeValue()
                        .addOnSuccessListener {
                            case.value = 3
                            displayToast("Friend request canceled successfully")
                        }
                }
            }
    }

    fun cancelFriendRequest(message: String) {  // for declining friend requests
        Log.d(TAG, "cancelFriendRequest: starts")
        mFriendRequestsRef.child(mSenderId).child(mReceiverUserId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mFriendRequestsRef.child(mReceiverUserId).child(mSenderId).removeValue()
                        .addOnSuccessListener {
                            case.value = 3
                            showBtnDecline.value = 0
                            displayToast("Friend request declined")
                        }
                }
            }
    }

    fun acceptFriendRequest() {
        mFriendsRef.child(mSenderId).child(mReceiverUserId).setValue("friend")
            .addOnCompleteListener{ task->
                if (task.isSuccessful) {
                    mFriendsRef.child(mReceiverUserId).child(mSenderId).setValue("friend")
                        .addOnCompleteListener{ task1 ->
                            if (task1.isSuccessful) {
                                mFriendRequestsRef.child(mSenderId).child(mReceiverUserId)
                                    .removeValue()
                                    .addOnCompleteListener{ task2 ->
                                        if (task2.isSuccessful) {
                                            mFriendRequestsRef.child(mReceiverUserId)
                                                .child(mSenderId).removeValue()
                                                .addOnSuccessListener {
                                                    case.value = 1
                                                    showBtnDecline.value = 0
                                                    displayToast("Friend request accepted successfully")
                                                }
                                        }
                                    }
                            }
                        }
                }
            }
    }

    fun deleteFriend() {
        Log.d(TAG, "deleteContact: starts")
        mFriendsRef.child(mSenderId).child(mReceiverUserId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mFriendsRef.child(mReceiverUserId).child(mSenderId).removeValue()
                        .addOnSuccessListener {
                            case.value = 3
                            displayToast("Friend deleted successfully")
                        }
                }
            }
    }

    fun manageFriends(){
        if(mSenderId == mReceiverUserId){  // user views own profile
            showBtnAdd.value =  0
        } else {  // user views other user profile
            showBtnAdd.value = 1
        }
        // fun retrieves needed stuff from db in case user leaves and come back to this fragment
        mFriendRequestsRef.child(mSenderId).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild(mReceiverUserId)){  // request has been sent to end user or request received from another user
                    handleSentAndReceivedFriendRequest(snapshot)
                } else {  // request has been accepted by user or not yet sent
                    mFriendsRef.child(mSenderId).addValueEventListener(object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            case.value = if(snapshot.hasChild(mReceiverUserId)){
                                1
                            } else {
                                3
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                        }

                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun handleSentAndReceivedFriendRequest(snapshot: DataSnapshot) {
        val requestType = snapshot.child(mReceiverUserId).child("request_type").value.toString()
        if (requestType == "sent") {  // request sent
            case.value = 0
        } else if (requestType == "received") {  // request received from user
            case.value = 2
            showBtnDecline.value = 1
        }
    }

    private fun displayToast(message: String){
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }

}