package com.neo.mivchat.fragments.homeFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
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
import kotlinx.android.synthetic.main.fragment_home.view.rv_home


class HomeFragment : Fragment() {

    private val mViewModel by lazy {
        ViewModelProviders.of(this, defaultViewModelProviderFactory)[HomeFragmentViewModel::class.java]
//        ViewModelProvider(viewModelStore, ViewModelProvider.NewInstanceFactory())[HomeFragmentViewModel::class.java]
    }
    private val mFirebaseAdapter by lazy {
        mViewModel.initAdapter(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        view.rv_home.layoutManager = LinearLayoutManager(requireContext())

        view.rv_home.adapter = mFirebaseAdapter

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        mFirebaseAdapter.stopListening()
    }


}