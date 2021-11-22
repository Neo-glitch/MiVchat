package com.neo.mivchat.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.neo.mivchat.model.Friend
import com.neo.mivchat.model.User
import com.neo.mivchat.ui.fragments.friendsFragment.FriendsRepository
import kotlinx.coroutines.launch

class FriendsFragmentViewModel(application: Application) : AndroidViewModel(application) {
    // firebase DatabaseRef
    private val mFirebaseDatabaseRef: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference
    }
    val mUsersRef =
        mFirebaseDatabaseRef.child("users")

    private val mCurrentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    private val mFriendsRef = FirebaseDatabase.getInstance().reference.child("friends")

    private val mFriendsRepository = FriendsRepository(application)
    val allFriends: LiveData<PagedList<User>>

    companion object {
        private const val TAG = "FriendsFragmentViewMode"
    }

    init {
        allFriends = mFriendsRepository.getAllFriends()
    }

    fun getFriendsUserId() {
        Log.d(TAG, "getFriendsUserId: called")
        val query: Query = mFriendsRef
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch {
                    mFriendsRepository.deleteAllFiends()
                }
                snapshot.children.forEach {it ->
                    if(it.key == mCurrentUserId){  // we are in currentUser node in friends ref
                        it.children.forEach {dataSnapShot ->
                            val friendId: String = dataSnapShot.key.toString()
                            getFriendInfoAndUpdateLocalDb(friendId)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun getFriendInfoAndUpdateLocalDb(friendId: String) {
        Log.d(TAG, "getFriendInfoAndUpdateLocalDb: called")
        val query = mFirebaseDatabaseRef.child("users").orderByKey().equalTo(friendId)
        query
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    viewModelScope.launch {
                        snapshot.children.forEach { dataSnapShot ->
                            val user: User = dataSnapShot.getValue(User::class.java)!!
                            Log.d(TAG, "friend name: ${user.name}")
                            val friend = Friend(null, user.user_id!!)
                            mFriendsRepository.insertFriend(friend)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}