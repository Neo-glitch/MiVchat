package com.neo.mivchat.dataSource.database

import androidx.paging.DataSource.*
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface AppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(friends: Friend)

    @Query("select * from find_friends_table order by name")
    fun getAllUsers(): Factory<Int, User>

    // gets all users that are friends with Current user logged in
    @Query(
        "select find_friends_table.id, find_friends_table.name, find_friends_table.profile_image, find_friends_table.user_id, find_friends_table.bio from find_friends_table inner join friends_table on friends_table.user_id = find_friends_table.user_id")
    fun getAllFriends(): Factory<Int, User>

    @Query("delete from find_friends_table")
    suspend fun deleteAllUsers()

    @Query("delete from friends_table")
    suspend fun deleteAllFriends()

    /*
    gets count of user object in friendsTable
    if count user object in local db != firebase db
    update db
     */
//    @Query("select count() from find_friends_table")
//    suspend fun getUsersCount(): Int
//
//    @Query("select count() from friends_table")
//    suspend fun getAllFriendsCount(): Int





}