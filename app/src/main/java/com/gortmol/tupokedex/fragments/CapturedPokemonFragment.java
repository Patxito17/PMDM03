package com.gortmol.tupokedex.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gortmol.tupokedex.R;
import com.gortmol.tupokedex.fragments.placeholder.PlaceholderContent;
import com.gortmol.tupokedex.io.PokemonApiAdapter;
import com.gortmol.tupokedex.io.response.PokemonDetailsResponse;
import com.gortmol.tupokedex.model.PokemonCaptured;
import com.gortmol.tupokedex.ui.adapter.CapturedPokemonRecyclerViewAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 */
public class CapturedPokemonFragment extends Fragment implements Callback<PokemonDetailsResponse> {

    private ArrayList<PokemonCaptured> pokemonList;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CapturedPokemonFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Call<PokemonDetailsResponse> call = PokemonApiAdapter.getApiService().getPokemonDetails(6);
        call.enqueue(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_captured_pokemon_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new CapturedPokemonRecyclerViewAdapter(PlaceholderContent.ITEMS));
        }
        return view;
    }

    @Override
    public void onResponse(Call<PokemonDetailsResponse> call, Response<PokemonDetailsResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            PokemonCaptured pokemon = new PokemonCaptured();
            pokemon.setName(response.body().getName());
            pokemon.setId(response.body().getId());
            pokemon.setImageUrl();
            pokemon.setImageTypes(response.body().getTypeImages());
            pokemon.setWeight(response.body().getWeight());
            pokemon.setHeight(response.body().getHeight());
            Log.d("onResponse pokemons captured details",
                    String.format("Name: %s\nId: %d\nImageUrl: %s\nTypes: %s\nWeight: %.2f\nHeight: %.2f\n",
                            pokemon.getName(), pokemon.getId(), pokemon.getSpriteUrl(), pokemon.getImageTypes(), pokemon.getWeight(), pokemon.getHeight()));
        }

    }

    @Override
    public void onFailure(Call<PokemonDetailsResponse> call, Throwable throwable) {
        Log.e("onFailure pokemons captured details", "Error fetching data: " + throwable.getMessage(), throwable);
    }
}