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
import com.gortmol.tupokedex.ui.adapter.PokedexRecyclerViewAdapter;

import java.util.ArrayList;

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
            public void onSuccess(ArrayList<Pokemon> pokemons) {
                PokedexFragment.this.pokemonList = pokemons;
                adapter = new PokedexRecyclerViewAdapter(PokedexFragment.this);
                setInitialPokemonCapturedStatus();
                setPokemonCapturedStatus();
                adapter.setPokemons(PokedexFragment.this.pokemonList);
                binding.listPokedex.setAdapter(adapter);
                binding.listPokedex.setHasFixedSize(true);
                Log.d(TAG, "Lista de Pokémon obtenida: " + pokemonList.size());
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error al obtener la lista de Pokémon: " + e.getMessage(), e);
            }
        });



        return binding.getRoot();
    }

    private void setInitialPokemonCapturedStatus() {
        if (user != null) {
            FirestoreHelper.getInstance().getCapturedPokemonIds(user, capturedIds -> {
                for (Pokemon pokemon : pokemonList) {
                    pokemon.setCaptured(capturedIds.contains(String.valueOf(pokemon.getId())));
                    adapter.notifyItemChanged(pokemonList.indexOf(pokemon));
                }
                Log.d(TAG, "Estados de Pokémon en Pokédex actualizados al cargar.");
            });
        }
    }

    private void setPokemonCapturedStatus() {
        if (user != null) {
            FirestoreHelper.getInstance().listenToCapturedPokemonIds(user, new FirestoreHelper.OnCapturedIdsFetched() {
                @Override
                public void onCapturedIdsFetched(ArrayList<String> capturedIds) {
                    for (Pokemon pokemon : pokemonList) {
                        boolean isCaptured = capturedIds.contains(String.valueOf(pokemon.getId()));
                        if (pokemon.isCaptured() != isCaptured) {
                            pokemon.setCaptured(isCaptured);  // Cambiamos el estado solo si ha cambiado
                            adapter.notifyItemChanged(pokemonList.indexOf(pokemon));
                        }
                    }
                    Log.d(TAG, "Estados de Pokémon en Pokédex actualizados en tiempo real.");
                }
            });
        }
    }

    @Override
    public void onPokemonClick(int position) {
        Pokemon pokemon = pokemonList.get(position);

        PokeApiHelper.getInstance().getPokemonById(pokemon.getId(), new PokeApiHelper.PokemonDetailsCallback() {
            @Override
            public void onSuccess(Pokemon pokemonCaptured) {
                if (!pokemon.isCaptured()) {
                    pokemon.setCaptured(true);
                    FirestoreHelper.getInstance().addPokemon(pokemonCaptured, user);
                    Log.d("PokedexFragment", "Pokémon capturado: " + pokemonCaptured.getName());
                } else {
                    pokemon.setCaptured(false);
                    FirestoreHelper.getInstance().deletePokemon(pokemonCaptured, user);
                    Log.d("PokedexFragment", "Pokémon liberado: " + pokemon.getName());
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