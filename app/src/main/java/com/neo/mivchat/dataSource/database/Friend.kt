package com.neo.mivchat.dataSource.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.neo.mivchat.Constants


@Entity(tableName = Constants.FRIENDS_TABLE)
data class Friend (
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    @ColumnInfo(name = Constants.FRIEND_USER_ID)
    val user_id: String
//    @ColumnInfo(name = Constants.USER)
//    val user: User
)