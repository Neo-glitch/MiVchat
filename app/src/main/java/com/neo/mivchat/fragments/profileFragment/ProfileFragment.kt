package com.neo.mivchat.fragments.profileFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.neo.mivchat.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*


class ProfileFragment : Fragment() {

    //const
    private val TAG = "ProfileFragment"
    val VISIT_USER_ID = "visit_user_id"
    val VISIT_PROFILE_IMAGE = "profile_image"
    val VISIT_PROFILE_NAME = "profile_name"
    val SENT = "sent"
    val ACCEPTED = "accepted"
    val RECIEVED = "recieved"
    val NEW = "NEW"

    //var
    private lateinit var mReceiverUserId: String  // receiver of friendsRequest
    private lateinit var mReceiverUserImage: String
    private lateinit var  mReceiverUserName: String
    private val mSenderId by lazy {
        mAuth.currentUser?.uid!!
    }  // my own id(currentUser), used to send friendReq
    private val calledBy = "" // to hold info of user calling currentUser
    private var case = NEW

    // firebase
    private val mUsersRef by lazy {
        FirebaseDatabase.getInstance().reference.child("users")
    }
    private val mFriendsRef by lazy {
        FirebaseDatabase.getInstance().reference.child("friends")
    }
    private val mFriendRequestsRef by lazy {
        FirebaseDatabase.getInstance().reference.child("friend_requests")
    }
    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle: Bundle? = arguments
        mReceiverUserId = bundle?.getString(VISIT_USER_ID)!!
        mReceiverUserImage = bundle.getString(VISIT_PROFILE_IMAGE)!!
        mReceiverUserName = bundle.getString(VISIT_PROFILE_NAME)!!

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_profile, container, false)

        if(mReceiverUserImage != ""){
            Picasso.get().load(mReceiverUserImage).placeholder(R.drawable.profile_image).into(view.user_image_profile)
            Picasso.get().load(mReceiverUserImage).placeholder(R.drawable.profile_image).into(view.user_image_profile_small)
        }
        view.user_name_profile.text = mReceiverUserName

        manageClickEvents()
        return view
    }

    override fun onStart() {
        super.onStart()
        manageClickEvents()
    }

    private fun sendFriendRequest() {
        Log.d(TAG, "sendFriendRequest: starts")
        mFriendRequestsRef.child(mSenderId).child(mReceiverUserId).child("request_type")
            .setValue("sent")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mFriendRequestsRef.child(mReceiverUserId).child(mSenderId).child("request_type")
                        .setValue("received")
                        .addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                case = SENT
                                view?.btn_add_friend?.text = "Cancel Request"
                            }
                        }
                }
            }
    }

    private fun cancelFriendRequest() {
        Log.d(TAG, "cancelFriendRequest: starts")
        mFriendRequestsRef.child(mSenderId).child(mReceiverUserId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mFriendRequestsRef.child(mReceiverUserId).child(mSenderId).removeValue()
                        .addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                case = NEW
                                view?.btn_add_friend?.text = "Add Friend"
                            }
                        }
                }
            }
    }

    private fun deleteContact() {
        Log.d(TAG, "deleteContact: starts")
        mFriendsRef.child(mSenderId).child(mReceiverUserId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mFriendRequestsRef.child(mReceiverUserId).child(mSenderId).removeValue()
                        .addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                case = NEW
                                view?.btn_add_friend?.text = "Add Friend"
                            }
                        }
                }
            }

    }


    private fun manageClickEvents(){
        Log.d(TAG, "manageClickEvents: starts")
        // fun retrieves needed stuff from db incase user leaves and come back to this fragment
        mFriendRequestsRef.child(mSenderId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(mReceiverUserId)) { // request has been sent to end user already
                    var requestType =
                        snapshot.child(mReceiverUserId).child("request_type").value.toString()
                    if (requestType == "sent") {
                        case = SENT
                        view?.btn_add_friend?.text = "Cancel Request"
//                        btn_add_friend.background = getDrawable(requireContext(), R.drawable.btn_decline_style)
//                        btn_add_friend.setTextColor(Color.BLACK)
                    }
                } else {  // request has been accepted by user already or not sent yet
                    mFriendsRef.child(mSenderId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.hasChild(mReceiverUserId)) {  // request has already been accepted
                                    case = ACCEPTED
                                    view?.btn_add_friend?.text = "Delete Contact"
                                } else {   // no friend request sent
                                    case = NEW
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })

                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        if(mSenderId == mReceiverUserId){ // user tapped on own profile
            view?.btn_add_friend?.visibility = View.GONE
        } else{
            view?.btn_add_friend?.setOnClickListener {
                when(case){
                    NEW -> sendFriendRequest()
                    SENT -> cancelFriendRequest()
                    RECIEVED -> acceptFriendRequest()
                    ACCEPTED -> deleteContact()

                }
            }
        }
    }

    private fun acceptFriendRequest() {
        mFriendsRef.child(mSenderId).child(mReceiverUserId).child("Friend").setValue("Saved")
            .addOnCompleteListener{ task->
                if (task.isSuccessful) {
                    mFriendsRef.child(mReceiverUserId).child(mSenderId).child("Friend")
                        .setValue("Saved")
                        .addOnCompleteListener{ task1 ->
                            if (task1.isSuccessful) {
                                mFriendRequestsRef.child(mSenderId).child(mReceiverUserId)
                                    .removeValue()
                                    .addOnCompleteListener{ task2 ->
                                        if (task2.isSuccessful) {
                                            mFriendRequestsRef.child(mReceiverUserId)
                                                .child(mSenderId).removeValue()
                                                .addOnCompleteListener{task3->
                                                    if (task3.isSuccessful) {
                                                        view?.btn_add_friend?.text =  "Delete Contact"
                                                        case = ACCEPTED
                                                    }
                                                }
                                        }
                                    }
                            }
                        }
                }
            }
    }
}