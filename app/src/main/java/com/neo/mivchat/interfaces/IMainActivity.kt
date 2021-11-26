package com.neo.mivchat.interfaces

interface IMainActivity {
    fun startCallActivity(listUserId: String)
    fun inflateProfileActivity(receiverUserId: String, receiverUserImage: String, receiverUserName: String, receiverUserBio: String)
}