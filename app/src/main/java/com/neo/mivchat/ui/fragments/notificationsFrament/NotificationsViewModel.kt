package com.neo.mivchat.ui.fragments.notificationsFrament

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.neo.mivchat.dataSource.database.User

class NotificationsViewModel: ViewModel() {

    companion object{
        private const val TAG = "NotificationsViewModel"
    }

    // firebase
    private val mCurrentUserId by lazy {
        FirebaseAuth.getInstance().currentUser!!.uid
    }

    private val mFirebasedatabaseRef by lazy {
        FirebaseDatabase.getInstance().reference
    }

    private val mUserFriendRequestRef by lazy {
        mFirebasedatabaseRef.child("friend_requests").child(mCurrentUserId)
    }

    private val mFriendRequestsRef by lazy {
        mFirebasedatabaseRef.child("friend_requests")
    }

    private val mUsersRef by lazy {
        mFirebasedatabaseRef.child("users")
    }

    // arrayList to hold all User obj of friend Requests of current user
    var mFriendRequestsLive: MutableLiveData<MutableList<User>> = MutableLiveData()

    // array list to hold all user_id of User added to current user friendRequest
    val mFriendRequestsIds: MutableLiveData<MutableList<String>> = MutableLiveData()


    fun getFriendRequestsIds(){
        var mFriendRequests: MutableList<User> = mutableListOf()
        val query = mFriendRequestsRef.orderByKey().equalTo(mCurrentUserId)
        query.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {userIdDataSnapshot ->
                    val something: HashMap<String, HashMap<String, String>> = userIdDataSnapshot.value as HashMap<String, HashMap<String, String>>
                    
                    something.forEach {
                        val friendRequestUserId = it.key
                        it.value.forEach{
                            if(it.value == "received"){  // adds only user received friendRequest, to be displayed in rv
                                getFriendRequestUserInfoAndUpdateRvList(friendRequestUserId, mFriendRequests)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun getFriendRequestUserInfoAndUpdateRvList(
        userId: String,
        mFriendRequests: MutableList<User>
    ){
        val query = mFirebasedatabaseRef.child("users").orderByKey().equalTo(userId)

        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { dataSnapshot ->
                    val user: User = dataSnapshot.getValue(User::class.java)!!
                    mFriendRequests.add(user)
                    mFriendRequestsLive.value = mFriendRequests
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

//    private val mSource =
//        NotificationsSource()
//
//    fun initAdapter(context: Context): FirebaseRecyclerAdapter<User, NotificationsRvViewHolder> {
//        val firebaseAdapter =
//            NotificationsRvAdapter(
//                context,
//                mSource.getAllFriendRequests()
//            )
//        firebaseAdapter.startListening()
//        return firebaseAdapter
//    }
}