package com.neo.mivchat

import android.Manifest
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.opentok.android.*
import kotlinx.android.synthetic.main.activity_video_chat.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class VideoChatActivity : AppCompatActivity(), Session.SessionListener,
    PublisherKit.PublisherListener {
    /////////////  openTokSection
    private val API_KEY = "46964834"
    private val SESSION_ID =
        "1_MX40Njk2NDgzNH5-MTYwMzQ4ODYzMTc2M35jamE2RGZTRXAzRUU0R1dZTHNJUE8ySUR-fg"

    // lasts for 30 days
    private val TOKEN =
        "T1==cGFydG5lcl9pZD00Njk2NDgzNCZzaWc9OGNmMWMwZWMzOTdlMDVhZmYwN2ZiMzYwNWQ3NWVjNDc2Y2Q0NTRmZTpzZXNzaW9uX2lkPTFfTVg0ME5qazJORGd6Tkg1LU1UWXdNelE0T0RZek1UYzJNMzVqYW1FMlJHWlRSWEF6UlVVMFIxZFpUSE5KVUU4eVNVUi1mZyZjcmVhdGVfdGltZT0xNjAzNDg4NzAwJm5vbmNlPTAuNzA3Nzc1MzgzMTU3MDI5NyZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNjA2MDg0Mjk5JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9"
    /////////// openTokSection

    private val TAG = "VideoChatActivity"

    companion object {
        private const val RC_VIDEO_PERM = 123
    }

    //firebase
    val mUsersRef by lazy {
        FirebaseDatabase.getInstance().reference.child("users")
    }
    val mUserId by lazy {
        FirebaseAuth.getInstance().currentUser?.uid!!
    }

    //widgets
    private val mPublisherViewController by lazy {
        publisher_container
    }
    private val mSubscriberViewController by lazy {
        subscriber_container
    }

    //VideoStream
    private lateinit var mSession: Session          // videoStream Session
    private var mPublisher: Publisher? = null       //device owner
    private var mSubscriber: Subscriber? = null     //other user in call


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_chat)

        cancel_video_chat.setOnClickListener {
            endVideoChat()
        }
    }

    private fun endVideoChat(){
        mUsersRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(mUserId).hasChild("Ringing")){  // case subscriber
                    mUsersRef.child(mUserId).child("Ringing").removeValue()

                    // cleanUp for VideoStream
                    if(mPublisher != null){
                        mPublisher?.destroy()
                    }
                    if(mSubscriber != null){
                        mSubscriber?.destroy()
                    }
                    finish()
                }
                if(snapshot.child(mUserId).hasChild("Calling")){ // publisher
                    mUsersRef.child(mUserId).child("Calling").removeValue()

                    // cleanUp for VideoStream

                    // cleanUp for VideoStream
                    if (mPublisher != null) {
                        mPublisher!!.destroy()
                    }
                    if (mSubscriber != null) {
                        mSubscriber!!.destroy()
                    }
                    finish()
                } else{
                    // cleanUp for VideoStream

                    // cleanUp for VideoStream
                    if (mPublisher != null) {
                        mPublisher!!.destroy()
                    }
                    if (mSubscriber != null) {
                        mSubscriber!!.destroy()
                    }
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        requestPermissions()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    @AfterPermissionGranted(RC_VIDEO_PERM)
    private fun requestPermissions() {  // request the needed permissions
        // fun request the needed permissions
        val perms = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )

        if (EasyPermissions.hasPermissions(this, *perms)) {
            // 1. init and conn to VideoStream
            mSession = Session.Builder(this, API_KEY, SESSION_ID).build()
            mSession.setSessionListener(this)
            mSession.connect(TOKEN)
        } else {
            EasyPermissions.requestPermissions(
                this,
                "App needs permission to use Mic and Camera to make Video Calls, Please Grant",
                RC_VIDEO_PERM,
                *perms
            )
        }

    }


    override fun onDestroy() {
        endVideoChat()
        super.onDestroy()
    }


    ////////// Session Events Listener Callbacks

    // 4. cleanup
    override fun onStreamDropped(p0: Session?, p1: Stream?) {
        if(mSubscriber != null){
            mSubscriber == null
            mSubscriberViewController.removeAllViews()
        }
    }

    //3. sub other user to stream
    override fun onStreamReceived(session: Session?, stream: Stream?) {
        if(mSubscriber == null){   // other user not in another call
            mSubscriber = Subscriber.Builder(this, stream).build()
            mSession.subscribe(mSubscriber)
            mSubscriberViewController.addView(mSubscriber?.view)
        }

    }

    // 2.pub stream to session
    override fun onConnected(session: Session?) {
        mPublisher = Publisher.Builder(this).build()
        mPublisher?.setPublisherListener(this)
        mPublisherViewController.addView(mPublisher?.view)
        if(mPublisher?.view is GLSurfaceView){  // GlSurface view enables us see own and other users stream
            (mPublisher?.view as GLSurfaceView).setZOrderOnTop(true)  // put this view on top subscriber steam
        }
        mSession.publish(mPublisher)
    }

    override fun onDisconnected(p0: Session?) {
        TODO("Not yet implemented")
    }

    override fun onError(p0: Session?, p1: OpentokError?) {
        TODO("Not yet implemented")
    }
    //////  Session Events Listener Callbacks


    //////// Publisher Events Listener Callbacks
    override fun onError(p0: PublisherKit?, p1: OpentokError?) {
        TODO("Not yet implemented")
    }

    override fun onStreamCreated(p0: PublisherKit?, p1: Stream?) {
        TODO("Not yet implemented")
    }

    override fun onStreamDestroyed(p0: PublisherKit?, p1: Stream?) {
        TODO("Not yet implemented")
    }
    /////// Publisher Events Listener Callbacks
}