package com.neo.mivchat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.neo.mivchat.repository.RegisterRepository

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private var mRepository = RegisterRepository(application)

    var showProgressBar = mRepository.showProgressBar
    var isRegistered = mRepository.isRegistered  // to track if registration is successful


    fun registerUser(email: String, password: String){
        mRepository.registerUser(email.trim(), password.trim())
    }

    fun checkPassWords(s1: String, s2: String): Boolean{
        return s1.trim() == s2.trim()
    }




}