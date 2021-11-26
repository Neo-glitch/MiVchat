package com.neo.mivchat.interfaces

interface OnNotificationItemClickedListener {
    fun onAcceptRequest(userId: String)
    fun onDeclineRequest(userId: String)
    fun onCallNotification(userId: String)
}