package com.neo.mivchat.ui.fragments.notificationsFrament

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.neo.mivchat.dataSource.database.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class NotificationsViewModel : ViewModel() {

    companion object {
        private const val TAG = "NotificationsViewModel"
    }

    private val mNotificationsRepository = NotificationsRepository()

    // arrayList to hold all User obj of friend Requests of current user
    var mFriendRequestsLive = mNotificationsRepository.mFriendRequestsLive


    fun getFriendRequestsIds() {
        mNotificationsRepository.getFriendRequestsIds()
    }

}