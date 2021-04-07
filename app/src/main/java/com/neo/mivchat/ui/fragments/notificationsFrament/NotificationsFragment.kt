package com.neo.mivchat.ui.fragments.notificationsFrament

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.neo.mivchat.R
import com.neo.mivchat.databinding.FragmentNotificationsBinding


class NotificationsFragment : Fragment() {
    private val TAG = "NotificationsFragment"
    private lateinit var  binding: FragmentNotificationsBinding;

    private val mViewModel by lazy {
        ViewModelProvider(this)[NotificationsViewModel::class.java]
    }
//    private val mFirebaseRecyclerAdapter by lazy {
//        mViewModel.initAdapter(requireContext())
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val view = binding.root

        initRecyclerView()
        return view
    }

    private fun initRecyclerView() {
        mViewModel.getFriendRequestsIds()
        binding.rvNotifications.layoutManager = LinearLayoutManager(requireContext())
        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        divider.setDrawable(requireContext().getDrawable(R.drawable.background_toolbar)!!)
        binding.rvNotifications.addItemDecoration(divider)
        val adapter = NotificationsRvAdapterAux(requireContext())
        binding.rvNotifications.adapter = adapter

        mViewModel.mFriendRequestsLive.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }
}