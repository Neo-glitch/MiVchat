package com.neo.mivchat.ui.fragments.homeFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.neo.mivchat.IMainActivity
import com.neo.mivchat.R
import com.neo.mivchat.model.User
import com.squareup.picasso.Picasso

class HomeRvAdapter(context: Context, options: FirebaseRecyclerOptions<User>) :
    FirebaseRecyclerAdapter<User, HomeRvViewHolder>(options) {

    private lateinit var mListener: IMainActivity
    private var mContext = context
    private lateinit var mUserName: String
    private lateinit var mProfileImageUrl: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRvViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home, parent, false)
        return HomeRvViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeRvViewHolder, position: Int, model: User) {
        val listUserId = getRef(position).key!!   // id of each user in rv list

        HomeFragmentSource().mUsersRef.child(listUserId).addValueEventListener(object :
            ValueEventListener {  // to get info of user in rv list and work
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    mUserName = snapshot.child("name").value.toString()
                    mProfileImageUrl = snapshot.child("profile_image").value.toString()
                    holder.userName.text = mUserName
                    if (mProfileImageUrl != "") {
                        Picasso.get().load(mProfileImageUrl).placeholder(R.drawable.profile_image)
                            .into(holder.userImage)
                    }

                    holder.videoCallBtn.setOnClickListener {
                        mListener.startCallActivity(listUserId)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mListener = mContext as IMainActivity
    }
}