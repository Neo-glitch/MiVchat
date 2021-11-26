package com.neo.mivchat.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.neo.mivchat.Constants
import com.neo.mivchat.dataSource.database.ListTypeConverter
import com.neo.mivchat.enum.NotificationType


@Entity(tableName = Constants.NOTIFICATIONS_TABLE)
data class Notification(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    @ColumnInfo(name = Constants.NOTIFICATIONS_USER_ID)
    val user_id: String?,

    @TypeConverters(NotificationTypeConverter::class)
    @ColumnInfo(name = Constants.NOTIFICATIONS_TYPE)
    val notificationType: NotificationType?


)

