package com.spellingo.client_app

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceFragmentCompat


class SettingsFragment : PreferenceFragmentCompat() {

    // below describes the method of getting preferences saved in settings
    // val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    // val variable_name = sharedPreferences.get_____(key_name, default_value_if_key_not_present)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}