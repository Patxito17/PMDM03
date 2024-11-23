package com.gortmol.tupokedex.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.firebase.ui.auth.AuthUI;
import com.gortmol.tupokedex.LoginActivity;
import com.gortmol.tupokedex.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String LANGUAGE_PREF_KEY = "language_switch";
    private static final String DELETE_POKEMON_PREF_KEY = "delete_pokemon_switch";
    private static final String ABOUT_PREF_KEY = "about";
    private static final String LOGOUT_PREF_KEY = "logout";


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Preference prefLanguage = findPreference(LANGUAGE_PREF_KEY);

        Preference prefDeletePokemon = findPreference(DELETE_POKEMON_PREF_KEY);

        Preference prefAbout = findPreference(ABOUT_PREF_KEY);

        Preference logoutPreference = findPreference(LOGOUT_PREF_KEY);
        if (logoutPreference != null) {
            logoutPreference.setOnPreferenceClickListener(preference -> {
                AuthUI.getInstance().signOut(requireContext()).addOnCompleteListener(task -> {
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                });
                return true;
            });
        }
    }
}