package com.gortmol.tupokedex.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gortmol.tupokedex.LoginActivity;
import com.gortmol.tupokedex.R;
import com.gortmol.tupokedex.data.FirestoreHelper;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private static final String PREF_NAME = "AppSettings";
    public static final String PREF_LANGUAGE = "pref_language";
    public static final String PREF_POKEMON_GENERATION = "pref_pokemon_generation";
    public static final String PREF_DELETE_POKEMON = "pref_delete_pokemon";
    public static final String PREF_POKEMONS_ORDER_BY = "pref_pokemons_order_by";
    public static final String PREF_POKEMONS_ORDER_ASC_DESC = "pref_pokemons_order_asc_desc";
    private static final String PREF_LOGOUT = "logout";


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        setupPreferenceListeners();
        syncPreferencesWithFirestore(FirebaseAuth.getInstance().getCurrentUser());
    }

    private void setupPreferenceListeners() {
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

    private void syncPreferencesWithFirestore(FirebaseUser user) {
        FirestoreHelper fh = FirestoreHelper.getInstance();

        fh.getUserSetting(user, PREF_LANGUAGE, value -> {
            if (value instanceof String) {
                ListPreference lp = (ListPreference) findPreference(PREF_LANGUAGE);
                Objects.requireNonNull(lp).setValue((String) value);
            }
        });

        fh.getUserSetting(user, PREF_POKEMON_GENERATION, value -> {
            if (value instanceof String) {
                ListPreference lp = (ListPreference) findPreference(PREF_POKEMON_GENERATION);
                Objects.requireNonNull(lp).setValue((String) value);
            }
        });

        fh.getUserSetting(user, PREF_DELETE_POKEMON, value -> {
            if (value instanceof Boolean) {
                SwitchPreferenceCompat spc = (SwitchPreferenceCompat) findPreference(PREF_DELETE_POKEMON);
                Objects.requireNonNull(spc).setChecked((Boolean) value);
            }
        });

        fh.getUserSetting(user, PREF_POKEMONS_ORDER_BY, value -> {
            if (value instanceof String) {
                ListPreference lp = (ListPreference) findPreference(PREF_POKEMONS_ORDER_BY);
                Objects.requireNonNull(lp).setValue((String) value);
            }
        });

        fh.getUserSetting(user, PREF_POKEMONS_ORDER_ASC_DESC, value -> {
            if (value instanceof String) {
                ListPreference lp = (ListPreference) findPreference(PREF_POKEMONS_ORDER_ASC_DESC);
                Objects.requireNonNull(lp).setValue((String) value);
            }
        });
    }

    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
        Context context = requireContext();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        switch (preference.getKey()) {
            case PREF_LANGUAGE:
                FirestoreHelper.getInstance()
                        .updateUserSetting(context, user, PREF_LANGUAGE, newValue);
                LocaleListCompat appLocales;
                if (newValue.equals("es")) {
                    appLocales = LocaleListCompat.forLanguageTags("es");
                } else {
                    appLocales = LocaleListCompat.forLanguageTags("en");
                }
                AppCompatDelegate.setApplicationLocales(appLocales);
                return true;

            case PREF_POKEMON_GENERATION:
                FirestoreHelper.getInstance()
                        .updateUserSetting(context, user, PREF_POKEMON_GENERATION, newValue);
                return true;

            case PREF_DELETE_POKEMON:
                FirestoreHelper.getInstance()
                        .updateUserSetting(context, user, PREF_DELETE_POKEMON, newValue);
                return true;

            case PREF_POKEMONS_ORDER_BY:
                FirestoreHelper.getInstance()
                        .updateUserSetting(context, user, PREF_POKEMONS_ORDER_BY, newValue);
                return true;

            case PREF_POKEMONS_ORDER_ASC_DESC:
                FirestoreHelper.getInstance()
                        .updateUserSetting(context, user, PREF_POKEMONS_ORDER_ASC_DESC, newValue);
                return true;

            default:
                return false;
        }
    }
}