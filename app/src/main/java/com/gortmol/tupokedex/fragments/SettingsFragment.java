package com.gortmol.tupokedex.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.gortmol.tupokedex.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}