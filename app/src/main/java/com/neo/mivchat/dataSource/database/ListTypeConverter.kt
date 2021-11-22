package com.neo.mivchat.dataSource.database

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson


class ListTypeConverter {

    /*
    converts list friends list to jsonString to be saved to db
     */
    @TypeConverter
    fun fromFriendsList(friends: MutableList<String>): String?{
        if(friends == null){
            return null
        }

        val gson = Gson()
        val type = object : TypeToken<MutableList<String?>?>() {}.type
        var json: String = gson.toJson(friends, type)
        return json
    }

    /*
    gets list of friends from db
     */
    @TypeConverter
    fun toFriendsList(friendJsonString: String): MutableList<String>?{
        if(friendJsonString == null){
            return null
        }

        val gson = Gson()
        val type = object : TypeToken<MutableList<String?>?>() {}.type
        val friends: MutableList<String> = gson.fromJson(friendJsonString, type)
        return friends

    }
}