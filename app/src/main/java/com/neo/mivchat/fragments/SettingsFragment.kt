package com.neo.mivchat.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.neo.mivchat.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}