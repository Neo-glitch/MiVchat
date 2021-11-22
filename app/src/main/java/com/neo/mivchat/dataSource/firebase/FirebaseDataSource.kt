package com.neo.mivchat.dataSource.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.neo.mivchat.Constants

object FirebaseDataSource {

    private val mAuth = FirebaseAuth.getInstance()
    private val mFirebaseFireStoreDb = FirebaseFirestore.getInstance()

    private val mUsersCollection = mFirebaseFireStoreDb.collection(Constants.USERS_COLLECTION)
    private val mServerCollection = mFirebaseFireStoreDb.collection(Constants.SERVER_COLLECTION)


    fun getAuth(): FirebaseAuth = mAuth
    fun getFirebaseFirestore(): FirebaseFirestore = mFirebaseFireStoreDb

    fun getUsersCollection(): CollectionReference = mUsersCollection
    fun getServerCollection(): CollectionReference = mServerCollection


}