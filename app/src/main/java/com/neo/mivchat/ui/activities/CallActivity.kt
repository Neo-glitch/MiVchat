package com.neo.mivchat.ui.activities

import android.app.Activity
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.neo.mivchat.R
import com.neo.mivchat.databinding.ActivityCallBinding
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_call.*
import java.util.HashMap

class CallActivity : AppCompatActivity() {

    private val TAG = "CallActivity"

    // firebase
    private val mUsersRef by lazy {
        FirebaseDatabase.getInstance().reference.child("users")
    }

    private val mSenderUserId by lazy {  // current App User
        FirebaseAuth.getInstance().currentUser?.uid!!
    }

    private val mFirebaseDatabaseRef by lazy {
        FirebaseDatabase.getInstance().reference
    }

    private lateinit var mReceiverUserId: String   // id of user being called by app current user
    private lateinit var mReceiverUserImage: String
    private lateinit var mReceiverUserName: String
    private lateinit var mSenderUserImage: String
    private lateinit var mSenderUserName: String

    private var mCancelCall = false   // if true cancel call, else remain in call
    private lateinit var mCallingId: String   // id of callee(person being called)
    private lateinit var mRingingId: String   // id of caller

    // mediaPlayer for playing ringtone
    private lateinit var mMediaPlayer: MediaPlayer

    private lateinit var binding: ActivityCallBinding

    // const
    val VISIT_USER_ID = "visit_user_id"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCallBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mReceiverUserId = intent?.extras?.getString(VISIT_USER_ID)!!

        retrieveUserInfo()
        binding.cancelCall.setOnClickListener {
            mCancelCall = true
            cancelCall()
        }

