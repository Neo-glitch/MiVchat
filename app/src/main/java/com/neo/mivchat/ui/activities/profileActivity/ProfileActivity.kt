package com.neo.mivchat.ui.activities.profileActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.neo.mivchat.R
import com.neo.mivchat.databinding.ActivityProfileBinding
import com.squareup.picasso.Picasso
import kotlin.properties.Delegates

class ProfileActivity : AppCompatActivity() {
    //const
    private val TAG = "ProfileActivity"
    val VISIT_USER_ID = "visit_user_id"
    val VISIT_PROFILE_IMAGE = "profile_image"
    val VISIT_PROFILE_NAME = "profile_name"
    val VISIT_USER_BIO = "user_bio"
    val SENT = "sent"
    val ACCEPTED = "accepted"
    val RECIEVED = "recieved"
    val NEW = "NEW"

    //var
    private lateinit var mReceiverUserId: String  // receiver of friendsRequest
    private lateinit var mReceiverUserImage: String
    private lateinit var mReceiverUserName: String
    private lateinit var mReceiverBio: String

    private val mViewModel by lazy {
        ViewModelProvider(this)[ProfileActivityViewModel::class.java]
    }

    private lateinit var binding: ActivityProfileBinding

    /*
    case that UI observes for action
    0 = friend request SENT
    1 = friend request ACCEPTED
    2 = friend request RECEIVED
    3 = friend request NOT YET SENT TO USER i.e NEW OR DECLINED PREVIOUSLY
    */
    private var caseUI by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
//        setContentView(R.layout.activity_profile)

        val bundle = intent.extras
        mReceiverUserId = bundle?.getString(VISIT_USER_ID)!!
        mReceiverUserImage = bundle.getString(VISIT_PROFILE_IMAGE)!!
        mReceiverUserName = bundle.getString(VISIT_PROFILE_NAME)!!
        mReceiverBio = bundle.getString(VISIT_USER_BIO)!!

        mViewModel.getReceiverUserId(mReceiverUserId)
        mViewModel.case.observe(this, Observer {
            caseUI = it
            initButtonClickListener()
        })

        if (mReceiverUserImage != "") {
            Picasso.get().load(mReceiverUserImage).placeholder(R.drawable.profile_image)
                .into(binding.userImageProfile)
            Picasso.get().load(mReceiverUserImage).placeholder(R.drawable.profile_image)
                .into(binding.userImageProfileSmall)
        }
        binding.userNameProfile.text = mReceiverUserName
        binding.userBioProfile.text = mReceiverBio
    }

    private fun initButtonClickListener() {
        mViewModel.showBtnAdd.observe(this, Observer {
            if(it == 0){
                binding.btnAddFriend.visibility = View.GONE
                binding.btnDeclineRequest.visibility = View.GONE
            } else{
                binding.btnAddFriend.visibility = View.VISIBLE
            }
        })
        mViewModel.showBtnDecline.observe(this, Observer {
            if(it == 1){
                binding.btnDeclineRequest.visibility = View.VISIBLE
            } else if(it == 0){
                binding.btnDeclineRequest.visibility = View.GONE
            }
        })

       handleBtnText(caseUI)

        binding.btnAddFriend.setOnClickListener {
            when (caseUI) {
                0 -> mViewModel.cancelFriendRequest()
                1 -> mViewModel.deleteFriend()
                2 -> mViewModel.acceptFriendRequest()
                3 -> mViewModel.sendFriendRequest()
            }
        }
        binding.btnDeclineRequest.setOnClickListener {
            mViewModel.declineFriendRequest("decline")
        }
    }

    private fun handleBtnText(caseUI: Int) {
        when(caseUI){
            0 -> binding.btnAddFriend.text = "Cancel request"
            1 -> binding.btnAddFriend.text = "Delete Friend"
            2 -> binding.btnAddFriend.text = "Accept Request"
            3 -> binding.btnAddFriend.text = "Add Friend"
        }
    }

    override fun onStart() {
        super.onStart()
        mViewModel.manageFriends()
    }

