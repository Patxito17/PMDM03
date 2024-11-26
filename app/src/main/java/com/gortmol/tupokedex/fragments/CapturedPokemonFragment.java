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
import com.gortmol.tupokedex.databinding.FragmentCapturedPokemonListBinding;
import com.gortmol.tupokedex.model.PokemonCaptured;
import com.gortmol.tupokedex.ui.adapter.CapturedPokemonRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 */
public class CapturedPokemonFragment extends Fragment implements CapturedPokemonRecyclerViewAdapter.OnPokemonClickListener {

    private static final String TAG = "CapturedPokemonFragment";

    private FragmentCapturedPokemonListBinding binding;
    private ArrayList<PokemonCaptured> pokemonList;
    private CapturedPokemonRecyclerViewAdapter adapter;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public CapturedPokemonFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCapturedPokemonListBinding.inflate(inflater, container, false);

        if (user != null) {
            FirestoreHelper.getInstance().getPokemonCapturedList(user, pokemonList -> {
                CapturedPokemonFragment.this.pokemonList = pokemonList;
                adapter = new CapturedPokemonRecyclerViewAdapter(CapturedPokemonFragment.this);
                adapter.setPokemons(pokemonList);
                binding.listCapturedPokemon.setAdapter(adapter);
                binding.listCapturedPokemon.setHasFixedSize(true);
                Log.d(TAG, "Obtenida lista de pokemons capturados: " + pokemonList.size());
            });
        }

        return binding.getRoot();
    }

    @Override
    public void onPokemonClick(int position) {

    }
}