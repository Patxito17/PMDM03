package com.gortmol.tupokedex.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.gortmol.tupokedex.model.Pokemon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

    public void loadSettings(Context context, FirebaseUser user) {
        if (user == null) {
            Log.e(TAG, "Error: Usuario no autenticado");
            return;
        }

        db.collection("users").document(user.getUid())
                .collection("user_settings").document("app_settings")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        SharedPreferences sharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        Map<String, Object> settings = documentSnapshot.getData();
                        if (settings != null) {
                            for (Map.Entry<String, Object> entry : settings.entrySet()) {
                                if (entry.getValue() instanceof Boolean) {
                                    editor.putBoolean(entry.getKey(), (Boolean) entry.getValue());
                                } else if (entry.getValue() instanceof String) {
                                    editor.putString(entry.getKey(), (String) entry.getValue());
                                } else if (entry.getValue() instanceof Integer) {
                                    editor.putInt(entry.getKey(), (Integer) entry.getValue());
                                } else if (entry.getValue() instanceof Float) {
                                    editor.putFloat(entry.getKey(), (Float) entry.getValue());
                                } else if (entry.getValue() instanceof Long) {
                                    editor.putLong(entry.getKey(), (Long) entry.getValue());
                                }
                            }
                            editor.apply();
                            Log.d(TAG, "Ajustes cargados y guardados en SharedPreferences.");
                        }
                    } else {
                        Log.d(TAG, "No se encontraron ajustes para este usuario.");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error al cargar los ajustes: ", e));
    }

    public void listenToSharedPreferences(Context context, FirebaseUser user) {
        if (user == null) {
            Log.e(TAG, "Error: Usuario no autenticado");
            return;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);

        SharedPreferences.OnSharedPreferenceChangeListener listener = (sharedPrefs, key) -> {
            Map<String, Object> update = new HashMap<>();
            Object value = sharedPrefs.getAll().get(key);
            update.put(key, value);

            db.collection("users").document(user.getUid())
                    .collection("user_settings").document("app_settings")
                    .set(update, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Ajuste sincronizado con Firestore: " + key))
                    .addOnFailureListener(e -> Log.e(TAG, "Error al sincronizar ajuste con Firestore: ", e));
        };

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);

        Log.d(TAG, "Listener de SharedPreferences registrado.");
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