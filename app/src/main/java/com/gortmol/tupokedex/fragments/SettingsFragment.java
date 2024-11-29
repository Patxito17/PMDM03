package com.gortmol.tupokedex.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gortmol.tupokedex.LoginActivity;
import com.gortmol.tupokedex.R;
import com.gortmol.tupokedex.data.FirestoreHelper;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private static final String PREF_NAME = "AppSettings";
    private static final String PREF_LANGUAGE = "pref_language";
    private static final String PREF_POKEMON_GENERATION = "pref_pokemon_generation";
    private static final String PREF_DELETE_POKEMON = "pref_delete_pokemon";
    private static final String PREF_POKEMONS_ORDER_BY = "pref_pokemons_order_by";
    private static final String PREF_POKEMONS_ORDER_ASC_DESC = "pref_pokemons_order_asc_desc";
    private static final String PREF_LOGOUT = "logout";


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Preference languagePreference = findPreference(PREF_LANGUAGE);
        if (languagePreference != null) {
            languagePreference.setOnPreferenceChangeListener(this);
        }

        Preference pokemonGenerationPreference = findPreference(PREF_POKEMON_GENERATION);
        if (pokemonGenerationPreference != null) {
            pokemonGenerationPreference.setOnPreferenceChangeListener(this);
        }

        Preference deletePokemonPreference = findPreference(PREF_DELETE_POKEMON);
        if (deletePokemonPreference != null) {
            deletePokemonPreference.setOnPreferenceChangeListener(this);
        }

        Preference pokemonsOrderByPreference = findPreference(PREF_POKEMONS_ORDER_BY);
        if (pokemonsOrderByPreference != null) {
            pokemonsOrderByPreference.setOnPreferenceChangeListener(this);
        }

        Preference pokemonsOrderAscDescPreference = findPreference(PREF_POKEMONS_ORDER_ASC_DESC);
        if (pokemonsOrderAscDescPreference != null) {
            pokemonsOrderAscDescPreference.setOnPreferenceChangeListener(this);
        }

        Preference logoutPreference = findPreference(PREF_LOGOUT);
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


    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
        Context context = requireContext();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        switch (preference.getKey()) {
            case PREF_LANGUAGE:
                FirestoreHelper.getInstance().updateSharedPreference(context, user, PREF_LANGUAGE, newValue);
                LocaleListCompat appLocales;
                if (newValue.equals("es")) {
                    appLocales = LocaleListCompat.forLanguageTags("es");
                } else {
                    appLocales = LocaleListCompat.forLanguageTags("en");
                }
                AppCompatDelegate.setApplicationLocales(appLocales);
                return true;

            case PREF_POKEMON_GENERATION:
                // setPokemonGeneration((String) newValue);
                return true;

            case PREF_DELETE_POKEMON:
                // setDeletePokemonEnabled((Boolean) newValue);
                return true;

            case PREF_POKEMONS_ORDER_BY:
                // setPokemonsOrderBy((String) newValue);
                return true;

            case PREF_POKEMONS_ORDER_ASC_DESC:
                // setPokemonsOrderAscDesc((String) newValue);
                return true;

            default:
                return false;
        }
    }
}