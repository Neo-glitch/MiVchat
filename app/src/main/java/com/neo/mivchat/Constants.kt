package com.neo.mivchat

class Constants {

    companion object{
//        const val DATABASE_NAME = "app_database"
//
        // user table table name and cols
        const val USER_TABLE = "find_friends_table"
        const val USER_NAME = "name"
        const val USER_IMAGE = "profile_image"
        const val USER_ID = "user_id"
        const val USER_BIO = "bio"

        // friends table name and cols
        const val FRIENDS_TABLE = "friends_table"
        const val FRIEND_USER_ID = "user_id"
        const val USER = "user"

        // notifications table name and cols( not really needed now)
        const val FRIEND_REQUEST_TABLE = "friend_request_table"
        const val FRIEND_REQUEST_SENDER_ID = "user_id"    // id of friend request sender


        // new stuff
        const val USERS_COLLECTION = "users"
        const val RING_STATE_COLLECTION = "ring_state"
        const val AVAILABLE_DOC = "available"

        const val NOTIFICATIONS_COLLECTION = "notifications"
        const val SENT_REQUEST_DOC = "sent_request"
        const val RECEIVED_REQUEST_DOC = "received_request"
        const val MISSED_CALLS_DOC = "missed_calls"


        const val SERVER_COLLECTION = "server"
        const val KEYS_DOC = "keys"
        const val KEYS_CHANGED_DOC = "keys_changed"


    }

}