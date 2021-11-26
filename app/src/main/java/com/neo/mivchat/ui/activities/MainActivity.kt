package com.neo.mivchat.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.neo.mivchat.R
import com.neo.mivchat.databinding.ActivityMainBinding
import com.neo.mivchat.ui.fragments.FindFriendsFragment
import com.neo.mivchat.ui.fragments.FriendsFragment
import com.neo.mivchat.ui.fragments.NotificationsFragment
import com.neo.mivchat.interfaces.IMainActivity
import com.neo.mivchat.adapters.PagerAdapter
import com.neo.mivchat.ui.activities.auth.LoginActivity
import com.neo.mivchat.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity(),
    IMainActivity {

    // firebase
    private lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener
    private val mViewModel: MainActivityViewModel by lazy{
        ViewModelProvider(this)[MainActivityViewModel::class.java]
    }

    // const
    val VISIT_USER_ID = "visit_user_id"
    val VISIT_PROFILE_IMAGE = "profile_image"
    val VISIT_PROFILE_NAME = "profile_name"
    val VISIT_USER_BIO = "user_bio"
    val CALLING_ACTIVITY = 9
    private val TAG = "MainActivity"

    // fragments
    private lateinit var mCalledBy: String   // to store info of user calling current use
    private lateinit var binding: ActivityMainBinding

    private val mFirebaseDatabaseRef by lazy {
        FirebaseDatabase.getInstance().reference
    }

    private lateinit var mViewPager: ViewPager2
    private var isInACall by Delegates.notNull<Boolean>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mViewModel.isAuthenticated.observe(this, Observer { isAuthenticated ->
            if(!isAuthenticated){
                goToLoginActivity()
            }
        })

        mViewModel.isInACall.observe(this, Observer {
            isInACall = it
        })

        initToolbar()
        setUpViewPager()
    }

    private fun initToolbar() {
        val toolbar = toolbar
        setSupportActionBar(toolbar)
    }

    private fun setUpViewPager(){
        mViewPager = binding.viewPager
        mViewPager.adapter = PagerAdapter(this)

        val tabLayout = binding.tabLayout
        val tabLayoutMediator = TabLayoutMediator(tabLayout, mViewPager, object: TabLayoutMediator.TabConfigurationStrategy{
            override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                when(position){
                    0 -> {
                        tab.text = "Home"
                    }
                    1 -> {
                        tab.text = "Find Friends"
                    }
                    2 -> {
                        tab.text = "Notifications"
                    }
                }
            }
        })
        tabLayoutMediator.attach()  // links ViewPager2 and TabLayout together
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_account -> {
                inflateAccountActivity()
                true
            }
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun inflateAccountActivity() {
        // show Activity showing user account details
        startActivity(Intent(this, AccountActivity::class.java))
    }

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


    override fun onStart() {
        super.onStart()
        mViewModel.onStart()
    }

    override fun onStop() {
        super.onStop()
        mViewModel.onStop()
//        FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener)
    }


    // check if user has an incoming call
    private fun ifReceivingCall() {
//        mUsersRef.child(mCurrentUserId).child("Ringing").addValueEventListener(object :
//            ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.hasChild("ringing")) {  // user has an incoming call
//                    mCalledBy = snapshot.child("ringing").value.toString()
//                    startCallActivity(mCalledBy)
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//            }
//        })

//        // my way of handling incoming call
//        mFirebaseDatabaseRef.child("call_node").child(mCurrentUserId)
//            .addValueEventListener(object: ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if(snapshot.hasChild("called_by")){ // user has incoming call
//                        mCalledBy = snapshot.child("called_by").value.toString()
//                        startCallActivity(mCalledBy)
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//            })
    }


    override fun startCallActivity(listUserId: String) {
//        // check if user making call is connected first
//        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")
//        connectedRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val connected = snapshot.getValue(Boolean::class.java) ?: false
//                if (connected) {
//                    Log.d(TAG, "connected")
//                    val intent = Intent(this@MainActivity, CallActivity::class.java)
//                    intent.putExtra(VISIT_USER_ID, listUserId)
//                    startActivityForResult(intent, CALLING_ACTIVITY)
//                } else {
//                    Log.d(TAG, "not connected")
//
//                    // dialog telling user that to make call he needs internet
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.w(TAG, "Listener was cancelled")
//            }
//        })


    }

    override fun inflateProfileActivity(
        receiverUserId: String,
        receiverUserImage: String,
        receiverUserName: String,
        receiverUserBio: String
    ) {
        val profileIntent = Intent(this, ProfileActivity::class.java)
        profileIntent.putExtra(VISIT_USER_ID, receiverUserId)
        profileIntent.putExtra(VISIT_PROFILE_IMAGE, receiverUserImage)
        profileIntent.putExtra(VISIT_PROFILE_NAME, receiverUserName)
        profileIntent.putExtra(VISIT_USER_BIO, receiverUserBio)

        startActivity(profileIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CALLING_ACTIVITY && resultCode == Activity.RESULT_OK) {
//            startActivity(Intent(this, VideoChatActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    override fun onBackPressed() {
        if(mViewPager.currentItem > 0){
            mViewPager.currentItem = mViewPager.currentItem - 1
        } else{
            super.onBackPressed()
        }
    }
}