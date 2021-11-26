package com.neo.mivchat.ui.views.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import com.neo.mivchat.R
import com.neo.mivchat.databinding.DialogProfileBinding
import com.neo.mivchat.model.User
import java.lang.ref.WeakReference

class ProfileDialog(context: Context,
                    var user: User
): AlertDialog(context, R.style.CustomAlertDialogStyle) {
    private lateinit var mBinding: DialogProfileBinding
    private var mContext: WeakReference<Context> = WeakReference(context)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var profileImageString = user.profile_image
        var userName = user.name
        var userId = user.user_id
        var userBio = user.bio
        var userFriendsNumber = user.friends.size  // total num of friends this user has

        mBinding = DialogProfileBinding.inflate(LayoutInflater.from(mContext.get()), null, false)
        setContentView(mBinding.root)
        this?.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
    }
}