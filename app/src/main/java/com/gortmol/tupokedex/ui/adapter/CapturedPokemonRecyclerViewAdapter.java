package com.gortmol.tupokedex.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gortmol.tupokedex.databinding.FragmentCapturedPokemonBinding;
import com.gortmol.tupokedex.model.Pokemon;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Pokemon}.
 */
public class CapturedPokemonRecyclerViewAdapter extends RecyclerView.Adapter<CapturedPokemonRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Pokemon> pokemonCaptureds;
    private CapturedPokemonRecyclerViewAdapter.OnPokemonClickListener onPokemonClickListener;
    private CapturedPokemonRecyclerViewAdapter.OnDeleteClickListener onDeleteClickListener;
    private boolean deleteEnabled = false;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FragmentCapturedPokemonBinding binding = FragmentCapturedPokemonBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Pokemon currentPokemon = pokemonCaptureds.get(position);
        holder.bind(currentPokemon);

        holder.itemView.setOnClickListener(view -> {
            if (onPokemonClickListener != null) {
                onPokemonClickListener.onPokemonClick(position);
            }
        });

        if (deleteEnabled) {
            holder.binding.deleteButton.setVisibility(View.VISIBLE);
            holder.binding.deleteButton.setOnClickListener(view -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteButtonClick(holder.getBindingAdapterPosition());
                }
            });
        } else {
            holder.binding.deleteButton.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> {
            if (onPokemonClickListener != null) {
                onPokemonClickListener.onPokemonClick(holder.getBindingAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return pokemonCaptureds.size();
    }

    public void setDeleteEnabled(boolean enabled) {
        this.deleteEnabled = enabled;
        notifyDataSetChanged(); // Refresca todos los elementos del RecyclerView para reflejar el cambio
    }

    public interface OnPokemonClickListener {
        void onPokemonClick(int position);
    }

    public interface OnDeleteClickListener {
        void onDeleteButtonClick(int position);
    }

    public CapturedPokemonRecyclerViewAdapter(CapturedPokemonRecyclerViewAdapter.OnPokemonClickListener onPokemonClickListener, CapturedPokemonRecyclerViewAdapter.OnDeleteClickListener onDeleteClickListener) {
        this.onPokemonClickListener = onPokemonClickListener;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public void setPokemons(ArrayList<Pokemon> pokemonCaptureds) {
        this.pokemonCaptureds = pokemonCaptureds;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final FragmentCapturedPokemonBinding binding;

        public ViewHolder(@NonNull FragmentCapturedPokemonBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Pokemon currentPokemon) {

            Picasso.with(binding.getRoot().getContext()).load(currentPokemon.getImageUrl()).into(binding.pokemonImage);
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