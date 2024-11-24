package com.gortmol.tupokedex.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gortmol.tupokedex.data.PokemonPokedexData;
import com.gortmol.tupokedex.databinding.FragmentPokedexListBinding;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 */
public class PokedexFragment extends Fragment {

    private FragmentPokedexListBinding binding;
    private ArrayList<PokemonPokedexData> pokemonList;
    private PokedexRecyclerViewAdapter adapter;

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

        // Para borrar
        pokemonList = new ArrayList<>();
        pokemonList.add(new PokemonPokedexData("Pikachu", "025", true));
        pokemonList.add(new PokemonPokedexData("Bulbasaur", "001", false));


        binding = FragmentPokedexListBinding.inflate(inflater, container, false);
        adapter = new PokedexRecyclerViewAdapter(pokemonList, this);
        binding.listPokedex.setAdapter(adapter);
        binding.listPokedex.setLayoutManager(new LinearLayoutManager(getContext()));
        return binding.getRoot();
    }

    public void capturePokemon(PokemonPokedexData currentPokemon, View view) {
    }
}