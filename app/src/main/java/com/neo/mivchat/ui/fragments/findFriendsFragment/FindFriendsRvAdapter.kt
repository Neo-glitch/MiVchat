package com.neo.mivchat.ui.fragments.findFriendsFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.neo.mivchat.IMainActivity
import com.neo.mivchat.R
import com.neo.mivchat.model.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class FindFriendsRvAdapter(context: Context, options: FirebaseRecyclerOptions<User>) :
    FirebaseRecyclerAdapter<User, FindFriendsRvViewHolder>(options) {

    private lateinit var mListener: IMainActivity
    private var mContext = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindFriendsRvViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_find_friends, parent, false)
        return FindFriendsRvViewHolder(
            view
        )
    }

    override fun onBindViewHolder(holder: FindFriendsRvViewHolder, position: Int, model: User) {
        val listUserId = getRef(position).key!!
        holder.userName.text = model.name
        holder.userBio.text = model.bio
        if (model.profile_image != "") {
            Picasso.get().load(model.profile_image)
                .placeholder(R.drawable.profile_image)
                .into(holder.userImage)
        }
        holder.itemView.setOnClickListener {
            mListener.inflateProfileFragment(
                listUserId,
                model.profile_image!!,
                model.name!!,
                model.bio!!
            )
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mListener = mContext as IMainActivity
    }
}