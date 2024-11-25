package com.gortmol.tupokedex.data;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gortmol.tupokedex.model.PokemonCaptured;

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

    public void addPokemon(PokemonCaptured pokemon, FirebaseUser user) {
        if (user == null) {
            Log.e(TAG, "Error: Usuario no autenticado");
            return;
        }

        String userId = user.getUid();

        db.collection("users").document(userId).collection("captured_pokemons").document(String.valueOf(pokemon.getId()))
                .set(pokemon)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Pokemon añadido con éxito: " + pokemon.getName()))
                .addOnFailureListener(e -> Log.e(TAG, "Error al añadir el Pokemon: " + e.getMessage()));
    }
    public void deletePokemon(PokemonCaptured pokemon, FirebaseUser user) {
        if (user == null) {
            Log.e(TAG, "Error: Usuario no autenticado");
            return;
        }

        String userId = user.getUid();

        db.collection("users").document(userId).collection("captured_pokemons").document(String.valueOf(pokemon.getId()))
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Pokemon eliminado: " + pokemon.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Error al eliminar el Pokemon: " + e.getMessage()));
    }

    public void getPokemonById(String pokemonId, FirebaseUser user, Consumer<PokemonCaptured> callback) {
        if (user == null) {
            Log.e(TAG, "Error: Usuario no autenticado");
            callback.accept(null);
            return;
        }

        String userId = user.getUid();

        db.collection("users").document(userId).collection("captured_pokemons").document(pokemonId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        PokemonCaptured pokemon = documentSnapshot.toObject(PokemonCaptured.class);
                        if (pokemon != null) {
                            pokemon.setImageUrl(); // Asegurar que la URL de la imagen se genere correctamente
                            callback.accept(pokemon);
                        } else {
                            Log.e(TAG, "Error: No se pudo convertir el documento en PokemonCaptured");
                            callback.accept(null);
                        }
                    } else {
                        Log.e(TAG, "Error: No se encontró el Pokémon con ID " + pokemonId);
                        callback.accept(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al leer el Pokémon: " + e.getMessage());
                    callback.accept(null);
                });
    }

    public void getCapturedPokemonIds(FirebaseUser user, Consumer<ArrayList<String>> callback) {
        if (user == null) {
            Log.e(TAG, "Error: Usuario no autenticado");
            callback.accept(new ArrayList<>());
            return;
        }

        String userId = user.getUid();

        db.collection("users").document(userId).collection("captured_pokemons")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<String> pokemonIds = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String pokemonId = documentSnapshot.getId();
                        pokemonIds.add(pokemonId);
                    }
                    callback.accept(pokemonIds);  // Devuelve la lista de IDs
                    Log.d(TAG, "IDs de Pokémon obtenidos: " + pokemonIds.size());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener los IDs de los Pokémon: " + e.getMessage());
                    callback.accept(new ArrayList<>());
                });
    }
}