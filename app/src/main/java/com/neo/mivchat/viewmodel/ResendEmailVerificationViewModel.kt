package com.neo.mivchat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.neo.mivchat.repository.ResendEmailVerificationRepository

class ResendEmailVerificationViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository = ResendEmailVerificationRepository(application)
    val dismissDialog = mRepository.dismissDialog

    fun authAndResendVerificationMail(email: String, password: String){
        mRepository.authAndResendVerificationMail(email.trim(),password.trim())
    }
}