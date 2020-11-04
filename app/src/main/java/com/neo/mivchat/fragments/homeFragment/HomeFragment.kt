package com.neo.mivchat.fragments.homeFragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.neo.mivchat.IMainActivity
import com.neo.mivchat.R
import com.neo.mivchat.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_home.view.rv_home


class HomeFragment : Fragment() {
    //firebase
    private lateinit var mFriendsRef: DatabaseReference
    private lateinit var mUsersRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    // const

    private lateinit var mCurrentUserId:String
    private lateinit var mUserName: String
    private lateinit var mProfileImageUrl: String
    private lateinit var mCalledBy:String   // to store info of user calling current user

    // listener
    private val listener by lazy {
        requireActivity() as IMainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        mUsersRef = FirebaseDatabase.getInstance().reference.child("users")
        mFriendsRef = FirebaseDatabase.getInstance().reference.child("Friends")
        mCurrentUserId = FirebaseAuth.getInstance().currentUser?.uid!!

        view.rv_home.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

    override fun onStart() {
        super.onStart()

        ifReceivingCall()
        val options = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(mFriendsRef.child(mCurrentUserId), User::class.java).build()

        val firebaseAdapter = initFirebaseAdapter(options)
        rv_home.adapter = firebaseAdapter
        firebaseAdapter.startListening()
    }

    private fun initFirebaseAdapter(options: FirebaseRecyclerOptions<User>): FirebaseRecyclerAdapter<User, HomeRvViewHolder >{
        return object : FirebaseRecyclerAdapter<User, HomeRvViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRvViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home, parent, false)
                return HomeRvViewHolder(view)
            }

            override fun onBindViewHolder(holder: HomeRvViewHolder, position: Int, model: User) {
                val listUserId = getRef(position).key!!   // id of each user in rv list

                mUsersRef.child(listUserId).addValueEventListener(object : ValueEventListener{  // to get info of user in rv list and work
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            mUserName = snapshot.child("name").value.toString()
                            mProfileImageUrl = snapshot.child("profile_image").value.toString()
                            holder.userName.text = mUserName
                            if(mProfileImageUrl == ""){
                                Picasso.get().load(mProfileImageUrl).placeholder(R.drawable.profile_image).into(holder.userImage)
                            }

                            holder.videoCallBtn.setOnClickListener {
                                listener.inflateCallFragment(listUserId)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
        }
    }

    // check if user has an incoming call
    private fun ifReceivingCall() {
        mUsersRef.child(mCurrentUserId).child("Ringing").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild("ringing")){  // user has an incoming call
                    mCalledBy = snapshot.child("ringing").value.toString()
                    listener.inflateCallFragment(mCalledBy)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


}