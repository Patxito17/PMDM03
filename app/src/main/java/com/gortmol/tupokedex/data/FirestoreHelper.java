package com.gortmol.tupokedex.data;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gortmol.tupokedex.model.Pokemon;

import java.util.ArrayList;
import java.util.function.Consumer;

public class FirestoreHelper {

    private static final String TAG = "FirestoreHelper";
    private static FirestoreHelper instance;
    private final FirebaseFirestore db;

    private FirestoreHelper() {
        this.db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirestoreHelper getInstance() {
        if (instance == null) {
            instance = new FirestoreHelper();
        }
        return instance;
    }

    public void addPokemon(Pokemon pokemon, FirebaseUser user) {
        if (user == null) {
            Log.e(TAG, "Error: Usuario no autenticado");
            return;
        }

        db.collection("users").document(user.getUid())
                .collection("captured_pokemons").document(String.valueOf(pokemon.getId()))
                .set(pokemon)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Pokemon añadido con éxito: " + pokemon.getName()))
                .addOnFailureListener(e -> Log.e(TAG, "Error al añadir el Pokemon: " + e.getMessage()));
    }

    public void deletePokemon(Pokemon pokemon, FirebaseUser user) {
        if (user == null) {
            Log.e(TAG, "Error: Usuario no autenticado");
            return;
        }

        db.collection("users").document(user.getUid())
                .collection("captured_pokemons").document(String.valueOf(pokemon.getId()))
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Pokemon eliminado: " + pokemon.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Error al eliminar el Pokemon: " + e.getMessage()));
    }

    public void listenToCapturedPokemons(FirebaseUser user, Consumer<ArrayList<Pokemon>> callback) {
        if (user == null) {
            Log.e(TAG, "Error: Usuario no autenticado");
            callback.accept(new ArrayList<>());
            return;
        }

        db.collection("users").document(user.getUid())
                .collection("captured_pokemons")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error al escuchar los Pokémon capturados: ", e);
                        return;
                    }
                    if (snapshots != null) {
                        ArrayList<Pokemon> capturedPokemonList = new ArrayList<>();
                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            Pokemon pokemon = document.toObject(Pokemon.class);
                            pokemon.setImageUrl();
                            capturedPokemonList.add(pokemon);
                        }
                        callback.accept(capturedPokemonList);
                    }
                });
    }

    public void listenToCapturedPokemonIds(FirebaseUser user, Consumer<ArrayList<String>> callback) {
        if (user == null) {
            Log.e(TAG, "Error: Usuario no autenticado");
            return;
        }

        db.collection("users").document(user.getUid())
                .collection("captured_pokemons")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error al escuchar los Pokémon capturados: ", e);
                        return;
                    }
                    if (snapshots != null) {
                        ArrayList<String> capturedPokemonIdList = new ArrayList<>();
                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            capturedPokemonIdList.add(document.getId());
                        }
                        callback.accept(capturedPokemonIdList);
                    }
                });
    }
}