package com.neo.mivchat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.neo.mivchat.repository.PasswordResetRepository

class PasswordResetViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository = PasswordResetRepository(application)
    val dismissDialog = mRepository.dismissDialog

    fun sendPassWordResetEmail(email: String){
        mRepository.sendPasswordResetEmail(email.trim())
    }
}