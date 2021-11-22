package com.neo.mivchat.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.neo.mivchat.dataSource.firebase.FirebaseDataSource
import com.neo.mivchat.util.Helper


class PasswordResetRepository(application: Application) {
    private val mContext = application
    val dismissDialog: MutableLiveData<Boolean> = MutableLiveData()

    fun sendPasswordResetEmail(email: String){
        FirebaseDataSource.getAuth().sendPasswordResetEmail(email)
            .addOnCompleteListener {
                val text: String = if (it.isSuccessful) {
                    "PassWord reset link sent to email"
                } else {
                    "No user is associated with that email"
                }
                Helper.showMessage(mContext, text)
                dismissDialog.value = true
            }.addOnFailureListener { exception ->
                Helper.showMessage(mContext, "Something appeared to have gone wrong, please check your internet and try again")
                dismissDialog.value = true
            }
    }
}