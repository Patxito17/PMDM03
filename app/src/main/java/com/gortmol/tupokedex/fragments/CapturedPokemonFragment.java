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
import com.gortmol.tupokedex.model.Pokemon;
import com.gortmol.tupokedex.ui.adapter.CapturedPokemonRecyclerViewAdapter;

import java.util.ArrayList;

public class CapturedPokemonFragment extends Fragment implements CapturedPokemonRecyclerViewAdapter.OnPokemonClickListener {

    private static final String TAG = "CapturedPokemonFragment";

    private FragmentCapturedPokemonListBinding binding;
    private ArrayList<Pokemon> pokemonList;
    private CapturedPokemonRecyclerViewAdapter adapter;

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

        loadCapturedPokemons(FirebaseAuth.getInstance().getCurrentUser());

        return binding.getRoot();
    }

    private void loadCapturedPokemons(FirebaseUser user) {
        if (user != null) {
            FirestoreHelper.getInstance().listenToCapturedPokemons(user, updatedList -> {
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
}