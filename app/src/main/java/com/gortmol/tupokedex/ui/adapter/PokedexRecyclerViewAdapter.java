package com.gortmol.tupokedex.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gortmol.tupokedex.databinding.FragmentPokedexBinding;
import com.gortmol.tupokedex.model.Pokemon;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Pokemon}.
 */
public class PokedexRecyclerViewAdapter extends RecyclerView.Adapter<PokedexRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Pokemon> pokemons;

    public PokedexRecyclerViewAdapter() {

    }

    public void setPokemons(ArrayList<Pokemon> pokemons) {
        this.pokemons = pokemons;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FragmentPokedexBinding binding = FragmentPokedexBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Pokemon currentPokemon = pokemons.get(position);
        holder.bind(currentPokemon);
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

        public void bind(Pokemon pokemon) {
            binding.pokemonIndex.setText(String.format("%03d", pokemon.getIndex()));
            binding.pokemonName.setText(pokemon.getName());
            binding.pokemonCaptured.setVisibility(pokemon.isCaptured() ? View.VISIBLE : View.GONE);
            Picasso.with(binding.getRoot().getContext()).load(pokemon.getSpriteUrl()).into(binding.pokemonSprite);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + binding.pokemonName.getText() + "'";
        }
    }
}