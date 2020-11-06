package com.neo.mivchat.fragments.notificationsFrament

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.neo.mivchat.R
import com.neo.mivchat.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_notifications.view.*


class NotificationsFragment : Fragment() {
    private val TAG = "NotificationsFragment"
    private var mCurrentUserId: String = ""
    private val mUsersRef by lazy {
        FirebaseDatabase.getInstance().reference.child("users")
    }
    private val mFriendRequestsRef by lazy {
        FirebaseDatabase.getInstance().reference.child("friend_requests")
    }
    private val mFriendsRef by lazy {
        FirebaseDatabase.getInstance().reference.child("friends")
    }
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)

        mAuth = FirebaseAuth.getInstance()
        mCurrentUserId = mAuth.currentUser?.uid!!

        view.rv_notifications.layoutManager = LinearLayoutManager(requireContext())
        return view
    }


    override fun onStart() {
        super.onStart()

        val options = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(mFriendRequestsRef.child(mCurrentUserId), User::class.java)
            .build()
        val firebaseAdapter = initFirebaseRvAdapter(options)

        view?.rv_notifications?.adapter = firebaseAdapter
        firebaseAdapter.startListening()
    }

    private fun initFirebaseRvAdapter(options: FirebaseRecyclerOptions<User>): FirebaseRecyclerAdapter<User, NotificationsRvViewHolder> {
        return object : FirebaseRecyclerAdapter<User, NotificationsRvViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): NotificationsRvViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_notifications, parent, false)
                return NotificationsRvViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: NotificationsRvViewHolder,
                position: Int,
                model: User
            ) {
                var listUserId = getRef(position).key!! // uid of user in rv list

                var requestTypeRef =
                    getRef(position).child("request_type").ref // get ref the reqType, value will be sent or Received
                var query = requestTypeRef.orderByKey()
                query.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            var type = snapshot.value.toString()
                            if (type == "received") {  // received friend requests
                                holder.itemView.visibility = View.VISIBLE
                                mUsersRef.child(listUserId)
                                    .addValueEventListener(object :
                                        ValueEventListener {   // queries node of user in rv to get needed details
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val imageUrl =
                                                snapshot.child("profile_image").value
                                                    .toString()
                                            Log.d(TAG, "onDataChange: $imageUrl")
                                            if (imageUrl != "") {
                                                Picasso.get().load(imageUrl)
                                                    .placeholder(R.drawable.profile_image)
                                                    .into(holder.userImage)
                                            }
                                            holder.userName.text =
                                                (snapshot.child("name").value.toString())
                                            holder.addFriendBtn.setOnClickListener {
                                                acceptRequest(listUserId)
                                            }
                                            holder.cancelFriendBtn.setOnClickListener {
                                                declineRequest(listUserId)
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                        }
                                    })
                            } else {  // own user, so don't shw user in list
                                holder.itemView.visibility = View.GONE
                            }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })

            }

        }

    }

    private fun acceptRequest(listUserId: String) {
        mFriendsRef.child(mCurrentUserId).child(listUserId).child("friends").setValue("saved")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mFriendsRef.child(listUserId).child(mCurrentUserId).child("friends")
                        .setValue("saved")
                        .addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                mFriendRequestsRef.child(mCurrentUserId).child(listUserId)
                                    .removeValue()
                                    .addOnCompleteListener { task3 ->
                                        mFriendRequestsRef.child(listUserId).child(mCurrentUserId)
                                            .removeValue()
                                    }
                            }
                        }
                }
            }
    }

    private fun declineRequest(listUserId: String) {
        mFriendRequestsRef.child(mCurrentUserId).child(listUserId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mFriendRequestsRef.child(listUserId).child(mCurrentUserId).removeValue()
                }
            }
    }


}