package com.gortmol.tupokedex.data;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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

    public void getPokemonById(String pokemonId, FirebaseUser user, Consumer<Pokemon> callback) {
        if (user == null) {
            Log.e(TAG, "Error: Usuario no autenticado");
            callback.accept(null);
            return;
        }

        db.collection("users").document(user.getUid())
                .collection("captured_pokemons").document(pokemonId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Pokemon pokemon = documentSnapshot.toObject(Pokemon.class);
                        if (pokemon != null) {
                            pokemon.setImageUrl();
                            callback.accept(pokemon);
                        } else {
                            Log.e(TAG, "Error: No se pudo convertir el documento en Pokemon");
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

        db.collection("users").document(user.getUid())
                .collection("captured_pokemons")
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

    public void getPokemonCapturedList(FirebaseUser user, Consumer<ArrayList<Pokemon>> callback) {
        if (user == null) {
            Log.e(TAG, "Error: Usuario no autenticado");
            callback.accept(new ArrayList<>());
            return;
        }

        db.collection("users").document(user.getUid())
                .collection("captured_pokemons")
                // .orderBy("id", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Pokemon> pokemonCapturedList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Pokemon pokemon = documentSnapshot.toObject(Pokemon.class);
                        pokemon.setImageUrl();
                        pokemonCapturedList.add(pokemon);
                    }
                    callback.accept(pokemonCapturedList);
                    Log.d(TAG, "Pokémons capturados obtenidos: " + pokemonCapturedList.size());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener los Pokémon capturados: " + e.getMessage());
                    callback.accept(new ArrayList<>());
                });
    }

    public void listenToCapturedPokemonIds(FirebaseUser user, FirestoreCallback<ArrayList<String>> callback) {
        if (user == null) {
            Log.e(TAG, "Error: Usuario no autenticado");
            return;
        }

        db.collection("users").document(user.getUid())
                .collection("captured_pokemon")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("FirestoreHelper", "Error al escuchar IDs de Pokémon capturados: ", e);
                        return;
                    }
                    if (snapshots != null) {
                        ArrayList<String> capturedIds = new ArrayList<>();
                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            capturedIds.add(document.getId());
                        }
                        callback.onComplete(capturedIds);
                    }
                });
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

    public void containsPokemon(String pokemonId, FirebaseUser user, Consumer<Boolean> callback) {
        if (user == null) {
            Log.e(TAG, "Error: Usuario no autenticado");
            return;
        }

        db.collection("users").document(user.getUid())
                .collection("captured_pokemons").document(pokemonId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    callback.accept(documentSnapshot.exists());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al verificar si el Pokémon está capturado: " + e.getMessage());
                    callback.accept(false);
                });
    }

    public void listenToCapturedPokemonIds(FirebaseUser user, OnCapturedIdsFetched callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid())
                .collection("captured_pokemons")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error al escuchar cambios en los Pokémon capturados: ", e);
                        return;
                    }
                    ArrayList<String> capturedIds = new ArrayList<>();
                    if (snapshot != null && !snapshot.isEmpty()) {
                        for (DocumentSnapshot document : snapshot.getDocuments()) {
                            capturedIds.add(document.getId());
                        }
                        callback.onCapturedIdsFetched(capturedIds);
                    }
                });
    }

    public interface FirestoreCallback<T> {
        void onComplete(T result);
    }

    public interface OnCapturedIdsFetched {
        void onCapturedIdsFetched(ArrayList<String> capturedIds);
    }
}