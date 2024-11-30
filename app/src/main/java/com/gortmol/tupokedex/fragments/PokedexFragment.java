package com.gortmol.tupokedex.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;
import com.gortmol.tupokedex.data.FirestoreHelper;
import com.gortmol.tupokedex.data.PokeApiHelper;
import com.gortmol.tupokedex.databinding.FragmentPokedexListBinding;
import com.gortmol.tupokedex.model.Pokemon;
import com.gortmol.tupokedex.ui.adapter.PokedexRecyclerViewAdapter;

import java.util.ArrayList;

public class PokedexFragment extends Fragment implements PokedexRecyclerViewAdapter.OnPokemonClickListener {

    private static final String TAG = "PokedexFragment";
    private static final int POKEMON_OFFSET = 0;
    private static final int POKEMON_LIMIT = 150;

    private FragmentPokedexListBinding binding;
    private ArrayList<Pokemon> pokemonList;
    private PokedexRecyclerViewAdapter adapter;

    public static ListenerRegistration listenToCapturedPokemonIds;

    public PokedexFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPokedexListBinding.inflate(inflater, container, false);

        loadPokemonsList(POKEMON_OFFSET, POKEMON_LIMIT);

        return binding.getRoot();
    }

    public void loadPokemonsList(int offset, int limit) {
        PokeApiHelper.getInstance().getPokemonList(offset, limit, pokemons -> {
            adapter = new PokedexRecyclerViewAdapter(PokedexFragment.this);
            adapter.setPokemons(pokemons);
            binding.listPokedex.setAdapter(adapter);
            Log.d(TAG, "Lista de Pokémon obtenidos: " + pokemons.size());

            listenToCapturedPokemonIds = FirestoreHelper.getInstance().listenToCapturedPokemonIds(FirebaseAuth.getInstance().getCurrentUser(), capturedPokemonIdList -> {
                for (Pokemon pokemon : pokemons) {
                    pokemon.setCaptured(capturedPokemonIdList.contains(String.valueOf(pokemon.getId())));
                }
                adapter.notifyDataSetChanged();
                Log.d(TAG, "Lista de Pokémon actualizada con estado de captura.");
                pokemonList = pokemons;
            });
        });
    }

    @Override
    public void onPokemonClick(int position) {
        Pokemon pokemon = pokemonList.get(position);

        PokeApiHelper.getInstance().getPokemonById(pokemon.getId(), pokemonCaptured -> {
            if (!pokemon.isCaptured()) {
                pokemon.setCaptured(true);
                FirestoreHelper.getInstance().addPokemon(pokemonCaptured, FirebaseAuth.getInstance().getCurrentUser());
                Log.d(TAG, "Pokémon capturado: " + pokemonCaptured.getName());
            } else {
                pokemon.setCaptured(false);
                FirestoreHelper.getInstance().deletePokemon(pokemonCaptured, FirebaseAuth.getInstance().getCurrentUser());
                Log.d(TAG, "Pokémon liberado: " + pokemon.getName());
            }
            adapter.notifyItemChanged(position);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenToCapturedPokemonIds != null) {
            listenToCapturedPokemonIds.remove();
            Log.d(TAG, "Listener de Pokémon capturados cancelado.");
        }
    }

}