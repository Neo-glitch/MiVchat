package com.neo.mivchat.ui.fragments.notificationsFrament

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.neo.mivchat.model.User

class NotificationsRepository {
    companion object{
        private const val TAG = "NotificationsRepository"
    }

    val mUsersRef by lazy {
        FirebaseDatabase.getInstance().reference.child("users")
    }
    private val mFriendRequestsRef by lazy {
        FirebaseDatabase.getInstance().reference.child("friend_requests")
    }

    private val mCurrentUserId by lazy {
        FirebaseAuth.getInstance().currentUser!!.uid
    }

    private val mFirebasedatabaseRef by lazy {
        FirebaseDatabase.getInstance().reference
    }



    // arrayList to hold all User obj of friend Requests of current user
    var mFriendRequestsLive: MutableLiveData<MutableList<User>> = MutableLiveData()
    var mFriendRequestIds = mutableListOf<String>()
    private val mFriendRequests = mutableListOf<User>()


    fun getAllFriendRequests(): FirebaseRecyclerOptions<User> =
        FirebaseRecyclerOptions.Builder<User>()
            .setQuery(mFriendRequestsRef.child(mCurrentUserId), User::class.java)
            .build()

    fun getFriendRequestsIds() {
        val query = mFriendRequestsRef
        query.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                mFriendRequestIds.clear()
                mFriendRequests.clear()
                val something: HashMap<String, HashMap<String, HashMap<String, String>>> =
                    snapshot.value as HashMap<String, HashMap<String, HashMap<String, String>>>

                something.forEach {
                    Log.d(TAG, "again: ${it.key}")
                    if (it.key == mCurrentUserId && it.key != "dummy") {
                        it.value.forEach {
                            Log.d(TAG, "onDataChange: test ${it.key}")
                            val friendRequestUserId = it.key
                            it.value.forEach {
                                Log.d(TAG, "onDataChange: tch ${it.value}")
                                if (it.value == "received") {  // only add to list, if received friendRequest
                                    mFriendRequestIds.add(friendRequestUserId)
                                }
                            }
                        }
                        return@forEach
                    }
                }
                getFriendRequestUserInfoAndUpdateRvList(mFriendRequestIds)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun getFriendRequestUserInfoAndUpdateRvList(
        mFriendRequestIds: MutableList<String>
    ) {
        if (mFriendRequestIds.size > 0) {
            for (userId: String in mFriendRequestIds) {
                val query = mFirebasedatabaseRef.child("users").orderByKey().equalTo(userId)
                query.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { dataSnapshot ->
                            val user: User = dataSnapshot.getValue(User::class.java)!!
                            Log.d(TAG, "User is: ${user.name}")
                            mFriendRequests.add(user)
                        }
                        mFriendRequestsLive.value = mFriendRequests
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
        } else {
            mFriendRequestsLive.value = mFriendRequests
        }
    }
}