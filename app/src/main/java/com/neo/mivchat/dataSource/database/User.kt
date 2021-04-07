package com.neo.mivchat.dataSource.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.neo.mivchat.Constants


@Entity(tableName = Constants.USER_TABLE)
data class User
    (
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = Constants.USER_NAME)
    var name: String? = null,
    @ColumnInfo(name = Constants.USER_IMAGE)
    var profile_image: String? = null,
    @ColumnInfo(name = Constants.USER_ID)
    var user_id: String? = null,
    @ColumnInfo(name = Constants.USER_BIO)
    var bio: String? = null
)