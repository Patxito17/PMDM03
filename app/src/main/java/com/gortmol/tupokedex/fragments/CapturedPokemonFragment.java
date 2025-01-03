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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;
import com.gortmol.tupokedex.R;
import com.gortmol.tupokedex.data.FirestoreHelper;
import com.gortmol.tupokedex.databinding.FragmentCapturedPokemonListBinding;
import com.gortmol.tupokedex.model.Pokemon;
import com.gortmol.tupokedex.ui.adapter.CapturedPokemonRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Locale;

public class CapturedPokemonFragment extends Fragment implements CapturedPokemonRecyclerViewAdapter.OnPokemonClickListener, CapturedPokemonRecyclerViewAdapter.OnDeleteClickListener {

    private static final String TAG = "CapturedPokemonFragment";

    private ListenerRegistration listenToCapturedPokemons;

    private FragmentCapturedPokemonListBinding binding;
    private ArrayList<Pokemon> pokemonList;
    private CapturedPokemonRecyclerViewAdapter adapter;

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private SharedPreferences sp;

    private ItemTouchHelper itemTouchHelper;

    public CapturedPokemonFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null)
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

        itemTouchHelper = getItemTouchHelper();

        FirestoreHelper.getInstance().getUserSetting(FirebaseAuth.getInstance().getCurrentUser(), SettingsFragment.PREF_DELETE_POKEMON, deletePokemon -> {
            if (deletePokemon instanceof Boolean) {
                setDeleteEnabled((Boolean) deletePokemon);
            }
        });

        return binding.getRoot();
    }

    @NonNull
    private ItemTouchHelper getItemTouchHelper() {
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                Pokemon removedPokemon = pokemonList.remove(position);
                FirestoreHelper.getInstance()
                        .deletePokemon(removedPokemon, FirebaseAuth.getInstance().getCurrentUser());
                showSnackbarPokemonRemoved(removedPokemon);
            }
        });
    }

    private void showSnackbarPokemonRemoved(Pokemon removedPokemon) {
        String pokemonName = removedPokemon.getName().substring(0, 1).toUpperCase() + removedPokemon.getName().substring(1);
        Snackbar.make(binding.getRoot(), getString(R.string.pokemon_removed) + " " + pokemonName, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> FirestoreHelper.getInstance()
                        .addPokemon(removedPokemon, FirebaseAuth.getInstance().getCurrentUser())).show();
    }

    private void setDeleteEnabled(boolean enabled) {
        if (enabled) {
            if (adapter != null) {
                adapter.setDeleteEnabled(true);
            }
            itemTouchHelper.attachToRecyclerView(binding.listCapturedPokemon);
        } else {
            if (adapter != null) {
                adapter.setDeleteEnabled(false);
            }
            itemTouchHelper.attachToRecyclerView(null);
        }
    }

    private void initializeSharedPreferencesListener() {
        preferenceChangeListener = (sharedPreferences, key) -> {
            if (key != null) {
                switch (key) {

                    case SettingsFragment.PREF_DELETE_POKEMON:
                        boolean deletePokemon = sharedPreferences.getBoolean(SettingsFragment.PREF_DELETE_POKEMON, false);
                        this.setDeleteEnabled(deletePokemon);
                        break;

                    case SettingsFragment.PREF_POKEMONS_ORDER_BY:

                    case SettingsFragment.PREF_POKEMONS_ORDER_ASC_DESC:
                        loadCapturedPokemons(FirebaseAuth.getInstance().getCurrentUser());
                        break;
                }
            }
        };
    }

    private void loadCapturedPokemons(FirebaseUser user) {
        if (user != null && getActivity() != null) {
            SharedPreferences sp = getActivity().getSharedPreferences(SettingsFragment.PREF_NAME, 0);
            String orderType = sp.getString(SettingsFragment.PREF_POKEMONS_ORDER_BY, "id");
            Log.d(TAG, "Order Type: " + orderType);
            String orderDirection = sp.getString(SettingsFragment.PREF_POKEMONS_ORDER_ASC_DESC, "asc");
            Log.d(TAG, "Order Direction: " + orderDirection);
            listenToCapturedPokemons = FirestoreHelper.getInstance().listenToCapturedPokemons(user, orderType, orderDirection, updatedList -> {
                this.pokemonList = updatedList;
                if (adapter == null) {
                    adapter = new CapturedPokemonRecyclerViewAdapter(this, this);
                    binding.listCapturedPokemon.setAdapter(adapter);
                }
                adapter.setPokemons(updatedList);
                Log.d(TAG, "Lista de Pokémon capturados actualizada en tiempo real.");
            });
        }
    }


    @Override
    public void onPokemonClick(int position) {
        Pokemon selectedPokemon = pokemonList.get(position);
        PokemonDetailsFragment detailsFragment = new PokemonDetailsFragment();
        Bundle bundle = getBundle(selectedPokemon);
        detailsFragment.setArguments(bundle);
        detailsFragment.show(getParentFragmentManager(), "PokemonDetailsFragment");
    }

    @NonNull
    private static Bundle getBundle(Pokemon selectedPokemon) {
        Bundle bundle = new Bundle();
        bundle.putString("image", selectedPokemon.getImageUrl());
        String name = selectedPokemon.getName().substring(0, 1).toUpperCase() + selectedPokemon.getName().substring(1);
        bundle.putString("name", name);
        String index = String.format(Locale.ENGLISH, "#%04d", selectedPokemon.getId());
        bundle.putString("index", index);
        bundle.putString("height", String.format(Locale.ENGLISH, "%.2f m", selectedPokemon.getHeight() / 10.0));
        bundle.putString("weight", String.format(Locale.ENGLISH, "%.2f kg", selectedPokemon.getWeight() / 10.0));
        bundle.putString("type1", selectedPokemon.getImageTypes().get(0));
        if (selectedPokemon.getImageTypes().size() > 1) {
            bundle.putString("type2", selectedPokemon.getImageTypes().get(1));
        }
        return bundle;
    }

    @Override
    public void onDeleteButtonClick(int position) {
        Pokemon removedPokemon = pokemonList.get(position);
        FirestoreHelper.getInstance()
                .deletePokemon(removedPokemon, FirebaseAuth.getInstance().getCurrentUser());
        showSnackbarPokemonRemoved(removedPokemon);
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