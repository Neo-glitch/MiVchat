package com.neo.mivchat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.neo.mivchat.fragments.findFriendsFragment.FindFriendsFragment
import com.neo.mivchat.fragments.homeFragment.HomeFragment
import com.neo.mivchat.fragments.notificationsFrament.NotificationsFragment
import com.neo.mivchat.fragments.profileFragment.ProfileFragment
import com.neo.mivchat.model.FragmentTag
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener,
    IMainActivity {

    // firebase
    private lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener
    //firebase
    private lateinit var mFriendsRef: DatabaseReference
    private lateinit var mUsersRef: DatabaseReference
    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }

    // const
    val VISIT_USER_ID = "visit_user_id"
    val VISIT_PROFILE_IMAGE = "profile_image"
    val VISIT_PROFILE_NAME = "profile_name"
    val HOME_FRAGMENT = 0
    val FINDFRIENDS_FRAGMENT = 1
    val NOTIFICATIONS_FRAGMENT = 2
    val CALLING_ACTIVITY = 9

    private val TAG = "MainActivity"

    // fragments
    private lateinit var mHomeFragment: HomeFragment
    private lateinit var mFindFriendsFragment: FindFriendsFragment
    private lateinit var mNotificationsFragment: NotificationsFragment
//    private lateinit var mCallFragment: CallFragment
    private lateinit var mProfileFragment: ProfileFragment

    //var
    private var mFragmentTags: MutableList<String> = mutableListOf()
    private var mFragments: MutableList<FragmentTag> = mutableListOf()
    private lateinit var mCurrentUserId:String
    private lateinit var mCalledBy:String   // to store info of user calling current user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mUsersRef = FirebaseDatabase.getInstance().reference.child("users")
        mFriendsRef = FirebaseDatabase.getInstance().reference.child("Friends")
        mCurrentUserId = FirebaseAuth.getInstance().currentUser?.uid!!

        initToolbar()
        setupFirebaseAuth()
        ifReceivingCall()
        setupBottomNav()
        init()
    }

    private fun initToolbar() {
        val toolbar = toolbar
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_settings -> true
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupBottomNav() {
        bottom_nav_view.onNavigationItemSelectedListener = this
    }

    private fun init() {
        if (!::mHomeFragment.isInitialized) {
            mHomeFragment = HomeFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(
                R.id.main_activity_frame,
                mHomeFragment,
                getString(R.string.home_fragment)
            )
            transaction.commit()
            mFragmentTags.add(getString(R.string.home_fragment))
            mFragments.add(FragmentTag(mHomeFragment, getString(R.string.home_fragment)))
        } else {
            mFragmentTags.remove(getString(R.string.home_fragment))
            mFragments.remove(FragmentTag(mHomeFragment, getString(R.string.home_fragment)))
        }
        setFragmentVisibility(getString(R.string.home_fragment))

    }

    private fun setFragmentVisibility(tagName: String) {
        when(tagName){
            getString(R.string.home_fragment), getString(R.string.find_friends_fragment),
            getString(R.string.notifications_fragment) -> {
                toolbar.visibility = View.VISIBLE
                setBottomNavVisibility(true)}
//            getString(R.string.call_fragment)-> {
//                toolbar.visibility = View.VISIBLE
//                setBottomNavVisibility(false)}
            getString(R.string.profile_fragment) -> {
                toolbar.visibility = View.GONE
                setBottomNavVisibility(false)
            }
        }
        for (i in mFragments.indices) {
            if (tagName == mFragments[i].tag) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.show(mFragments[i].fragment!!)
                transaction.commit()
            } else {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.hide(mFragments[i].fragment!!)
                transaction.commit()
            }
        }
        setNavigationIcon(tagName)
    }

    private fun setBottomNavVisibility(visibility: Boolean) {
        bottom_nav_view.visibility = if (visibility) View.VISIBLE else View.GONE
    }

    private fun setNavigationIcon(tagName: String) {
        var menu = bottom_nav_view.menu
        var menuItem: MenuItem?
        if (tagName == getString(R.string.home_fragment)) {
            menuItem = menu.getItem(HOME_FRAGMENT)
            menuItem.isChecked = true
        } else if (tagName == getString(R.string.find_friends_fragment)) {
            menuItem = menu.getItem(FINDFRIENDS_FRAGMENT)
            menuItem.isChecked = true
        } else if (tagName == getString(R.string.notifications_fragment)) {
            menuItem = menu.getItem(NOTIFICATIONS_FRAGMENT)
            menuItem.isChecked = true
        }
    }

    override fun onBackPressed() {
        // remove fragment at top and show the one directly below it.. but check if there's another fragment below top
        val backStackCount = mFragmentTags.size
        Log.d(TAG, "onBackPressed: $backStackCount")
        if (backStackCount > 1) { // true if fragment is after fragment in focus
            var topFragmentTag = mFragmentTags[backStackCount - 1]

            // logic makes fragment after topFragment to now be in view, and removes old fragment from stack
            val newTopFragmentTag = mFragmentTags.get(backStackCount - 2)
            setFragmentVisibility(newTopFragmentTag)
            mFragmentTags.remove(topFragmentTag)
        } else if (backStackCount == 1) {
            super.onBackPressed()
        }
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


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.homeFragment -> {
                if (!::mHomeFragment.isInitialized) {
                    mHomeFragment =
                        HomeFragment()
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.add(
                        R.id.main_activity_frame,
                        mHomeFragment,
                        getString(R.string.home_fragment)
                    )
                    transaction.commit()
                    mFragmentTags.add(getString(R.string.home_fragment))
                    mFragments.add(FragmentTag(mHomeFragment, getString(R.string.home_fragment)))
                } else {
                    mFragmentTags.remove(getString(R.string.home_fragment))
                    mFragmentTags.add(getString(R.string.home_fragment))
                }
                setFragmentVisibility(getString(R.string.home_fragment))
                item.isChecked = true
                return true
            }
            R.id.findFriendsFragment -> {
                if (!::mFindFriendsFragment.isInitialized) {
                    mFindFriendsFragment =
                        FindFriendsFragment()
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.add(
                        R.id.main_activity_frame,
                        mFindFriendsFragment,
                        getString(R.string.find_friends_fragment)
                    )
                    transaction.commit()
                    mFragmentTags.add(getString(R.string.find_friends_fragment))
                    mFragments.add(
                        FragmentTag(
                            mFindFriendsFragment,
                            getString(R.string.find_friends_fragment)
                        )
                    )
                } else {
                    mFragmentTags.remove(getString(R.string.find_friends_fragment))
                    mFragmentTags.add(getString(R.string.find_friends_fragment))
                }
                setFragmentVisibility(getString(R.string.find_friends_fragment))
                item.isChecked = true
                return true
            }
            R.id.notificationsFragment -> {
                if (!::mNotificationsFragment.isInitialized) {
                    mNotificationsFragment =
                        NotificationsFragment()
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.add(
                        R.id.main_activity_frame,
                        mNotificationsFragment,
                        getString(R.string.notifications_fragment)
                    )
                    transaction.commit()
                    mFragmentTags.add(getString(R.string.notifications_fragment))
                    mFragments.add(
                        FragmentTag(
                            mNotificationsFragment,
                            getString(R.string.notifications_fragment)
                        )
                    )
                } else {
                    mFragmentTags.remove(getString(R.string.notifications_fragment))
                    mFragmentTags.add(getString(R.string.notifications_fragment))
                }
                setFragmentVisibility(getString(R.string.notifications_fragment))
                item.isChecked = true
                return true
            }
        }
        return false
    }

    // check if user has an incoming call
    private fun ifReceivingCall() {
        mUsersRef.child(mCurrentUserId).child("Ringing").addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild("ringing")){  // user has an incoming call
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CALLING_ACTIVITY && resultCode == Activity.RESULT_OK){
            startActivity(Intent(this, VideoChatActivity::class.java))
        }
    }

    override fun inflateProfileFragment(
        receiverUserId: String,
        receiverUserImage: String,
        receiverUserName: String
    ) {
        if(::mProfileFragment.isInitialized){
            supportFragmentManager.beginTransaction().remove(mProfileFragment).commitAllowingStateLoss()
        }
        mProfileFragment =
            ProfileFragment()
        var args = Bundle()
        args.putString(VISIT_USER_ID, receiverUserId)
        args.putString(VISIT_PROFILE_IMAGE, receiverUserImage)
        args.putString(VISIT_PROFILE_NAME, receiverUserName)
        mProfileFragment.arguments = args

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.main_activity_frame, mProfileFragment, getString(R.string.profile_fragment))
        transaction.commit()
        mFragmentTags.add(getString(R.string.profile_fragment))
        mFragments.add(FragmentTag(mProfileFragment, getString(R.string.profile_fragment)))
        setFragmentVisibility(getString(R.string.profile_fragment))
    }
}