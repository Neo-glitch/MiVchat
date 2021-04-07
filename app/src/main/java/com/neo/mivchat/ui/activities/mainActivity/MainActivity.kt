package com.neo.mivchat.ui.activities.mainActivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.neo.mivchat.ui.activities.CallActivity.CallActivity
import com.neo.mivchat.ui.activities.LoginActivity.LoginActivity
import com.neo.mivchat.ui.activities.VideoChatActivity.VideoChatActivity
import com.neo.mivchat.utilities.IMainActivity
import com.neo.mivchat.R
import com.neo.mivchat.databinding.ActivityMainBinding
import com.neo.mivchat.ui.fragments.findFriendsFragment.FindFriendsFragment
import com.neo.mivchat.ui.fragments.friendsFragment.FriendsFragment
import com.neo.mivchat.ui.fragments.notificationsFrament.NotificationsFragment
import com.neo.mivchat.ui.activities.AccountActivity.AccountActivity
import com.neo.mivchat.ui.activities.ProfileActivity.ProfileActivity
import com.neo.mivchat.utilities.PagerAdapter
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity(),
    IMainActivity {

    // firebase
    private lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener
    private lateinit var mViewModel: MainActivityViewModel
    private lateinit var mFriendsRef: DatabaseReference
    private lateinit var mUsersRef: DatabaseReference
    private lateinit var mCurrentUserId: String

    // const
    val VISIT_USER_ID = "visit_user_id"
    val VISIT_PROFILE_IMAGE = "profile_image"
    val VISIT_PROFILE_NAME = "profile_name"
    val VISIT_USER_BIO = "user_bio"
    val HOME_FRAGMENT = 0
    val FINDFRIENDS_FRAGMENT = 1
    val NOTIFICATIONS_FRAGMENT = 2
    val CALLING_ACTIVITY = 9
    private val TAG = "MainActivity"

    // fragments
    private lateinit var mHomeFragment: FriendsFragment
    private lateinit var mFindFriendsFragment: FindFriendsFragment
    private lateinit var mNotificationsFragment: NotificationsFragment
    private lateinit var mCalledBy: String   // to store info of user calling current user

    //Binding
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mViewModel = ViewModelProvider(viewModelStore, ViewModelProvider.NewInstanceFactory())[MainActivityViewModel::class.java]

        mUsersRef = mViewModel.mUsersRef
        mFriendsRef = mViewModel.mFriendsRef
        mCurrentUserId = mViewModel.mCurrentUserId

        initToolbar()
        setupFirebaseAuth()
        ifReceivingCall()
        setUpViewPager()
//        setupBottomNav()
//        init(mViewModel.bottomNavDisplaySelection)
    }

    private fun initToolbar() {
        val toolbar = toolbar
        setSupportActionBar(toolbar)
    }

    private fun setUpViewPager(){
        val viewpager = binding.viewPager
        viewpager.adapter = PagerAdapter(this)

        val tabLayout = binding.tabLayout
        val tabLayoutMediator = TabLayoutMediator(tabLayout, view_pager, object: TabLayoutMediator.TabConfigurationStrategy{
            override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                when(position){
                    0 -> {
                        tab.text = "Home"
//                        tab.setIcon(R.drawable.ic_home)
                    }
                    1 -> {
                        tab.text = "Find Friends"
//                        tab.setIcon(R.drawable.ic_find_friends)
                    }
                    2 -> {
                        tab.text = "Notifications"
//                        tab.setIcon(R.drawable.ic_notificaions)
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

    override fun onResume() {
        super.onResume()
        checkAuthState()
    }

    /*
        extra check to ensure user in this, is always authenticated
     */
    private fun checkAuthState() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }


    //////// Firebase Auth setup //////////
    private fun setupFirebaseAuth() {
        mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                // user not authenticated
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

    }
    ///////////////////////////////////////

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener)
    }


    // check if user has an incoming call
    private fun ifReceivingCall() {
        mUsersRef.child(mCurrentUserId).child("Ringing").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("ringing")) {  // user has an incoming call
                    mCalledBy = snapshot.child("ringing").value.toString()
                    startCallActivity(mCalledBy)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    override fun startCallActivity(listUserId: String) {
        val intent = Intent(this, CallActivity::class.java)
        intent.putExtra(VISIT_USER_ID, listUserId)
        startActivityForResult(intent, CALLING_ACTIVITY)
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

    override fun acceptRequest(userId: String) {
        mViewModel.acceptRequest(userId)
    }

    override fun declineRequest(userId: String) {
        mViewModel.declineRequest(userId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CALLING_ACTIVITY && resultCode == Activity.RESULT_OK) {
            startActivity(Intent(this, VideoChatActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }
}