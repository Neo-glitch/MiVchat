package com.neo.mivchat.dataSource.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.neo.mivchat.Constants


@Database(entities = [User::class, Friend::class], version = 1, exportSchema = false)
abstract class AppRoomDatabase: RoomDatabase() {

    abstract fun appDao() : AppDao

    companion object{
        private var appRoomDatabase: AppRoomDatabase? = null

        fun getDatabase(context: Context): AppRoomDatabase? {
            if(appRoomDatabase == null){
                synchronized(AppRoomDatabase::class.java){
                    if(appRoomDatabase == null){
                        appRoomDatabase =
                            Room.databaseBuilder<AppRoomDatabase>(
                                context,
                                AppRoomDatabase::class.java,
                                Constants.DATABASE_NAME
                            ).build()
                    }
                }
            }
            return appRoomDatabase
        }
    }


}