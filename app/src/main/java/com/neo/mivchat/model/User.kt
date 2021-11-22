package com.neo.mivchat.model

import androidx.room.*
import com.neo.mivchat.Constants
import com.neo.mivchat.dataSource.database.ListTypeConverter


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
    var bio: String? = null,

    @ColumnInfo(name = "friends")
    @TypeConverters(ListTypeConverter::class)
    var friends: MutableList<String>? = null
)