package com.neo.mivchat.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.google.firebase.database.*
import com.neo.mivchat.model.User
import com.neo.mivchat.ui.fragments.findFriendsFragment.FindFriendsRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FindFriendsFragmentViewModel(application: Application) : AndroidViewModel(application) {

    // firebase DatabaseRef
    private val mFirebaseDatabaseRef: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference
    }
    private val mFindFriendsRepository: FindFriendsRepository = FindFriendsRepository(application)
    val allUsers: LiveData<PagedList<User>>

    companion object {
        private const val TAG = "FindFriendsFragment"
    }

    init {
        allUsers = mFindFriendsRepository.getAllUsers()
    }

    fun getAllUsersFromFirebaseAndUpdateDb() {
        val query: Query = mFirebaseDatabaseRef.child("users")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch {
                    mFindFriendsRepository.deleteAllUsers()
                    snapshot.children.forEach { dataSnapShot ->
                        if(dataSnapShot.key.toString() != "dummy"){
                            val user: User = dataSnapShot.getValue(User::class.java)!!
                            runBlocking {
                                mFindFriendsRepository.insertUser(user)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: called with ${error.details}")
            }
        })
    }
}