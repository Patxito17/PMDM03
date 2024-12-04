package com.gortmol.tupokedex.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

    private static final String TAG = "SettingsFragment";

    public static final String PREF_NAME = "AppSettings";
    public static final String PREF_LANGUAGE = "pref_language";
    public static final String PREF_POKEMON_GENERATION = "pref_pokemon_generation";
    public static final String PREF_DELETE_POKEMON = "pref_delete_pokemon";
    public static final String PREF_POKEMONS_ORDER_BY = "pref_pokemons_order_by";
    public static final String PREF_POKEMONS_ORDER_ASC_DESC = "pref_pokemons_order_asc_desc";
    public static final String PREF_ABOUT = "pref_about";
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

        Preference aboutPreference = findPreference(PREF_ABOUT);
        if (aboutPreference != null) {
            aboutPreference.setOnPreferenceClickListener(preference -> {
                showAboutDialog();
                return true;
            });
        }

        Preference logoutPreference = findPreference(PREF_LOGOUT);
        if (logoutPreference != null) {
            logoutPreference.setOnPreferenceClickListener(preference -> {
                logout();
                return true;
            });
        }
    }

    private void logout() {
        new android.app.AlertDialog.Builder(requireContext()).setTitle(R.string.end_session).setMessage(R.string.end_session_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> AuthUI.getInstance().signOut(requireContext()).addOnCompleteListener(task -> {
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                    Log.d(TAG, "User signed out");
                })).setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss()).show();
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.requireContext());
        builder.setTitle(R.string.about).setMessage(R.string.about_message).setIcon(R.drawable.ic_about).setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void syncPreferencesWithFirestore(FirebaseUser user) {
        FirestoreHelper fh = FirestoreHelper.getInstance();

        fh.getUserSetting(user, PREF_LANGUAGE, value -> {
            if (value instanceof String) {
                ListPreference lp = findPreference(PREF_LANGUAGE);
                if (lp != null) {
                    lp.setValue((String) value);
                    Log.d(TAG, "Preferencia sincronizada con Firestore: " + lp.getKey() + " = " + value);
                }
            }
        });

        fh.getUserSetting(user, PREF_POKEMON_GENERATION, value -> {
            if (value instanceof String) {
                ListPreference lp = findPreference(PREF_POKEMON_GENERATION);
                if (lp != null) {
                    lp.setValue((String) value);
                    Log.d(TAG, "Preferencia sincronizada con Firestore: " + lp.getKey() + " = " + value);
                }
            }
        });

        fh.getUserSetting(user, PREF_DELETE_POKEMON, value -> {
            if (value instanceof Boolean) {
                SwitchPreferenceCompat spc = findPreference(PREF_DELETE_POKEMON);
                if (spc != null) {
                    spc.setChecked((Boolean) value);
                    Log.d(TAG, "Preferencia sincronizada con Firestore: " + spc.getKey() + " = " + value);
                }
            }
        });

        fh.getUserSetting(user, PREF_POKEMONS_ORDER_BY, value -> {
            if (value instanceof String) {
                ListPreference lp = findPreference(PREF_POKEMONS_ORDER_BY);
                if (lp != null) {
                    lp.setValue((String) value);
                    Log.d(TAG, "Preferencia sincronizada con Firestore: " + lp.getKey() + " = " + value);
                }
            }
        });

        fh.getUserSetting(user, PREF_POKEMONS_ORDER_ASC_DESC, value -> {
            if (value instanceof String) {
                ListPreference lp = findPreference(PREF_POKEMONS_ORDER_ASC_DESC);
                if (lp != null) {
                    lp.setValue((String) value);
                    Log.d(TAG, "Preferencia sincronizada con Firestore: " + lp.getKey() + " = " + value);
                }
            }
        });
    }

    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        switch (preference.getKey()) {

            case PREF_LANGUAGE:
                FirestoreHelper.getInstance().updateUserSetting(user, PREF_LANGUAGE, newValue);
                sp.edit().putString(PREF_LANGUAGE, (String) newValue).apply();
                LocaleListCompat appLocales;
                if (newValue.equals("es")) {
                    appLocales = LocaleListCompat.forLanguageTags("es");
                } else {
                    appLocales = LocaleListCompat.forLanguageTags("en");
                }
                AppCompatDelegate.setApplicationLocales(appLocales);
                Toast.makeText(requireContext(), getString(R.string.language_changed), Toast.LENGTH_LONG).show();
                Log.d(TAG, "Idioma cambiado a " + newValue);
                return true;

            case PREF_POKEMON_GENERATION:
                new android.app.AlertDialog.Builder(requireContext()).setTitle(R.string.restart_app).setMessage(R.string.restart_app_message)
                        .setPositiveButton(R.string.restart, (dialog, which) -> {
                            FirestoreHelper.getInstance().updateUserSetting(user, PREF_POKEMON_GENERATION, newValue);
                            sp.edit().putString(PREF_POKEMON_GENERATION, (String) newValue).apply();
                            Intent intent = requireContext().getPackageManager().getLaunchIntentForPackage(requireContext().getPackageName());
                            if (intent != null) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Toast.makeText(requireContext(), getString(R.string.toas_pokemon_generation), Toast.LENGTH_LONG).show();
                            }
                            requireActivity().finish();
                            Log.d(TAG, "Generación de Pokémon cambiada a " + newValue);
                        }).setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss()).show();
                return false;

            case PREF_DELETE_POKEMON:
                FirestoreHelper.getInstance().updateUserSetting(user, PREF_DELETE_POKEMON, newValue);
                sp.edit().putBoolean(PREF_DELETE_POKEMON, (Boolean) newValue).apply();
                Toast.makeText(requireContext(), getString(R.string.toast_delete_pokemon) + " " + ((boolean) newValue ? getString(R.string.activated) : getString(R.string.disabled)), Toast.LENGTH_LONG).show();
                Log.d(TAG, "Eliminar Pokémon activado: " + newValue);
                return true;

            case PREF_POKEMONS_ORDER_BY:
                FirestoreHelper.getInstance().updateUserSetting(user, PREF_POKEMONS_ORDER_BY, newValue);
                sp.edit().putString(PREF_POKEMONS_ORDER_BY, (String) newValue).apply();
                Toast.makeText(requireContext(), getString(R.string.toast_order_by), Toast.LENGTH_LONG).show();
                Log.d(TAG, "Ordenar Pokémon por: " + newValue);
                return true;

            case PREF_POKEMONS_ORDER_ASC_DESC:
                FirestoreHelper.getInstance().updateUserSetting(user, PREF_POKEMONS_ORDER_ASC_DESC, newValue);
                sp.edit().putString(PREF_POKEMONS_ORDER_ASC_DESC, (String) newValue).apply();
                Toast.makeText(requireContext(), getString(R.string.toast_order_asc_desc), Toast.LENGTH_LONG).show();
                Log.d(TAG, "Ordenar Pokémon de forma: " + newValue);
                return true;

            default:
                return false;
        }
    }
}