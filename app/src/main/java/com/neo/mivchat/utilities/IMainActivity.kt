package com.neo.mivchat.utilities

interface IMainActivity {
    fun startCallActivity(listUserId: String)
    fun inflateProfileActivity(receiverUserId: String, receiverUserImage: String, receiverUserName: String, receiverUserBio: String)
    fun acceptRequest(userId: String)
    fun declineRequest(userId: String)
}