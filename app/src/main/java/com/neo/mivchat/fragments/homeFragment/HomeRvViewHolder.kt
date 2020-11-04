package com.neo.mivchat.fragments.homeFragment

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.neo.mivchat.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_home.view.*

class HomeRvViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var userImage = itemView.findViewById<CircleImageView>(R.id.user_image_home)
    var userName = itemView.findViewById<TextView>(R.id.user_name_home)
    var videoCallBtn = itemView.findViewById<ImageView>(R.id.video_call_home)
}