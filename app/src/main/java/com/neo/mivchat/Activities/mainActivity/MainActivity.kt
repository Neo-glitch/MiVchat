package com.neo.mivchat.Activities.mainActivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.neo.mivchat.*
import com.neo.mivchat.Activities.CallActivity.CallActivity
import com.neo.mivchat.Activities.LoginActivity.LoginActivity
import com.neo.mivchat.Activities.VideoChatActivity.VideoChatActivity
import com.neo.mivchat.R
import com.neo.mivchat.fragments.accountFragment.AccountFragment
import com.neo.mivchat.fragments.findFriendsFragment.FindFriendsFragment
import com.neo.mivchat.fragments.homeFragment.HomeFragment
import com.neo.mivchat.fragments.notificationsFrament.NotificationsFragment
import com.neo.mivchat.fragments.profileFragment.ProfileFragment
import com.neo.mivchat.model.FragmentTag
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_account.*


class MainActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener,
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
    private lateinit var mHomeFragment: HomeFragment
    private lateinit var mFindFriendsFragment: FindFriendsFragment
    private lateinit var mNotificationsFragment: NotificationsFragment
    private lateinit var mProfileFragment: ProfileFragment
    private lateinit var mAccountFragment: AccountFragment
    private lateinit var mCalledBy: String   // to store info of user calling current user



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mViewModel = ViewModelProvider(viewModelStore, ViewModelProvider.NewInstanceFactory())[MainActivityViewModel::class.java]

        mUsersRef = mViewModel.mUsersRef
        mFriendsRef = mViewModel.mFriendsRef
        mCurrentUserId = mViewModel.mCurrentUserId

        initToolbar()
        setupFirebaseAuth()
        ifReceivingCall()
        setupBottomNav()
        init(mViewModel.bottomNavDisplaySelection)
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
        return when (item.itemId) {
            R.id.action_account -> {
                inflateAccountFragment()
                true
            }
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun inflateAccountFragment() {
        if (::mAccountFragment.isInitialized) {
            supportFragmentManager.beginTransaction().remove(mAccountFragment)
                .commitAllowingStateLoss()
        }
        mAccountFragment =
            AccountFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(
            R.id.main_activity_frame, mAccountFragment, getString(
                R.string.account_fragment
            )
        )
        transaction.commit()
        mViewModel.mFragmentTags.add(getString(R.string.account_fragment))
        mViewModel.mFragments.add(
            FragmentTag(
                mAccountFragment,
                getString(R.string.account_fragment)
            )
        )
        setFragmentVisibility(getString(R.string.account_fragment))

    }

    private fun setupBottomNav() {
        bottom_nav_view.onNavigationItemSelectedListener = this
    }

    private fun init(itemId: Int) {
        if (itemId == R.id.homeFragment) {
            if (!::mHomeFragment.isInitialized) {
                mHomeFragment = HomeFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(
                    R.id.main_activity_frame,
                    mHomeFragment,
                    getString(R.string.home_fragment)
                )
                transaction.commit()
                mViewModel.mFragmentTags.add(getString(R.string.home_fragment))
                mViewModel.mFragments.add(
                    FragmentTag(
                        mHomeFragment,
                        getString(R.string.home_fragment)
                    )
                )
            } else {
                mViewModel.mFragmentTags.remove(getString(R.string.home_fragment))
                mViewModel.mFragments.remove(
                    FragmentTag(
                        mHomeFragment,
                        getString(R.string.home_fragment)
                    )
                )
            }
            setFragmentVisibility(getString(R.string.home_fragment))
        } else if (itemId == R.id.findFriendsFragment) {
            if (!::mFindFriendsFragment.isInitialized) {
                mFindFriendsFragment = FindFriendsFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(
                    R.id.main_activity_frame,
                    mFindFriendsFragment,
                    getString(R.string.find_friends_fragment)
                )
                transaction.commit()
                mViewModel.mFragmentTags.add(getString(R.string.find_friends_fragment))
                mViewModel.mFragments.add(
                    FragmentTag(
                        mFindFriendsFragment,
                        getString(R.string.find_friends_fragment)
                    )
                )
            } else {
                mViewModel.mFragmentTags.remove(getString(R.string.find_friends_fragment))
                mViewModel.mFragments.remove(
                    FragmentTag(
                        mFindFriendsFragment,
                        getString(R.string.find_friends_fragment)
                    )
                )
            }
            setFragmentVisibility(getString(R.string.find_friends_fragment))
        } else if (itemId == R.id.notificationsFragment) {
            if (!::mNotificationsFragment.isInitialized) {
                mNotificationsFragment = NotificationsFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(
                    R.id.main_activity_frame,
                    mNotificationsFragment,
                    getString(R.string.notifications_fragment)
                )
                transaction.commit()
                mViewModel.mFragmentTags.add(getString(R.string.notifications_fragment))
                mViewModel.mFragments.add(
                    FragmentTag(
                        mNotificationsFragment,
                        getString(R.string.notifications_fragment)
                    )
                )
            } else {
                mViewModel.mFragmentTags.remove(getString(R.string.notifications_fragment))
                mViewModel.mFragments.remove(
                    FragmentTag(
                        mNotificationsFragment,
                        getString(R.string.notifications_fragment)
                    )
                )
            }
            setFragmentVisibility(getString(R.string.notifications_fragment))
        }
    }

    private fun setFragmentVisibility(tagName: String) {
        when (tagName) {
            getString(R.string.home_fragment), getString(
                R.string.find_friends_fragment
            ),
            getString(R.string.notifications_fragment) -> {
                toolbar.visibility = View.VISIBLE
                setBottomNavVisibility(true)
            }
//            getString(R.string.call_fragment)-> {
//                toolbar.visibility = View.VISIBLE
//                setBottomNavVisibility(false)}
            getString(R.string.profile_fragment), getString(
                R.string.account_fragment
            ) -> {
                toolbar.visibility = View.GONE
                setSupportActionBar(toolbar_account)
                setBottomNavVisibility(false)
            }
        }
        for (i in mViewModel.mFragments.indices) {
            if (tagName == mViewModel.mFragments[i].tag) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.show(mViewModel.mFragments[i].fragment!!)
                transaction.commit()
            } else {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.hide(mViewModel.mFragments[i].fragment!!)
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
        val backStackCount = mViewModel.mFragmentTags.size
        Log.d(TAG, "onBackPressed: $backStackCount")
        if (backStackCount > 1) { // true if fragment is after fragment in focus
            var topFragmentTag = mViewModel.mFragmentTags[backStackCount - 1]

            // logic makes fragment after topFragment to now be in view, and removes old fragment from stack
            val newTopFragmentTag = mViewModel.mFragmentTags.get(backStackCount - 2)
            setFragmentVisibility(newTopFragmentTag)
            mViewModel.mFragmentTags.remove(topFragmentTag)
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
                    mViewModel.mFragmentTags.add(getString(R.string.home_fragment))
                    mViewModel.mFragments.add(
                        FragmentTag(
                            mHomeFragment,
                            getString(R.string.home_fragment)
                        )
                    )
                } else {
                    mViewModel.mFragmentTags.remove(getString(R.string.home_fragment))
                    mViewModel.mFragmentTags.add(getString(R.string.home_fragment))
                }
                setFragmentVisibility(getString(R.string.home_fragment))
                item.isChecked = true
                mViewModel.bottomNavDisplaySelection = item.itemId
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
                    mViewModel.mFragmentTags.add(getString(R.string.find_friends_fragment))
                    mViewModel.mFragments.add(
                        FragmentTag(
                            mFindFriendsFragment,
                            getString(R.string.find_friends_fragment)
                        )
                    )
                } else {
                    mViewModel.mFragmentTags.remove(getString(R.string.find_friends_fragment))
                    mViewModel.mFragmentTags.add(getString(R.string.find_friends_fragment))
                }
                setFragmentVisibility(getString(R.string.find_friends_fragment))
                item.isChecked = true
                mViewModel.bottomNavDisplaySelection = item.itemId
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
                    mViewModel.mFragmentTags.add(getString(R.string.notifications_fragment))
                    mViewModel.mFragments.add(
                        FragmentTag(
                            mNotificationsFragment,
                            getString(R.string.notifications_fragment)
                        )
                    )
                } else {
                    mViewModel.mFragmentTags.remove(getString(R.string.notifications_fragment))
                    mViewModel.mFragmentTags.add(getString(R.string.notifications_fragment))
                }
                setFragmentVisibility(getString(R.string.notifications_fragment))
                item.isChecked = true
                mViewModel.bottomNavDisplaySelection = item.itemId
                return true
            }
        }
        return false
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CALLING_ACTIVITY && resultCode == Activity.RESULT_OK) {
            startActivity(Intent(this, VideoChatActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    override fun inflateProfileFragment(
        receiverUserId: String,
        receiverUserImage: String,
        receiverUserName: String,
        receiverUserBio: String
    ) {
        if (::mProfileFragment.isInitialized) {
            supportFragmentManager.beginTransaction().remove(mProfileFragment)
                .commitAllowingStateLoss()
        }
        mProfileFragment =
            ProfileFragment()
        var args = Bundle()
        args.putString(VISIT_USER_ID, receiverUserId)
        args.putString(VISIT_PROFILE_IMAGE, receiverUserImage)
        args.putString(VISIT_PROFILE_NAME, receiverUserName)
        args.putString(VISIT_USER_BIO, receiverUserBio)
        mProfileFragment.arguments = args

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(
            R.id.main_activity_frame,
            mProfileFragment,
            getString(R.string.profile_fragment)
        )
        transaction.commit()

        mViewModel.mFragmentTags.add(getString(R.string.profile_fragment))
        mViewModel.mFragments.add(
            FragmentTag(
                mProfileFragment,
                getString(R.string.profile_fragment)
            )
        )
        setFragmentVisibility(getString(R.string.profile_fragment))
    }
}