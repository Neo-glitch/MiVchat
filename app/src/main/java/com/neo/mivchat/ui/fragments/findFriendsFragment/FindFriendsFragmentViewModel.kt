package com.neo.mivchat.ui.fragments.findFriendsFragment

import android.content.Context
import androidx.lifecycle.ViewModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.neo.mivchat.model.User

class FindFriendsFragmentViewModel: ViewModel() {

    private val mSource: FindFriendsFragmentSource =
        FindFriendsFragmentSource()

    fun initAdapter(context: Context): FirebaseRecyclerAdapter<User, FindFriendsRvViewHolder>{
        val firebaseAdapter =
            FindFriendsRvAdapter(
                context,
                mSource.getAllUsers()
            )
        firebaseAdapter.startListening()
        return firebaseAdapter
    }
}