//    private fun sendFriendRequest() {
//        Log.d(TAG, "sendFriendRequest: starts")
//        mFriendRequestsRef.child(mSenderId).child(mReceiverUserId).child("request_type")
//            .setValue("sent")
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    mFriendRequestsRef.child(mReceiverUserId).child(mSenderId).child("request_type")
//                        .setValue("received")
//                        .addOnCompleteListener { task1 ->
//                            if (task1.isSuccessful) {
//                                case = SENT
//                                btn_add_friend?.text = "Cancel Request"
//                            }
//                        }
//                }
//            }
//    }
//
//    private fun cancelFriendRequest() {
//        Log.d(TAG, "cancelFriendRequest: starts")
//        mFriendRequestsRef.child(mSenderId).child(mReceiverUserId).removeValue()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    mFriendRequestsRef.child(mReceiverUserId).child(mSenderId).removeValue()
//                        .addOnCompleteListener { task1 ->
//                            if (task1.isSuccessful) {
//                                case = NEW
//                                btn_add_friend?.text = "Add Friend"
//                            }
//                        }
//                }
//            }
//    }
//
//    private fun deleteContact() {
//        Log.d(TAG, "deleteContact: starts")
//        mFriendsRef.child(mSenderId).child(mReceiverUserId).removeValue()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    mFriendRequestsRef.child(mReceiverUserId).child(mSenderId).removeValue()
//                        .addOnCompleteListener { task1 ->
//                            if (task1.isSuccessful) {
//                                case = NEW
//                                btn_add_friend?.text = "Add Friend"
//                            }
//                        }
//                }
//            }
//
//    }
//
//    private fun manageClickEvents() {
//        Log.d(TAG, "manageClickEvents: starts")
//        // fun retrieves needed stuff from db incase user leaves and come back to this fragment
//        mFriendRequestsRef.child(mSenderId).addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.hasChild(mReceiverUserId)) { // request has been sent to end user already
//                    var requestType =
//                        snapshot.child(mReceiverUserId).child("request_type").value.toString()
//                    if (requestType == "sent") {
//                        case = SENT
//                        btn_add_friend?.text = "Cancel Request"
//                    }
//                } else {  // request has been accepted by user already or not sent yet
//                    mFriendsRef.child(mSenderId)
//                        .addListenerForSingleValueEvent(object : ValueEventListener {
//                            override fun onDataChange(snapshot: DataSnapshot) {
//                                if (snapshot.hasChild(mReceiverUserId)) {  // request has already been accepted
//                                    case = ACCEPTED
//                                    btn_add_friend?.text = "Delete Contact"
//                                } else {   // no friend request sent
//                                    case = NEW
//                                }
//                            }
//
//                            override fun onCancelled(error: DatabaseError) {}
//                        })
//
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {}
//        })
//
//        if (mSenderId == mReceiverUserId) { // user tapped on own profile
//            btn_add_friend?.visibility = View.GONE
//        } else {
//            btn_add_friend?.setOnClickListener {
//                when (case) {
//                    NEW -> sendFriendRequest()
//                    SENT -> cancelFriendRequest()
//                    RECIEVED -> acceptFriendRequest()
//                    ACCEPTED -> deleteContact()
//
//                }
//            }
//        }
//    }
//
//    private fun acceptFriendRequest() {
////        mFriendsRef.child(mSenderId).child(mReceiverUserId).child("Friend").setValue("Saved")
//        mFriendsRef.child(mSenderId).child(mReceiverUserId).setValue("friend")
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
////                    mFriendsRef.child(mReceiverUserId).child(mSenderId).child("Friend").setValue("Saved")
//                    mFriendsRef.child(mReceiverUserId).child(mSenderId).setValue("friend")
//                        .addOnCompleteListener { task1 ->
//                            if (task1.isSuccessful) {
//                                mFriendRequestsRef.child(mSenderId).child(mReceiverUserId)
//                                    .removeValue()
//                                    .addOnCompleteListener { task2 ->
//                                        if (task2.isSuccessful) {
//                                            mFriendRequestsRef.child(mReceiverUserId)
//                                                .child(mSenderId).removeValue()
//                                                .addOnCompleteListener { task3 ->
//                                                    if (task3.isSuccessful) {
//                                                        btn_add_friend?.text = "Delete Contact"
//                                                        case = ACCEPTED
//                                                    }
//                                                }
//                                        }
//                                    }
//                            }
//                        }
//                }
//            }
//    }
}