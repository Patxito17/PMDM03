package com.gortmol.tupokedex.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gortmol.tupokedex.databinding.FragmentPokedexListBinding;
import com.gortmol.tupokedex.io.PokemonApiAdapter;
import com.gortmol.tupokedex.io.response.PokemonResponse;
import com.gortmol.tupokedex.model.Pokemon;
import com.gortmol.tupokedex.ui.adapter.PokedexRecyclerViewAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 */
public class PokedexFragment extends Fragment implements Callback<PokemonResponse>, PokedexRecyclerViewAdapter.OnPokemonClickListener {

    private FragmentPokedexListBinding binding;
    private ArrayList<Pokemon> pokemonList;
    private PokedexRecyclerViewAdapter adapter;

    public PokedexFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Call<PokemonResponse> call = PokemonApiAdapter.getApiService().getPokemonList(0, 150);
        call.enqueue(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPokedexListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResponse(Call<PokemonResponse> call, Response<PokemonResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            pokemonList = response.body().getResults();
            adapter = new PokedexRecyclerViewAdapter(this);
            adapter.setPokemons(pokemonList);
            binding.listPokedex.setAdapter(adapter);
            binding.listPokedex.setHasFixedSize(true);
            Log.d("onResponse pokemons", "Size of pokemons: " + pokemonList.size());
        }
    }

    @Override
    public void onFailure(Call<PokemonResponse> call, Throwable throwable) {
        Log.e("onFailure pokemons", "Error fetching data: " + throwable.getMessage(), throwable);
    }

    @Override
    public void onPokemonClick(int position) {
        Pokemon pokemon = pokemonList.get(position);
        if (!pokemon.isCaptured()) {
            pokemon.setCaptured(true);

            adapter.notifyItemChanged(position);
        } else {
            pokemon.setCaptured(false);

            adapter.notifyItemChanged(position);
        }
    }

}