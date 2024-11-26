package com.gortmol.tupokedex.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gortmol.tupokedex.R;
import com.gortmol.tupokedex.databinding.FragmentCapturedPokemonBinding;
import com.gortmol.tupokedex.model.PokemonCaptured;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PokemonCaptured}.
 */
public class CapturedPokemonRecyclerViewAdapter extends RecyclerView.Adapter<CapturedPokemonRecyclerViewAdapter.ViewHolder> {

    private ArrayList<PokemonCaptured> pokemonCaptureds;
    private CapturedPokemonRecyclerViewAdapter.OnPokemonClickListener listener;

    public interface OnPokemonClickListener {
        void onPokemonClick(int position);
    }

    public CapturedPokemonRecyclerViewAdapter(CapturedPokemonRecyclerViewAdapter.OnPokemonClickListener listener) {
        this.listener = listener;
    }

    public void setPokemons(ArrayList<PokemonCaptured> pokemonCaptureds) {
        this.pokemonCaptureds = pokemonCaptureds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FragmentCapturedPokemonBinding binding = FragmentCapturedPokemonBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        PokemonCaptured currentPokemon = pokemonCaptureds.get(position);
        holder.bind(currentPokemon);

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onPokemonClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pokemonCaptureds.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final FragmentCapturedPokemonBinding binding;

        public ViewHolder(@NonNull FragmentCapturedPokemonBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PokemonCaptured currentPokemon) {

            Picasso.with(binding.getRoot().getContext()).load(currentPokemon.getSpriteUrl()).into(binding.pokemonImage);
            binding.id.setText(String.valueOf(currentPokemon.getId()));
            binding.name.setText(currentPokemon.getName());
            binding.height.setText(String.valueOf(currentPokemon.getHeight()));
            binding.weight.setText(String.valueOf(currentPokemon.getWeight()));
            Picasso.with(binding.getRoot().getContext()).load(currentPokemon.getImageTypes().get(0)).into(binding.abilityImage1);
            if (currentPokemon.getImageTypes().size() > 1) {
                Picasso.with(binding.getRoot().getContext()).load(currentPokemon.getImageTypes().get(1)).into(binding.abilityImage2);
            }
        }

        @Override
        public String toString() {
            return super.toString() + " '" + binding.name.getText() + "'";
        }

    }
}