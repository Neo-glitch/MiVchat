package com.neo.mivchat

interface IMainActivity {
    fun startCallActivity(listUserId: String)
    fun inflateProfileFragment(receiverUserId: String, receiverUserImage: String, receiverUserName: String, receiverUserBio: String)
}