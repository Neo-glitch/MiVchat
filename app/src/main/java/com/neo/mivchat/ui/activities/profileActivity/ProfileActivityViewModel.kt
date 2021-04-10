package com.neo.mivchat.ui.activities.profileActivity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class ProfileActivityViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var mReceiverUserId: String  // receiver of friendsRequest
    private var mProfileActivityRepository = ProfileActivityRepository(application)

    /*
   case that UI observes for action
   0 = friend request SENT
   1 = friend request ACCEPTED
   2 = friend request RECEIVED
   3 = friend request NOT YET SENT TO USER i.e NEW OR DECLINED PREVIOUSLY
    */
    val case: MutableLiveData<Int> = mProfileActivityRepository.case

    /*
    0 = hide btn since mCurrentUser viewing own profile
    1 = show btnAdd since mCurrentUser viewing other user profile
     */
    val hideBtnAdd: MutableLiveData<Int> = mProfileActivityRepository.hideBtnAdd

    fun getReceiverUserId(receiverUserId: String){
        mProfileActivityRepository.getReceiverUserId(receiverUserId)
    }

    fun sendFriendRequest(){
        mProfileActivityRepository.sendFriendRequest()
    }

    fun cancelFriendRequest(){
        mProfileActivityRepository.cancelFriendRequest()
    }

    fun acceptFriendRequest(){
        mProfileActivityRepository.acceptFriendRequest()
    }

    fun deleteFriend(){
        mProfileActivityRepository.deleteFriend()
    }

    fun manageFriends(){
        mProfileActivityRepository.manageFriends()
    }
}