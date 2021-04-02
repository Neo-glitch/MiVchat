package com.neo.mivchat.ui.Activities.AccountActivity

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.neo.mivchat.R
import com.neo.mivchat.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class AccountActivity : AppCompatActivity() {
    private val mUsersRef by lazy {
        FirebaseDatabase.getInstance().reference.child("users")
    }
    private val mCurrentUser by lazy {
        FirebaseAuth.getInstance().currentUser
    }

    private val mCurrentUserId by lazy {
        FirebaseAuth.getInstance().currentUser?.uid!!
    }

    private lateinit var mSelectedImageUri: Uri
    private lateinit var mUser: User
    private var mStoragePermission = false   // true when all permissions needed by us is granted
    private lateinit var mBytes: ByteArray  // array to hold byte value of comressed image
    private lateinit var mContentResolver: ContentResolver

    // const
    private val TAG = "AccountActivity"

    companion object {
        private const val READ_WRITE_PERM = 100
    }

    private val FILE_REQUEST_CODE = 1122
    private val MB_THRESHHOLD = 5.0
    private val MB = 1000000.0
    private val FIREBASE_IMAGE_STORAGE = "images/users"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val toolbar = toolbar_account
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)



        mContentResolver = contentResolver
        requestPermissions()

        getUserDetails()
        btn_update?.setOnClickListener {
            if (user_name_et.text.toString() != "") {  // username field not null
                mUsersRef.child(mCurrentUserId).child("name")
                    .setValue(user_name_et.text.toString())
            }
            if (bio_et.text.toString() != "") {
                mUsersRef.child(mCurrentUserId).child("bio").setValue(bio_et.text.toString())
            }
            uploadNewPhoto()
            Toast.makeText(this, "Update complete", Toast.LENGTH_SHORT).show()
        }

        btn_change_photo?.setOnClickListener {
            if (mStoragePermission) {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, FILE_REQUEST_CODE)
            } else {
                requestPermissions()
            }
        }
    }

    private fun uploadNewPhoto() {
//        BackgroundImageResize().execute(mSelectedImageUri)
        uploadImage()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    @AfterPermissionGranted(READ_WRITE_PERM)
    private fun requestPermissions() {  // request needed permission for work
        val perms = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (EasyPermissions.hasPermissions(this, *perms)) {
            mStoragePermission = true
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Apps needed permission to read and write from external storage",
                READ_WRITE_PERM,
                *perms
            )
        }
    }

    private fun getUserDetails() {
        var query = mUsersRef.orderByKey().equalTo(mCurrentUserId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { snapShot ->
                    //this loop will return a single result(since only a single child of this snapshot)
                    mUser = snapShot.getValue(User::class.java)!!
                    user_name_small?.text = mUser.name
                    user_name_et?.setText(mUser.name)
                    bio_et?.setText(mUser.bio)
                    email_et?.setText(mCurrentUser?.email)
                    if (mUser.profile_image != "") {
                        mSelectedImageUri = Uri.parse(mUser.profile_image)
                        Picasso.get().load(mUser.profile_image)
                            .placeholder(R.drawable.profile_image).into(profile_image)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mSelectedImageUri = data?.data!!
            Log.d(TAG, "onActivityResult: $mSelectedImageUri")
            Picasso.get().load(mSelectedImageUri).placeholder(R.drawable.profile_image)
                .into(profile_image)
        }
    }

    //    ////// AsyncTask to shrink image to below 5mb and upload to firebase storage
//    inner class BackgroundImageResize: AsyncTask<Uri, Void, ByteArray?>() {
//        private lateinit var mBitmap: Bitmap
//
//        override fun doInBackground(vararg params: Uri?): ByteArray? {
//            try {
//                // converts the uri to bitmap
//                mBitmap = MediaStore.Images.Media.getBitmap(mContentResolver, params[0])
//
//            }catch (e: IOException){
//            }
//
//            // image compression logic
//            var bytes: ByteArray? = null
//            for (i in 11 until 11){  // ran 10 times since max quality of bitmap is 100% and each loop drops quality by 10 =%
//                if(i == 10){
//                    break
//                }
//                bytes = getBytesFromBitmap(mBitmap, 100/ i)
//                Log.d(TAG, "doInBackground: in bytes ${bytes!!.size}")
//                Log.d(TAG, "doInBackground: megabytes: (  ${11 - i}  0%) ${bytes!!.size /MB} MB")
//                if(bytes?.size!! / MB < MB_THRESHHOLD){
//                    return bytes
//                }
//            }
//            return bytes!!
//        }
//
//        override fun onPostExecute(result: ByteArray?) {
//            super.onPostExecute(result)
//
//            mBytes = result!!
//            // exec the UploadTask
//            uploadImage()
//        }
//    }
//
//    /**
//     * convert from bitmap to byte array and also compress it
//     * @param quality: % quality to be left after compression e.g if 50% passed
//     * then quality of image to remain after compresion should be 50%
//     */
//    fun getBytesFromBitmap(bitmap: Bitmap, quality: Int): ByteArray? {
//        val stream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream) // does the compression
//        return stream.toByteArray()
//    }

    private fun uploadImage() {

        // dir on cloud where photo will be stored, if image already there overrides it
        val storageReference =
            FirebaseStorage.getInstance().reference.child("$FIREBASE_IMAGE_STORAGE/$mCurrentUserId/profile_image")

//        if(mBytes.size/ MB < MB_THRESHHOLD){  // upload only when image Compressed < 5MB
        // create meta data for added config
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpg")
            .setContentLanguage("en").build()
        val uploadTask = storageReference.putFile(mSelectedImageUri, metadata)
        uploadTask.addOnSuccessListener {
            Toast.makeText(this, "Update successful", Toast.LENGTH_SHORT).show()
            //after uploading image, insert image download url to database
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                val imageDownloadUri = uri

                mUsersRef.child(mCurrentUserId).child("profile_image")
                    .setValue(imageDownloadUri.toString())
            }

        }

//        } else{
        // image to large
//    }
    }

}