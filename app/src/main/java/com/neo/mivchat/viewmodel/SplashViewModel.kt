package com.neo.mivchat.viewmodel

import androidx.lifecycle.ViewModel
import com.neo.mivchat.dataSource.firebase.FirebaseDataSource

class SplashViewModel: ViewModel() {

    val mUser = FirebaseDataSource.getAuth().currentUser
}