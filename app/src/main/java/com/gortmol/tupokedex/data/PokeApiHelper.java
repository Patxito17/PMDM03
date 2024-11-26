package com.gortmol.tupokedex.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.gortmol.tupokedex.io.PokemonApiAdapter;
import com.gortmol.tupokedex.io.response.PokemonDetailsResponse;
import com.gortmol.tupokedex.io.response.PokemonResponse;
import com.gortmol.tupokedex.model.Pokemon;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PokeApiHelper {

    private static final String TAG = "PokeApiHelper";
    private static PokeApiHelper instance;

    private PokeApiHelper() {
    }

    public static synchronized PokeApiHelper getInstance() {
        if (instance == null) {
            instance = new PokeApiHelper();
        }
        return instance;
    }

    public void getPokemonList(int offset, int limit, PokemonListCallback callback) {
        Call<PokemonResponse> call = PokemonApiAdapter.getApiService().getPokemonList(offset, limit);
        call.enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(@NonNull Call<PokemonResponse> call, @NonNull Response<PokemonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<Pokemon> pokemonList = response.body().getResults();
                    callback.onSuccess(new ArrayList<>(pokemonList));
                    Log.d(TAG, "Pokémon obtenidos: " + pokemonList.size());
                } else {
                    Log.e(TAG, "Error al obtener la lista de Pokémon: " + response.message());
                    callback.onError(new Exception("Error al obtener la lista de Pokémon"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error en la llamada a la API: " + t.getMessage(), t);
                callback.onError(new Exception(t));
            }
        });
    }

    public void getPokemonById(int id, PokemonDetailsCallback callback) {
        Call<PokemonDetailsResponse> call = PokemonApiAdapter.getApiService().getPokemonDetails(id);
        call.enqueue(new Callback<PokemonDetailsResponse>() {
            @Override
            public void onResponse(@NonNull Call<PokemonDetailsResponse> call, @NonNull Response<PokemonDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PokemonDetailsResponse details = response.body();
                    Pokemon pokemonCaptured = new Pokemon(
                            details.getName(),
                            details.getId(),
                            details.getUrl(),
                            details.getTypeImages(),
                            details.getWeight(),
                            details.getHeight());
                    callback.onSuccess(pokemonCaptured);
                    Log.d(TAG, "Detalles del Pokémon obtenidos: " + details.getName());
                } else {
                    Log.e(TAG, "Error al obtener detalles del Pokémon: " + response.message());
                    callback.onError(new Exception("Error al obtener detalles del Pokémon"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonDetailsResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error en la llamada a la API: " + t.getMessage(), t);
                callback.onError(new Exception(t));
            }
        });
    }

    public interface PokemonListCallback {
        void onSuccess(ArrayList<Pokemon> pokemonList);
        void onError(Exception e);
    }

    public interface PokemonDetailsCallback {
        void onSuccess(Pokemon pokemon);
        void onError(Exception e);
    }
}