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
import com.google.firebase.auth.FirebaseUser;
import com.gortmol.tupokedex.data.FirestoreHelper;
import com.gortmol.tupokedex.data.PokeApiHelper;
import com.gortmol.tupokedex.databinding.FragmentPokedexListBinding;
import com.gortmol.tupokedex.model.Pokemon;
import com.gortmol.tupokedex.model.PokemonCaptured;
import com.gortmol.tupokedex.ui.adapter.PokedexRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 */
public class PokedexFragment extends Fragment implements PokedexRecyclerViewAdapter.OnPokemonClickListener {

    private static final String TAG = "PokedexFragment";

    private FragmentPokedexListBinding binding;
    private ArrayList<Pokemon> pokemonList;
    private PokedexRecyclerViewAdapter adapter;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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

        PokeApiHelper.getInstance().getPokemonList(0, 150, new PokeApiHelper.PokemonListCallback() {
            @Override
            public void onSuccess(ArrayList<Pokemon> pokemonList) {
                PokedexFragment.this.pokemonList = pokemonList;
                adapter = new PokedexRecyclerViewAdapter(PokedexFragment.this);
                adapter.setPokemons(pokemonList);
                binding.listPokedex.setAdapter(adapter);
                binding.listPokedex.setHasFixedSize(true);
                setPokemonCapturedStatus();

                Log.d(TAG, "Lista de Pokémon obtenida: " + pokemonList.size());
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error al obtener la lista de Pokémon: " + e.getMessage(), e);
            }
        });

        return binding.getRoot();
    }

    private void setPokemonCapturedStatus() {
        if (user != null) {
            FirestoreHelper.getInstance().getCapturedPokemonIds(user, pokemonIds -> {
                for (Pokemon pokemon : pokemonList) {
                    if (pokemonIds.contains(String.valueOf(pokemon.getIndex()))) {
                        pokemon.setCaptured(true);
                    } else {
                        pokemon.setCaptured(false);
                    }
                    adapter.notifyItemChanged(pokemonList.indexOf(pokemon));
                }
            });
        }
    }

    @Override
    public void onPokemonClick(int position) {
        Pokemon pokemon = pokemonList.get(position);

        PokeApiHelper.getInstance().getPokemonById(pokemon.getIndex(), new PokeApiHelper.PokemonDetailsCallback() {
            @Override
            public void onSuccess(PokemonCaptured pokemonCaptured) {
                if (!pokemon.isCaptured()) {
                    pokemon.setCaptured(true);
                    FirestoreHelper.getInstance().addPokemon(pokemonCaptured, user);
                    Log.d("PokedexFragment", "Pokémon capturado: " + pokemonCaptured.getName());
                } else {
                    pokemon.setCaptured(false);
                    FirestoreHelper.getInstance().deletePokemon(pokemonCaptured, user);
                    Log.d("PokedexFragment", "Pokémon liberado: " + pokemonCaptured.getName());
                }

                adapter.notifyItemChanged(position);
            }

            @Override
            public void onError(Exception e) {
                Log.e("PokedexFragment", "Error al obtener detalles del Pokémon: " + e.getMessage(), e);
            }
        });
    }
}