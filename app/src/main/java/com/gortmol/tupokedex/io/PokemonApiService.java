package com.gortmol.tupokedex.io;

import com.gortmol.tupokedex.io.response.PokemonDetails;
import com.gortmol.tupokedex.io.response.PokemonResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PokemonApiService {

    @GET("pokemon")
    Call<PokemonResponse> getPokemonList(@Query("offset") int offset, @Query("limit") int limit);

    @GET("pokemon/{name}")
    Call<PokemonDetails> getPokemonDetails(@Path("name") String name);

}
