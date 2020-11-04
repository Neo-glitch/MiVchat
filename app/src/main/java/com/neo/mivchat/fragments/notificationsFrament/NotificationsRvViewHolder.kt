package com.neo.mivchat.fragments.notificationsFrament

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.neo.mivchat.R
import de.hdodenhof.circleimageview.CircleImageView

class NotificationsRvViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var userImage = itemView.findViewById<CircleImageView>(R.id.user_image_home)
    var userName = itemView.findViewById<TextView>(R.id.user_name_home)
    var addFriendBtn = itemView.findViewById<Button>(R.id.btn_accept)
    var cancelFriendBtn = itemView.findViewById<Button>(R.id.btn_decline)
}