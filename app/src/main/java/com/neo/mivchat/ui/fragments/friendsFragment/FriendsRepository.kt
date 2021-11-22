package com.neo.mivchat.ui.fragments.friendsFragment

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.neo.mivchat.dataSource.database.AppDao
import com.neo.mivchat.dataSource.database.AppRoomDatabase
import com.neo.mivchat.model.Friend
import com.neo.mivchat.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class FriendsRepository(application: Application) {

    companion object{
        private const val TAG = "FriendsRepository"
    }

    private val appDao: AppDao
    private lateinit var allFriends: LiveData<PagedList<User>>

    init{
        val appDb = AppRoomDatabase.getDatabase(application)
        appDao = appDb!!.appDao()
    }

    fun getAllFriends(): LiveData<PagedList<User>>{
        runBlocking (Dispatchers.IO){
            allFriends = LivePagedListBuilder(appDao.getAllFriends(), 8).build()
        }
        return allFriends
    }

    suspend fun deleteAllFiends(){
        appDao.deleteAllFriends()
    }

    suspend fun insertFriend(friend: Friend){
        appDao.insert(friend)
    }
}