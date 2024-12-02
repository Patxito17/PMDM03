package com.gortmol.tupokedex.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

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

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    public static final String PREF_NAME = "AppSettings";
    public static final String PREF_LANGUAGE = "pref_language";
    public static final String PREF_POKEMON_GENERATION = "pref_pokemon_generation";
    public static final String PREF_DELETE_POKEMON = "pref_delete_pokemon";
    public static final String PREF_POKEMONS_ORDER_BY = "pref_pokemons_order_by";
    public static final String PREF_POKEMONS_ORDER_ASC_DESC = "pref_pokemons_order_asc_desc";
    private static final String PREF_LOGOUT = "logout";

    private SharedPreferences sp;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        sp = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

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
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);                    CapturedPokemonFragment.listenToCapturedPokemons.remove();
                    PokedexFragment.listenToCapturedPokemonIds.remove();
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
                if (lp != null) {
                    lp.setValue((String) value);
                }
            }
        });

        fh.getUserSetting(user, PREF_POKEMON_GENERATION, value -> {
            if (value instanceof String) {
                ListPreference lp = (ListPreference) findPreference(PREF_POKEMON_GENERATION);
                if (lp != null) {
                    lp.setValue((String) value);
                }
            }
        });

        fh.getUserSetting(user, PREF_DELETE_POKEMON, value -> {
            if (value instanceof Boolean) {
                SwitchPreferenceCompat spc = (SwitchPreferenceCompat) findPreference(PREF_DELETE_POKEMON);
                if (spc != null) {
                    spc.setChecked((Boolean) value);
                }
            }
        });

        fh.getUserSetting(user, PREF_POKEMONS_ORDER_BY, value -> {
            if (value instanceof String) {
                ListPreference lp = (ListPreference) findPreference(PREF_POKEMONS_ORDER_BY);
                if (lp != null) {
                    lp.setValue((String) value);
                }
            }
        });

        fh.getUserSetting(user, PREF_POKEMONS_ORDER_ASC_DESC, value -> {
            if (value instanceof String) {
                ListPreference lp = (ListPreference) findPreference(PREF_POKEMONS_ORDER_ASC_DESC);
                if (lp != null) {
                    lp.setValue((String) value);
                }
            }
        });
    }

    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        switch (preference.getKey()) {

            case PREF_LANGUAGE:
                FirestoreHelper.getInstance().updateUserSetting(requireContext(), user, PREF_LANGUAGE, newValue);
                sp.edit().putString(PREF_LANGUAGE, (String) newValue).apply();
                LocaleListCompat appLocales;
                if (newValue.equals("es")) {
                    appLocales = LocaleListCompat.forLanguageTags("es");
                } else {
                    appLocales = LocaleListCompat.forLanguageTags("en");
                }
                AppCompatDelegate.setApplicationLocales(appLocales);
                return true;

            case PREF_POKEMON_GENERATION:
                FirestoreHelper.getInstance().updateUserSetting(requireContext(), user, PREF_POKEMON_GENERATION, newValue);
                sp.edit().putString(PREF_POKEMON_GENERATION, (String) newValue).apply();
                Log.d("SettingsFragment", "Pokémon Generation preference changed to: " + sp.getString(PREF_POKEMON_GENERATION, ""));
                return true;

            case PREF_DELETE_POKEMON:
                FirestoreHelper.getInstance().updateUserSetting(requireContext(), user, PREF_DELETE_POKEMON, newValue);
                sp.edit().putBoolean(PREF_DELETE_POKEMON, (Boolean) newValue).apply();
                return true;

            case PREF_POKEMONS_ORDER_BY:
                FirestoreHelper.getInstance().updateUserSetting(requireContext(), user, PREF_POKEMONS_ORDER_BY, newValue);
                sp.edit().putString(PREF_POKEMONS_ORDER_BY, (String) newValue).apply();
                return true;

            case PREF_POKEMONS_ORDER_ASC_DESC:
                FirestoreHelper.getInstance().updateUserSetting(requireContext(), user, PREF_POKEMONS_ORDER_ASC_DESC, newValue);
                sp.edit().putString(PREF_POKEMONS_ORDER_ASC_DESC, (String) newValue).apply();
                return true;

            default:
                return false;
        }
    }
}