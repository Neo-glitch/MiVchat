package com.neo.mivchat.ui.fragments.notificationsFrament

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.neo.mivchat.R
import com.neo.mivchat.dataSource.database.User
import com.squareup.picasso.Picasso

class NotificationsRvAdapter(options: FirebaseRecyclerOptions<User>) :
    FirebaseRecyclerAdapter<User, NotificationsRvViewHolder>(options) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationsRvViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notifications, parent, false)
        return NotificationsRvViewHolder(
            view
        )
    }

    override fun onBindViewHolder(
        holder: NotificationsRvViewHolder,
        position: Int,
        model: User
    ) {
        val listUserId = getRef(position).key!! // uid of user in rv list

        val requestTypeRef =
            getRef(position).child("request_type").ref // get ref the reqType, value will be sent or Received
        val query = requestTypeRef.orderByKey()
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var type = snapshot.value.toString()
                    if (type == "received") {  // received friend requests
                        holder.itemView.visibility = View.VISIBLE
                        NotificationsRepository().mUsersRef.child(listUserId)
                            .addValueEventListener(object :
                                ValueEventListener {   // queries node of user in rv to get needed details
                                override fun onDataChange(snapshot: DataSnapshot) {
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
//                                        NotificationsRepository()
//                                            .acceptRequest(listUserId)
                                    }
                                    holder.cancelFriendBtn.setOnClickListener {
//                                        NotificationsRepository()
//                                            .declineRequest(listUserId)
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