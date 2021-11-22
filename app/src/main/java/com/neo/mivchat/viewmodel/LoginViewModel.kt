package com.neo.mivchat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.neo.mivchat.repository.LoginRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val mRepository = LoginRepository(application)

    val showProgress = mRepository.showProgress
    val isLoggedIn = mRepository.isLoggedIn

    fun login(email: String, password: String){
        mRepository.login(email.trim(), password.trim())
    }

    fun onStart(){
        mRepository.onStart()
    }

    fun onStop(){
        mRepository.onStop()
    }
}