package com.gortmol.tupokedex.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;
import com.gortmol.tupokedex.data.FirestoreHelper;
import com.gortmol.tupokedex.databinding.FragmentCapturedPokemonListBinding;
import com.gortmol.tupokedex.model.Pokemon;
import com.gortmol.tupokedex.ui.adapter.CapturedPokemonRecyclerViewAdapter;

import java.util.ArrayList;

public class CapturedPokemonFragment extends Fragment implements CapturedPokemonRecyclerViewAdapter.OnPokemonClickListener {

    private static final String TAG = "CapturedPokemonFragment";

    public static ListenerRegistration listenToCapturedPokemons;

    private FragmentCapturedPokemonListBinding binding;
    private ArrayList<Pokemon> pokemonList;
    private CapturedPokemonRecyclerViewAdapter adapter;

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private SharedPreferences sp;

    public CapturedPokemonFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getActivity().getSharedPreferences(SettingsFragment.PREF_NAME, Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCapturedPokemonListBinding.inflate(inflater, container, false);

        initializeSharedPreferencesListener();
        sp.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        loadCapturedPokemons(FirebaseAuth.getInstance().getCurrentUser());

        return binding.getRoot();
    }

    private void initializeSharedPreferencesListener() {
        preferenceChangeListener = (sharedPreferences, key) -> {
            switch (key) {

                case SettingsFragment.PREF_DELETE_POKEMON:
                    boolean deletePokemon = sharedPreferences.getBoolean(SettingsFragment.PREF_DELETE_POKEMON, false);
                    break;

                case SettingsFragment.PREF_POKEMONS_ORDER_BY:

                case SettingsFragment.PREF_POKEMONS_ORDER_ASC_DESC:
                    loadCapturedPokemons(FirebaseAuth.getInstance().getCurrentUser());
                    break;
            }
        };
    }

    private void loadCapturedPokemons(FirebaseUser user) {
        if (user != null) {
            SharedPreferences sp = getActivity().getSharedPreferences(SettingsFragment.PREF_NAME, 0);
            String orderType = sp.getString(SettingsFragment.PREF_POKEMONS_ORDER_BY, "id");
            Log.d(TAG, "Order Type: " + orderType);
            String orderDirection = sp.getString(SettingsFragment.PREF_POKEMONS_ORDER_ASC_DESC, "asc");
            Log.d(TAG, "Order Direction: " + orderDirection);
            listenToCapturedPokemons = FirestoreHelper.getInstance().listenToCapturedPokemons(user, orderType, orderDirection, updatedList -> {
                this.pokemonList = updatedList;
                if (adapter == null) {
                    adapter = new CapturedPokemonRecyclerViewAdapter(this);
                    binding.listCapturedPokemon.setAdapter(adapter);
                }
                adapter.setPokemons(updatedList);
                Log.d(TAG, "Lista de Pokémon capturados actualizada en tiempo real.");
            });
        }
    }


    @Override
    public void onPokemonClick(int position) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenToCapturedPokemons != null) {
            listenToCapturedPokemons.remove();
            Log.d(TAG, "Listener de Pokémon capturados removido.");
        }
        if (preferenceChangeListener != null) {
            sp.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
            Log.d(TAG, "Listener de preferencias removido.");
        }
    }

}