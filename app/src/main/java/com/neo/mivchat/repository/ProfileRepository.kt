package com.neo.mivchat.repository

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.neo.mivchat.Constants
import com.neo.mivchat.dataSource.firebase.FirebaseDataSource
import com.neo.mivchat.model.User
import com.neo.mivchat.util.Helper
import java.lang.ref.WeakReference

class ProfileRepository(application: Application) {

    companion object {
        private const val TAG = "ProfileActivityReposito"
    }

    private val mContext: WeakReference<Context> = WeakReference(application)
    private val mCurrentUserId = FirebaseDataSource.getAuth().uid!!
    private lateinit var mUserId: String

    /**
    case that UI observes for action
    0 = I tapped on my own profile, hide to buttons to add, unfriend and all that
    1 = friend request SENT,
    2 = friend request RECEIVED,
    3 = friend request NOT YET SENT TO USER i.e NEW OR DECLINED PREVIOUSLY,
    4 = checking for friend request failed due to network error
    5 = friend request ACCEPTED or is my friend
     **/
    val case: MutableLiveData<Int> = MutableLiveData()
    val totalFriends: MutableLiveData<Int> = MutableLiveData()  // total number of friends a user has

    /*
    0 = hide btn since mCurrentUser viewing own profile
    1 = show btnAdd since mCurrentUser viewing other user profile
     */
    val showBtnAdd: MutableLiveData<Int> = MutableLiveData()
    val showBtnDecline: MutableLiveData<Int> = MutableLiveData()

    init {
        showBtnDecline.value = 0
    }

    /**
     * userId of the user in question
     */
    fun manageFriends(userId: String) {
        mUserId = userId
        if (mCurrentUserId == mUserId) {  // user views own profile, so get his own details and populate screen
            case.value = 0
        } else {  // user views other user profile
            checkIfFriend()
        }

    }

    private fun checkIfFriend() {
        FirebaseDataSource.getUsersCollection().document(mCurrentUserId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if(documentSnapshot.exists()){
                    var user = documentSnapshot.toObject(User::class.java)
                    var friendsList = user!!.friends
                    var isFriend = mUserId in friendsList
                    if(isFriend){
                        case.value = 5
                    } else{
                        checkSentFriendRequest()
                    }
                }
            }
    }


    /**
    get the info needed to populate the profile screen
     **/
    private fun checkSentFriendRequest() {

        val userSentRequestDocRef = FirebaseDataSource.getUsersCollection().document(mCurrentUserId)
            .collection(Constants.NOTIFICATIONS_COLLECTION).document(Constants.SENT_REQUEST_DOC)


        // check if I have sent any request to this user(userId)
        userSentRequestDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.get(Constants.REQUESTS_FIELD_NAME) != null) {
                        val sentRequests: MutableList<String> =
                            documentSnapshot.get(Constants.REQUESTS_FIELD_NAME) as MutableList<String>
                        val isContained = mUserId in sentRequests
                        if (isContained) {
                            case.value = 1   // I have sent a request to this guy
                        } else {              // I didn't sent a request so query db to see if user sent a request to me
                            checkReceivedFriendRequest()
                        }
                    } else {
                        checkReceivedFriendRequest()
                    }
                } else {
                    checkReceivedFriendRequest()
                }
            }.addOnFailureListener {
                Helper.showMessage(mContext.get()!!, "Oops! There appears to be a problem please check your internet")
                case.value = 4  // friend request check failed due to network
            }


    }


    /**
     * userId = user Id that we are checking to see connections with
     * currentUserId = userId of person currently using the app
     */
    private fun checkReceivedFriendRequest() {
        val receivedRequestDocRef = FirebaseDataSource.getUsersCollection().document(mCurrentUserId)
            .collection(Constants.NOTIFICATIONS_COLLECTION).document(Constants.RECEIVED_REQUEST_DOC)

        receivedRequestDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                if (documentSnapshot.get(Constants.REQUESTS_FIELD_NAME) != null) {
                    val receivedRequests: MutableList<String> =
                        documentSnapshot.get(Constants.REQUESTS_FIELD_NAME) as MutableList<String>
                    val isContained = mUserId in receivedRequests
                    if (isContained) {
                        case.value = 2  // I have received a request from this guy
                    } else {
                        case.value = 3  // I haven't received a request from this guy
                    }
                } else {
                    case.value = 3  // I haven't received a request from this guy
                }
            } else {
                case.value = 3 // I haven't received a request from this guy
            }
        }.addOnFailureListener {
            Helper.showMessage(mContext.get()!!, "Oops! There appears to be a problem please check your internet")
            case.value = 4  // friend request check failed due to network
        }
    }

    fun sendFriendRequest() {
        Log.d(TAG, "sendFriendRequest: starts")

    }

    fun acceptFriendRequest() {

    }
    fun cancelFriendRequest() {
        Log.d(TAG, "cancelFriendRequest: starts")

    }

    fun cancelFriendRequest(message: String) {  // for declining friend requests
        Log.d(TAG, "cancelFriendRequest: starts")

    }



    fun deleteFriend() {

    }


}