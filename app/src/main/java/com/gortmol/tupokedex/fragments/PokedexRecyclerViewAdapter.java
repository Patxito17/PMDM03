package com.gortmol.tupokedex.fragments;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gortmol.tupokedex.data.PokemonPokedexData;
import com.gortmol.tupokedex.databinding.FragmentPokedexBinding;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PokemonPokedexData}.
 */
public class PokedexRecyclerViewAdapter extends RecyclerView.Adapter<PokedexRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<PokemonPokedexData> pokemons;
    private final PokedexFragment pokedexFragment;

    public PokedexRecyclerViewAdapter(ArrayList<PokemonPokedexData> pokemons, PokedexFragment pokedexFragment) {
        this.pokemons = pokemons;
        this.pokedexFragment = pokedexFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FragmentPokedexBinding binding = FragmentPokedexBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        PokemonPokedexData currentPokemon = pokemons.get(position);
        holder.bind(currentPokemon);

        holder.itemView.setOnClickListener(view -> capturePokemon(currentPokemon, view));
    }

    private void capturePokemon(PokemonPokedexData currentPokemon, View view) {
        pokedexFragment.capturePokemon(currentPokemon, view);
    }

    @Override
    public int getItemCount() {
        return pokemons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final FragmentPokedexBinding binding;

        public ViewHolder(@NonNull FragmentPokedexBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PokemonPokedexData pokemon) {
            binding.pokemonIndex.setText(pokemon.getIndex());
            binding.pokemonName.setText(pokemon.getName());
            binding.pokemonCaptured.setVisibility(pokemon.isCaptured() ? View.VISIBLE : View.GONE);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + binding.pokemonName.getText() + "'";
        }
    }
}