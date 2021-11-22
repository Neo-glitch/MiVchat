package com.neo.mivchat.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.neo.mivchat.R
import com.neo.mivchat.adapters.FindFriendsRvAdapter
import com.neo.mivchat.databinding.FragmentFindFriendsBinding
import com.neo.mivchat.viewmodel.FindFriendsFragmentViewModel


class FindFriendsFragment : Fragment() {
    // const
    private val TAG = "FindFriendsFragment"
    private val mViewModel by lazy {
        ViewModelProvider(this)[FindFriendsFragmentViewModel::class.java]
    }
    private lateinit var binding: FragmentFindFriendsBinding


    // var
    private var str = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)  // enables fragment to contribute to toolbar and handle
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: starts")
        binding = FragmentFindFriendsBinding.inflate(inflater, container, false)
        val view = binding.root
        mViewModel.getAllUsersFromFirebaseAndUpdateDb()
        initRecyclerView()
        return view
    }

    private fun initRecyclerView() {
        val adapter =
            FindFriendsRvAdapter(
                requireContext()
            )
        binding.rvFindFriends.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFindFriends.adapter = adapter
        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        divider.setDrawable(requireContext().resources.getDrawable(R.drawable.rv_item_divider)!!)
        binding.rvFindFriends.addItemDecoration(divider)

        mViewModel.allUsers.observe(viewLifecycleOwner, Observer {users ->
            adapter.submitList(users)
        })
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.menu_find_friends, menu)
//
//        var searchItem = menu.findItem(R.id.action_search)
//        val searchView: SearchView = MenuItemCompat.getActionView(searchItem) as SearchView
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//
//                searchView.clearFocus()
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
////                if (newText == "") {
////                    return true
////                } else {
////                    str += newText
////                    onStart()
////                }
//                return false
//            }
//        })
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.action_search -> {
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

}