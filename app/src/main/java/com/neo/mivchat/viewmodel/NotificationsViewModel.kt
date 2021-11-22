package com.neo.mivchat.viewmodel

import androidx.lifecycle.ViewModel
import com.neo.mivchat.ui.fragments.notificationsFrament.NotificationsRepository

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