package com.neo.mivchat.ui.views.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.neo.mivchat.R
import com.neo.mivchat.viewmodel.PasswordResetViewModel
import kotlinx.android.synthetic.main.dialog_password_reset.*
import kotlinx.android.synthetic.main.dialog_password_reset.view.*


class PasswordResetDialog : DialogFragment() {

    private val TAG = "PasswordResetDialog"
    lateinit var  mContext: Context
    private val mViewModel: PasswordResetViewModel by lazy {
        ViewModelProvider(this)[PasswordResetViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.dialog_password_reset, container, false)
        Log.d(TAG, "onCreateView: passwordreset starts")

        mViewModel.dismissDialog.observe(this, Observer {
            if(it){
                dialog?.dismiss()
            }
        })

        view.tv_confirm_reset.setOnClickListener {
            if (et_reset_email.text.toString().isNotEmpty()) {
//                sendPassWordResetToEmail(et_reset_email.text.toString())
//                dialog?.dismiss()
                mViewModel.sendPassWordResetEmail(et_reset_email.text.toString())
            }
        }
        return view
    }

//    private fun sendPassWordResetToEmail(email: String) {
//        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
//            .addOnCompleteListener { task ->
//                val text: String = if (task.isSuccessful) {
//                    "PassWord reset link sent to email"
//                } else {
//                    "No user is associated with that email"
//                }
//                Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show()
//            }
//    }


}