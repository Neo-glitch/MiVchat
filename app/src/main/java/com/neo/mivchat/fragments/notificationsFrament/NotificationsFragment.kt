package com.neo.mivchat.fragments.notificationsFrament

import android.os.Bundle
import android.text.Layout
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
import com.neo.mivchat.fragments.findFriendsFragment.FindFriendsRvViewHolder
import com.neo.mivchat.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.fragment_notifications.view.*


class NotificationsFragment : Fragment() {
    private var mCurrentUserId: String = ""
    private lateinit var mUsersRef: DatabaseReference
    private lateinit var mFriendsRequestRef: DatabaseReference
    private lateinit var mFriendsRef: DatabaseReference
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
        mUsersRef = FirebaseDatabase.getInstance().reference.child("users")
        mFriendsRequestRef = FirebaseDatabase.getInstance().reference.child("Friend Requests")
        mFriendsRef = FirebaseDatabase.getInstance().reference.child("Friends")

        view.rv_notifications.layoutManager = LinearLayoutManager(requireContext())
        return view
    }


    override fun onStart() {
        super.onStart()

        val options = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(mFriendsRef.child(mCurrentUserId), User::class.java)
            .build()
        val firebaseAdapter = initFirebaseRvAdapter(options)

        rv_notifications.adapter = firebaseAdapter
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
                                mUsersRef.child(listUserId)
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.hasChild("profile_image")) {
                                                val imageUrl =
                                                    snapshot.child("profile_image").value
                                                        .toString()
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
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }
                                    })
                            } else {  // own user, so that shw user in list
                                holder.itemView.visibility = View.GONE
                            }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
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
                                mFriendsRef.child(mCurrentUserId).child(listUserId).removeValue()
                                    .addOnCompleteListener { task3 ->
                                        mFriendsRef.child(listUserId).child(mCurrentUserId)
                                            .removeValue()
                                    }
                            }
                        }
                }
            }
    }

    private fun declineRequest(listUserId: String) {
        mFriendsRef.child(mCurrentUserId).child(listUserId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mFriendsRef.child(listUserId).child(mCurrentUserId).removeValue()
                }
            }
    }


}