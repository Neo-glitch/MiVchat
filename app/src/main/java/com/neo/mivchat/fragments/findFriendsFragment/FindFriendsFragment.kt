package com.neo.mivchat.fragments.findFriendsFragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
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
import kotlinx.android.synthetic.main.fragment_find_friends.*
import kotlinx.android.synthetic.main.fragment_find_friends.view.*
import kotlinx.android.synthetic.main.fragment_find_friends.view.rv_find_friends
import okhttp3.internal.Internal.instance


class FindFriendsFragment : Fragment() {
    // const
    private val TAG = "FindFriendsFragment"

    private lateinit var mFirebaseAdapter: FirebaseRecyclerAdapter<User, FindFriendsRvViewHolder>
    private val mViewModel by lazy {
        ViewModelProviders.of(this, defaultViewModelProviderFactory)[FindFriendsFragmentViewModel::class.java]
    }

    // var
    private var str = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)  // enables fragment to contribute to toolbar and handle
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: starts")
        val view = inflater.inflate(R.layout.fragment_find_friends, container, false)
        view.rv_find_friends.layoutManager = LinearLayoutManager(requireContext())

        mFirebaseAdapter = mViewModel.initAdapter(requireContext())
        view.rv_find_friends.adapter = mFirebaseAdapter

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_find_friends, menu)

        var searchItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                if (newText == "") {
//                    return true
//                } else {
//                    str += newText
//                    onStart()
//                }
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mFirebaseAdapter.stopListening()
    }

}