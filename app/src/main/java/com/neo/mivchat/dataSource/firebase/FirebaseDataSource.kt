package com.neo.mivchat.dataSource.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.neo.mivchat.Constants

object FirebaseDataSource {


    private val mAuth = FirebaseAuth.getInstance()
    private val mFirebaseFireStoreDb: FirebaseFirestore
    private val mUsersCollection: CollectionReference
    private val mServerCollection: CollectionReference
    init{
        mFirebaseFireStoreDb = FirebaseFirestore.getInstance()


        // need to make failure listener work, firebase fucked up here
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        mFirebaseFireStoreDb.firestoreSettings = settings

        mUsersCollection = mFirebaseFireStoreDb.collection(Constants.USERS_COLLECTION)
        mServerCollection = mFirebaseFireStoreDb.collection(Constants.SERVER_COLLECTION)
    }



    fun getAuth(): FirebaseAuth = mAuth
    fun getFirebaseFirestore(): FirebaseFirestore = mFirebaseFireStoreDb

    fun getUsersCollection(): CollectionReference = mUsersCollection
    fun getServerCollection(): CollectionReference = mServerCollection


}