package com.neo.mivchat.ui.fragments.notificationsFrament

import android.content.Context
import androidx.lifecycle.ViewModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.neo.mivchat.ui.fragments.homeFragment.HomeRvAdapter
import com.neo.mivchat.ui.fragments.homeFragment.HomeRvViewHolder
import com.neo.mivchat.model.User

class NotificationsViewModel: ViewModel() {
    private val mSource =
        NotificationsSource()

    fun initAdapter(context: Context): FirebaseRecyclerAdapter<User, HomeRvViewHolder> {
        val firebaseAdapter =
            HomeRvAdapter(
                context,
                mSource.getAllFriendRequests()
            )
        firebaseAdapter.startListening()
        return firebaseAdapter
    }
}