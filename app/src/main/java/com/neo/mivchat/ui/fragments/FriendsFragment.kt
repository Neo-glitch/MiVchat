package com.neo.mivchat.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.neo.mivchat.R
import com.neo.mivchat.adapters.FriendsRvAdapter
import com.neo.mivchat.databinding.FragmentFriendsBinding
import com.neo.mivchat.viewmodel.FriendsFragmentViewModel


class FriendsFragment : Fragment() {

    private val mViewModel by lazy {
        ViewModelProvider(this)[FriendsFragmentViewModel::class.java]
    }

    private lateinit var binding: FragmentFriendsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendsBinding.inflate(inflater, container, false)
        val view = binding.root
        initRecyclerView()
        return view
    }

    private fun initRecyclerView() {
        mViewModel.getFriendsUserId()
        val adapter =
            FriendsRvAdapter(
                requireContext()
            )
        binding.rvFriends.layoutManager = LinearLayoutManager(requireContext())
        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        divider.setDrawable(requireContext().resources.getDrawable(R.drawable.rv_item_divider)!!)
        binding.rvFriends.addItemDecoration(divider)
        binding.rvFriends.adapter = adapter

        mViewModel.allFriends.observe(viewLifecycleOwner, Observer { friends ->
            adapter.submitList(friends)
        })
    }

}