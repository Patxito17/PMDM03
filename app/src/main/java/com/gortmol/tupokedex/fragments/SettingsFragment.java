package com.gortmol.tupokedex.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.firebase.ui.auth.AuthUI;
import com.gortmol.tupokedex.LoginActivity;
import com.gortmol.tupokedex.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        // Buscar la preferencia de cerrar sesión
        Preference logoutPreference = findPreference("logout");
        if (logoutPreference != null) {
            logoutPreference.setOnPreferenceClickListener(preference -> {
                // Cerrar sesión con Firebase
                AuthUI.getInstance().signOut(requireContext()).addOnCompleteListener(task -> {
                    // Redirige al LoginActivity después de cerrar sesión
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    startActivity(intent);
                    requireActivity().finish(); // Finaliza la actividad actual
                });
                return true;
            });
        }
    }
}