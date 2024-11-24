package com.gortmol.tupokedex.io;

import com.gortmol.tupokedex.io.response.PokemonDetailsResponse;
import com.gortmol.tupokedex.io.response.PokemonResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PokemonApiService {

    @GET("pokemon")
    Call<PokemonResponse> getPokemonList(@Query("offset") int offset, @Query("limit") int limit);

    @GET("pokemon/{id}")
    Call<PokemonDetailsResponse> getPokemonDetails(@Path("id") int id);

}