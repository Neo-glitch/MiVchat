package com.neo.mivchat.ui.activities.CallActivity

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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_call.*
import java.util.HashMap

class CallActivity : AppCompatActivity() {

    private val TAG = "CallActivity"

    // firebase
    private val mUsersRef by lazy {
        FirebaseDatabase.getInstance().reference.child("users")
    }

    private val mSenderUserId by lazy {
        FirebaseAuth.getInstance().currentUser?.uid!!
    }

    private lateinit var mReceiverUserId: String
    private lateinit var mReceiverUserImage: String
    private lateinit var mReceiverUserName: String
    private lateinit var mSenderUserImage: String
    private lateinit var mSenderUserName: String

    private var mCancelCall = false   // if true cancel call, else remain in call
    private lateinit var mCallingId: String   // id of callee
    private lateinit var mRingingId: String   // id of caller

    // mediaPlayer for playing ringtone
    private lateinit var mMediaPlayer: MediaPlayer

    // const
    val VISIT_USER_ID = "visit_user_id"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)


        mReceiverUserId = intent?.extras?.getString(VISIT_USER_ID)!!

        retrieveUserInfo()
        cancel_call?.setOnClickListener {
            mCancelCall = true
            cancelCall()
        }

        make_call.setOnClickListener { // only available to callee
            acceptCall()
        }
    }


    private fun acceptCall(){
        var pickupMap = HashMap<String, Any>()
        pickupMap["picked"] = "picked"

        mUsersRef.child(mSenderUserId).child("Ringing").updateChildren(pickupMap).addOnCompleteListener { task ->
            if(task.isSuccessful){
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun cancelCall(){
        // cancel call from sender/caller side
        mUsersRef.child(mSenderUserId).child("Calling").addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists() && snapshot.hasChild("calling")){ // caller cancels first
                    // callee id
                    mCallingId = snapshot.child("calling").value.toString()
                    mUsersRef.child(mCallingId).child("Ringing").removeValue().addOnCompleteListener {task ->
//                        if(task.isSuccessful){
                            mUsersRef.child(mSenderUserId).child("Calling").removeValue().addOnCompleteListener { task1 ->
                                if(task.isSuccessful){
                                    finish()
                                }
                            }
//                        }
                    }
                } else{ // receiver cancels first
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        // cancel call from callee side, used mSenderUserId,since will be id of callee on own device
        mUsersRef.child(mSenderUserId).child("Ringing").addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists() && snapshot.hasChild("ringing")){  // callee cancels first
                    // caller id
                    mRingingId = snapshot.child("ringing").value.toString()

                    mUsersRef.child(mRingingId).child("Calling").removeValue().addOnCompleteListener { task ->
//                        if(task.isSuccessful){
                            mUsersRef.child(mSenderUserId).child("Ringing").removeValue().addOnCompleteListener { task1 ->
                                finish()
                            }
//                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }



    private fun retrieveUserInfo() {
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

        mUsersRef.child(mReceiverUserId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!mCancelCall && !snapshot.hasChild("Calling") && !snapshot.hasChild("Ringing")) {
                        // receiver(callee) is not being called  and call can be cancelled
                        var callingInfo = HashMap<String, Any>()
                        callingInfo["calling"] = mReceiverUserId  // uid of callee

                        mUsersRef.child(mSenderUserId).child("Calling").updateChildren(callingInfo)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val ringingInfo = HashMap<String, Any>()
                                    ringingInfo["ringing"] = mSenderUserId  // uid of caller
                                    mUsersRef.child(mReceiverUserId).child("Ringing")
                                        .updateChildren(ringingInfo)
                                }
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        mUsersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(mSenderUserId).hasChild("Ringing") && !snapshot.child(
                        mSenderUserId
                    ).hasChild("Calling")
                ) {
                    // true if user is receiver of call
                    make_call.visibility = View.VISIBLE
                }
                if (snapshot.child(mReceiverUserId).child("Ringing")
                        .hasChild("picked")
                ) { // other user(callee has picked)
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onDestroy() {
        cancelCall()
        super.onDestroy()
    }
}