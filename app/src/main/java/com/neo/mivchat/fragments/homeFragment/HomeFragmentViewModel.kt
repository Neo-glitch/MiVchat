package com.neo.mivchat.fragments.homeFragment

import android.content.Context
import androidx.lifecycle.ViewModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.neo.mivchat.fragments.findFriendsFragment.FindFriendsRvViewHolder
import com.neo.mivchat.model.User

class HomeFragmentViewModel: ViewModel() {
    private val mSource = HomeFragmentSource()

    fun initAdapter(context: Context): FirebaseRecyclerAdapter<User, HomeRvViewHolder>{
        val firebaseAdapter = HomeRvAdapter(context, mSource.getFriends())
        firebaseAdapter.startListening()
        return firebaseAdapter
    }
}