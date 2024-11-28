package com.gortmol.tupokedex.data;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.Preference;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.gortmol.tupokedex.LoginActivity;


public class SharedPreferencesHelper {

    private static final String PREF_NAME = "AppSettings";
    private static final String PREF_LANGUAGE = "pref_language";
    private static final String PREF_DELETE_POKEMON = "pref_delete_pokemon";
    private static final String PREF_POKEMONS_ORDER_BY = "pref_pokemons_order_by";
    private static final String PREF_POKEMONS_ORDER_ASC_DESC = "pref_pokemons_order_asc_desc";
    private static final String PREF_LOGOUT = "logout";



    private static SharedPreferencesHelper instance;
    private final SharedPreferences sp;

    private SharedPreferencesHelper(Context context) {
        this.sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPreferencesHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesHelper(context.getApplicationContext());
        }
        return instance;
    }

    public void setLanguage(String language) {
        sp.edit().putString(PREF_LANGUAGE, language).apply();
    }

    public String getLanguage() {
        return sp.getString(PREF_LANGUAGE, "es");
    }

    public void setDeletePokemonEnabled(boolean isEnabled) {
        sp.edit().putBoolean(PREF_DELETE_POKEMON, isEnabled).apply();
    }

    public boolean isDeletePokemonEnabled() {
        return sp.getBoolean(PREF_DELETE_POKEMON, false);
    }

    public void setPokemonsOrderBy(String orderBy) {
        sp.edit().putString(PREF_POKEMONS_ORDER_BY, orderBy).apply();
    }

    public String getPokemonsOrderBy() {
        return sp.getString(PREF_POKEMONS_ORDER_BY, "id");
    }

    public void setPokemonsOrderAscDesc(String orderAscDesc) {
        sp.edit().putString(PREF_POKEMONS_ORDER_ASC_DESC, orderAscDesc).apply();
    }

    public String getPokemonsOrderAscDesc() {
        return sp.getString(PREF_POKEMONS_ORDER_ASC_DESC, "ascending");
    }
}