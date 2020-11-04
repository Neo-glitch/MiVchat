package com.neo.mivchat.fragments.findFriendsFragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
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


class FindFriendsFragment : Fragment() {
    // const
    private val TAG = "FindFriendsFragment"

    // firebase
    private val mUsersRef by lazy {
        FirebaseDatabase.getInstance().reference.child("users")
    }
    private lateinit var mFirebaseAdapter: FirebaseRecyclerAdapter<User, FindFriendsRvViewHolder>

    // var
    private var str = ""
    private val mListener by lazy {
        requireActivity() as IMainActivity
    }

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

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: starts")
        val options = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(
                mUsersRef.orderByChild("name"),
                User::class.java
            )
            .build()
        mFirebaseAdapter = initFirebaseRvAdapter(options)
        rv_find_friends.adapter = mFirebaseAdapter
        mFirebaseAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mFirebaseAdapter.stopListening()
    }


    private fun initFirebaseRvAdapter(options: FirebaseRecyclerOptions<User>): FirebaseRecyclerAdapter<User, FindFriendsRvViewHolder> {
        Log.d(TAG, "initFirebaseRvAdapter: starts")
        return object : FirebaseRecyclerAdapter<User, FindFriendsRvViewHolder>(options){
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): FindFriendsRvViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_find_friends, parent, false)
                return FindFriendsRvViewHolder(view)
            }


            override fun onBindViewHolder(
                holder: FindFriendsRvViewHolder,
                position: Int,
                model: User
            ) {
                var listUserId = getRef(position).key!!
                holder.userName.text = model.name
                holder.userBio.text = model.bio
                if (model.profile_image != "") {
                    Picasso.get().load(model.profile_image)
                        .placeholder(R.drawable.profile_image)
                        .into(holder.userImage)
                }
                holder.itemView.setOnClickListener {
                    mListener.inflateProfileFragment(
                        listUserId,
                        model.profile_image!!,
                        model.name!!
                    )
                }
            }
        }
    }

}