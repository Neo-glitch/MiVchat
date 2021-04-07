package com.neo.mivchat.dataSource.database

import androidx.fragment.app.Fragment


/**
 * class used to hold each fragment and it's tag to implement Instagram like backstack
 */
data class FragmentTag (var fragment: Fragment? = null, var tag: String? = null)