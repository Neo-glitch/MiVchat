package com.neo.mivchat

interface IMainActivity {
    fun inflateCallFragment(listUserId: String)
    fun inflateProfileFragment(receiverUserId: String, receiverUserImage: String, receiverUserName: String)
}