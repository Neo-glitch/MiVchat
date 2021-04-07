package com.neo.mivchat.ui.activities.mainActivity

import androidx.lifecycle.ViewModel
import com.neo.mivchat.R
import com.neo.mivchat.dataSource.database.FragmentTag

class MainActivityViewModel: ViewModel() {
    fun acceptRequest(userId: String) {

    }

    fun declineRequest(userId: String) {

    }

    private val mSource =
        MainActivitySource()

    var mFragmentTags: MutableList<String> = mutableListOf()
    var mFragments: MutableList<FragmentTag> = mutableListOf()

    val mUsersRef = mSource.mUsersRef
    val mFriendsRef = mSource.mFriendsRef
    val mCurrentUserId = mSource.mCurrentUserId

    // prop to hold id of last bottomNav menu item clicked on before activity recreation
    var bottomNavDisplaySelection = R.id.homeFragment


}