        binding.makeCall.setOnClickListener { // only available to callee
            acceptCall()
        }
    }


    private fun acceptCall(){
//        var pickupMap = HashMap<String, Any>()
//        pickupMap["picked"] = "picked"
//
//        mUsersRef.child(mSenderUserId).child("Ringing").updateChildren(pickupMap).addOnCompleteListener { task ->
//            if(task.isSuccessful){
//                setResult(Activity.RESULT_OK)
//                finish()
//            }
//        }

        // my way to accept call
        val acceptedMap = HashMap<String, String>()
        acceptedMap["accepted"] = "accepted"

        // mSenderId is receiver of call here
        mFirebaseDatabaseRef.child("call_node").child(mSenderUserId).setValue(acceptedMap)
            .addOnSuccessListener {
                setResult(Activity.RESULT_OK)
                finish()
            }
    }

    private fun cancelCall(){
//        // cancel call from sender/caller side
//        mUsersRef.child(mSenderUserId).child("Calling").addListenerForSingleValueEvent(object:
//            ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if(snapshot.exists() && snapshot.hasChild("calling")){ // caller cancels first
//                    // callee id
//                    mCallingId = snapshot.child("calling").value.toString()
//                    mUsersRef.child(mCallingId).child("Ringing").removeValue().addOnCompleteListener {task ->
////                        if(task.isSuccessful){
//                            mUsersRef.child(mSenderUserId).child("Calling").removeValue().addOnCompleteListener { task1 ->
//                                if(task.isSuccessful){
//                                    finish()
//                                }
//                            }
////                        }
//                    }
//                } else{ // receiver cancels first
//                    finish()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
//
//        // cancel call from callee side, used mSenderUserId,since will be id of callee on own device
//        mUsersRef.child(mSenderUserId).child("Ringing").addListenerForSingleValueEvent(object:
//            ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if(snapshot.exists() && snapshot.hasChild("ringing")){  // callee cancels first
//                    // caller id
//                    mRingingId = snapshot.child("ringing").value.toString()
//
//                    mUsersRef.child(mRingingId).child("Calling").removeValue().addOnCompleteListener { task ->
////                        if(task.isSuccessful){
//                            mUsersRef.child(mSenderUserId).child("Ringing").removeValue().addOnCompleteListener { task1 ->
//                                finish()
//                            }
////                        }
//                    }
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })


        // cancel call my way from caller side
        mFirebaseDatabaseRef.child("call_node").child(mSenderUserId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild("calling")){  // caller has canceled call first
                    mCallingId = snapshot.child("calling").value.toString()  // callee id
                    mFirebaseDatabaseRef.child("call_node").child(mCallingId).child("called_by").removeValue()
                        .addOnSuccessListener {
                            mFirebaseDatabaseRef.child("call_node").child(mSenderUserId).child("calling").removeValue().addOnSuccessListener {
                                finish()
                            }
                        }
                } else{  // callee cancelled call b4 caller
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        // cancel call from callee side, mSenderId since will be id of callee on his own device
        mFirebaseDatabaseRef.child("call_node").child(mSenderUserId).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild("called_by")){
                    // caller id
                    val mCallerId = snapshot.child("called_by").value.toString()

                    mFirebaseDatabaseRef.child("call_node").child(mCallerId).child("calling").removeValue()
                        .addOnSuccessListener {
                            mFirebaseDatabaseRef.child("call_node").child(mSenderUserId).child("called_by").removeValue().addOnSuccessListener {
                                finish()
                            }
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }



    private fun retrieveUserInfo() {  // gets info of user being called
        mUsersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(mReceiverUserId)) {  // update ui on callerScreen
                    mReceiverUserImage =
                        snapshot.child(mReceiverUserId).child("profile_image").value.toString()
                    mReceiverUserName =
                        snapshot.child(mReceiverUserId).child("name").value.toString()
                    Log.d(TAG, "onDataChange: $mReceiverUserName")
                    name_calling.text = mReceiverUserName
                    if (mReceiverUserImage != "") {
                        Picasso.get().load(mReceiverUserImage).placeholder(R.drawable.profile_image)
                            .into(profile_image_calling)
                    }
                }
                else if (snapshot.hasChild(mSenderUserId)) {  // updates ui on calleeScreen
                    mSenderUserImage =
                        snapshot.child(mSenderUserId).child("profile_image").value.toString()
                    mSenderUserName = snapshot.child(mSenderUserId).child("name").value.toString()
                    name_calling.text = mSenderUserName
                    if(mSenderUserImage != ""){
                        Picasso.get().load(mSenderUserImage).placeholder(R.drawable.profile_image)
                            .into(profile_image_calling)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    override fun onStart() {
        super.onStart()

//        mUsersRef.child(mReceiverUserId)
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (!mCancelCall && !snapshot.hasChild("Calling") && !snapshot.hasChild("Ringing")) {
//                        // receiver(callee) is not being called  and call can be cancelled
//                        var callingInfo = HashMap<String, Any>()
//                        callingInfo["calling"] = mReceiverUserId  // uid of callee
//
//                        mUsersRef.child(mSenderUserId).child("Calling").updateChildren(callingInfo)
//                            .addOnCompleteListener { task ->
//                                if (task.isSuccessful) {
//                                    val ringingInfo = HashMap<String, Any>()
//                                    ringingInfo["ringing"] = mSenderUserId  // uid of caller
//                                    mUsersRef.child(mReceiverUserId).child("Ringing")
//                                        .updateChildren(ringingInfo)
//                                }
//                            }
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//            })


        // second way my way
        mFirebaseDatabaseRef.child("call_node").child(mReceiverUserId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                    if(!mCancelCall && !snapshot.hasChild("calling")
                        && !snapshot.hasChild("called_by")){

                        var callMap = HashMap<String, String>()
                        callMap["calling"] = mReceiverUserId // uid of callee

                        updateFbDbAndMakeCall(callMap)
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        handleBtnVisibilityAndReturn()


//        mUsersRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.child(mSenderUserId).hasChild("Ringing") && !snapshot.child(
//                        mSenderUserId
//                    ).hasChild("Calling")
//                ) {
//                    // true if user is receiver of call
//                    binding.makeCall.visibility = View.VISIBLE
//                }
//                if (snapshot.child(mReceiverUserId).child("Ringing")
//                        .hasChild("picked")
//                ) { // other user(callee has picked)
//                    setResult(Activity.RESULT_OK)
//                    finish()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//        })
    }

    private fun handleBtnVisibilityAndReturn() {
        mFirebaseDatabaseRef.child("call_node").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(mSenderUserId).hasChild("called_by") &&
                    !snapshot.child(mSenderUserId).hasChild("calling")){
                    // true if current app user is receiver of call, so make answer btn visible
                    binding.makeCall.visibility = View.VISIBLE
                }

                if(snapshot.child(mReceiverUserId).hasChild("accepted")){
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun updateFbDbAndMakeCall(callMap: HashMap<String, String>) {
        mFirebaseDatabaseRef.child("call_node").child(mSenderUserId).setValue(callMap)
            .addOnSuccessListener {
                val ringMap = HashMap<String, String>()
                ringMap["called_by"] = mSenderUserId // uid of caller
                mFirebaseDatabaseRef.child("call_node").child(mReceiverUserId).setValue(ringMap)
            }
    }

    override fun onDestroy() {
        cancelCall()
        super.onDestroy()
    }
}