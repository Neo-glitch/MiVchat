package com.neo.mivchat.ui.fragments.notificationsFrament

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.neo.mivchat.R
import com.neo.mivchat.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_notifications.view.*


class NotificationsFragment : Fragment() {
    private val TAG = "NotificationsFragment"

    private val mViewModel by lazy {
        ViewModelProviders.of(this, defaultViewModelProviderFactory)[NotificationsViewModel::class.java]
    }
    private val mFirebaseRecyclerAdapter by lazy {
        mViewModel.initAdapter(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)
        view.rv_notifications.layoutManager = LinearLayoutManager(requireContext())
        view.rv_notifications?.adapter = mFirebaseRecyclerAdapter

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        mFirebaseRecyclerAdapter.stopListening()
    }
}