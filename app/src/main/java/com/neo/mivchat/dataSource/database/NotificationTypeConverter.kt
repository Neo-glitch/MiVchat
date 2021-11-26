package com.neo.mivchat.dataSource.database

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.neo.mivchat.enum.NotificationType

class NotificationTypeConverter {

    @TypeConverter
    fun toInt(notificationType: NotificationType) : Int = notificationType.ordinal

    @TypeConverter
    fun fromInt(int: Int): NotificationType = enumValues<NotificationType>()[int]
}