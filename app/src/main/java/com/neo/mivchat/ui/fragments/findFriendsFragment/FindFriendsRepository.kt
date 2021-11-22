package com.neo.mivchat.ui.fragments.findFriendsFragment

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.neo.mivchat.dataSource.database.AppDao
import com.neo.mivchat.dataSource.database.AppRoomDatabase
import com.neo.mivchat.model.User
import kotlinx.coroutines.*

class FindFriendsRepository(application: Application) {

    companion object {
        private const val TAG = "FindFriendsRepository"
    }

    private val appDao: AppDao
    private lateinit var allUsers: LiveData<PagedList<User>>

    init {
        val appDb = AppRoomDatabase.getDatabase(application)
        appDao = appDb!!.appDao()
    }

    fun getAllUsers(): LiveData<PagedList<User>> {
        runBlocking(Dispatchers.IO) {
            allUsers = LivePagedListBuilder(appDao.getAllUsers(), 8).build()
        }
        return allUsers
    }

    suspend fun deleteAllUsers() {
        appDao.deleteAllUsers()
    }

    suspend fun insertUser(user: User) {
        appDao.insert(user)
    }

//    private suspend fun getAllUsersCount(): Int = appDao.getUsersCount()

//    fun isFbUserCountEqualsRoomUserCount(fbUserCount: Int): Boolean {
//        var isEqual = false
//        CoroutineScope(Dispatchers.IO).launch {
//            val roomUserCount = getAllUsersCount()
//            isEqual = roomUserCount == fbUserCount
//        }
//        return isEqual
//    }
}