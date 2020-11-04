package com.neo.mivchat.fragments.findFriendsFragment

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.neo.mivchat.R
import de.hdodenhof.circleimageview.CircleImageView

class FindFriendsRvViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val userName = itemView.findViewById<TextView>(R.id.user_name_findfriends)
    val userImage = itemView.findViewById<CircleImageView>(R.id.user_image_findfriends)
    val userBio = itemView.findViewById<TextView>(R.id.user_bio_findfriends)
}