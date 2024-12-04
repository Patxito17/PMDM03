package com.gortmol.tupokedex.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.gortmol.tupokedex.databinding.FragmentPokemonDetailsBinding;
import com.squareup.picasso.Picasso;

public class PokemonDetailsFragment extends DialogFragment {

    private FragmentPokemonDetailsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPokemonDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            String image = getArguments().getString("image");
            Picasso.get().load(image).into(binding.image);
            String index = getArguments().getString("index");
            binding.index.setText(index);
            String name = getArguments().getString("name");
            binding.name.setText(name);
            String type1 = getArguments().getString("type1");
            Picasso.get().load(type1).into(binding.type1);
            if (getArguments().getString("type2") != null) {
                String type2 = getArguments().getString("type2");
                Picasso.get().load(type2).into(binding.type2);
            }
            String height = getArguments().getString("height");
            binding.height.setText(height);
            String weight = getArguments().getString("weight");
            binding.weight.setText(weight);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}