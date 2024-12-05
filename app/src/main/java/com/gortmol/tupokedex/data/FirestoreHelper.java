package com.gortmol.tupokedex.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.gortmol.tupokedex.fragments.SettingsFragment;
import com.gortmol.tupokedex.model.Pokemon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class FirestoreHelper {

    private static final String TAG = "FirestoreHelper";
    private static final String USERS_COLLECTION = "users";
    private static final String CAPTURED_POKEMONS_COLLECTION = "captured_pokemons";
    private static final String USER_SETTINGS_COLLECTION = "user_settings";
    private static final String APP_SETTINGS_DOCUMENT = "app_settings";

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

        db.collection(USERS_COLLECTION).document(user.getUid())
                .collection(CAPTURED_POKEMONS_COLLECTION).document(String.valueOf(pokemon.getId()))
                .set(pokemon)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Pokemon añadido con éxito: " + pokemon.getName()))
                .addOnFailureListener(e -> Log.e(TAG, "Error al añadir el Pokemon: " + e.getMessage()));
    }

    public void deletePokemon(Pokemon pokemon, FirebaseUser user) {
        if (user == null) {
            Log.e(TAG, "Error: Usuario no autenticado");
            return;
        }

        db.collection(USERS_COLLECTION).document(user.getUid())
                .collection(CAPTURED_POKEMONS_COLLECTION).document(String.valueOf(pokemon.getId()))
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Pokemon eliminado: " + pokemon.getName()))
                .addOnFailureListener(e -> Log.e(TAG, "Error al eliminar el Pokemon: " + e.getMessage()));
    }

    public void setDefaultSettingsIfNotExist(FirebaseUser user) {
        if (user == null) {
            Log.e(TAG, "Error: Usuario no autenticado");
            return;
        }

        db.collection(USERS_COLLECTION).document(user.getUid())
                .collection(USER_SETTINGS_COLLECTION).document(APP_SETTINGS_DOCUMENT)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "El usuario ya tiene ajustes configurados. No se aplicarán los valores por defecto.");
                    } else {
                        Map<String, Object> defaultSettings = getStringObjectMap();
                        db.collection(USERS_COLLECTION).document(user.getUid())
                                .collection(USER_SETTINGS_COLLECTION).document(APP_SETTINGS_DOCUMENT)
                                .set(defaultSettings)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Documento de configuración creado con valores por defecto."))
                                .addOnFailureListener(e -> Log.e(TAG, "Error al crear el documento de configuración: ", e));
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error al verificar la existencia del documento: ", e));
    }

    @NonNull
    private static Map<String, Object> getStringObjectMap() {
        Map<String, Object> defaultSettings = new HashMap<>();
        String language = Locale.getDefault().getLanguage().equals("es") ? "es" : "en";
        defaultSettings.put(SettingsFragment.PREF_LANGUAGE, language);
        defaultSettings.put(SettingsFragment.PREF_POKEMON_GENERATION, "0–151");
        defaultSettings.put(SettingsFragment.PREF_DELETE_POKEMON, false);
        defaultSettings.put(SettingsFragment.PREF_POKEMONS_ORDER_BY, "id");
        defaultSettings.put(SettingsFragment.PREF_POKEMONS_ORDER_ASC_DESC, "asc");
        return defaultSettings;
    }

    public void downloadUserSettings(FirebaseUser user, Consumer<Map<String, Object>> callback) {
        if (user == null) {
            Log.e(TAG, "Error: Usuario no autenticado");
            return;
        }

        db.collection(USERS_COLLECTION).document(user.getUid())
                .collection(USER_SETTINGS_COLLECTION).document(APP_SETTINGS_DOCUMENT)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> settings = documentSnapshot.getData();
                        if (settings != null) {
                            callback.accept(settings);
                            Log.d(TAG, "Ajustes del usuario cargados: " + settings);
                        }
                    } else {
                        setDefaultSettingsIfNotExist(FirebaseAuth.getInstance().getCurrentUser());
                        Log.d(TAG, "No se encontraron ajustes para el usuario. Se han establecido valores por defecto.");
                        callback.accept(getStringObjectMap());
                    }
                });
    }

    public void getUserSetting(FirebaseUser user, String key, Consumer<Object> callback) {
        if (user == null) {
            Log.e(TAG, "Error: Usuario no autenticado");
            return;
        }

        db.collection(USERS_COLLECTION).document(user.getUid())
                .collection(USER_SETTINGS_COLLECTION).document(APP_SETTINGS_DOCUMENT)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> settings = documentSnapshot.getData();
                        if (settings != null && settings.containsKey(key)) {
                            callback.accept(settings.get(key));
                            Log.d(TAG, "Ajuste cargado: " + key + " = " + settings.get(key));
                        } else {
                            Log.d(TAG, "La clave especificada no se encontró en los ajustes: " + key);
                            callback.accept(null);
                        }
                    } else {
                        setDefaultSettingsIfNotExist(FirebaseAuth.getInstance().getCurrentUser());
                        Log.d(TAG, "No se encontraron ajustes para el usuario. Se han establecido valores por defecto.");
                        callback.accept(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al cargar los ajustes: ", e);
                    callback.accept(null);
                });
    }

    public void updateUserSetting(FirebaseUser user, String key, Object newValue) {
        if (user == null) {
            Log.e(TAG, "Error: Usuario no autenticado");
            return;
        }

        Map<String, Object> update = new HashMap<>();
        update.put(key, newValue);

        db.collection(USERS_COLLECTION).document(user.getUid())
                .collection(USER_SETTINGS_COLLECTION).document(APP_SETTINGS_DOCUMENT)
                .set(update, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Ajuste sincronizado con Firestore: " + key))
                .addOnFailureListener(e -> Log.e(TAG, "Error al sincronizar ajuste con Firestore: ", e));
    }

    public ListenerRegistration listenToCapturedPokemons(FirebaseUser user, String orderType, String orderDirection, Consumer<ArrayList<Pokemon>> callback) {
        if (user == null) {
            Log.e(TAG, "Error: Usuario no autenticado");
            callback.accept(new ArrayList<>());
            return null;
        }

        Query.Direction direction = orderDirection.equals("asc") ? Query.Direction.ASCENDING : Query.Direction.DESCENDING;
        return db.collection(USERS_COLLECTION).document(user.getUid())
                .collection(CAPTURED_POKEMONS_COLLECTION).orderBy(orderType, direction)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error al escuchar los Pokémon capturados: ", e);
                        return;
                    }
                    if (snapshots != null) {
                        ArrayList<Pokemon> capturedPokemonList = new ArrayList<>();
                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            Pokemon pokemon = document.toObject(Pokemon.class);
                            Objects.requireNonNull(pokemon).setImageUrl();
                            capturedPokemonList.add(pokemon);
                        }
                        callback.accept(capturedPokemonList);
                        Log.d(TAG, "Listener de Pokémon capturados actualizado: " + capturedPokemonList.size() + " Pokémon");
                    }
                });
    }

    public ListenerRegistration listenToCapturedPokemonIds(FirebaseUser user, Consumer<ArrayList<String>> callback) {
        if (user == null) {
            Log.e(TAG, "Error: Usuario no autenticado");
            return null;
        }

        return db.collection(USERS_COLLECTION).document(user.getUid())
                .collection(CAPTURED_POKEMONS_COLLECTION)
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
                        Log.d(TAG, "Listener de Pokémon (IDs) capturados actualizado: " + capturedPokemonIdList.size() + " Pokémon");
                    }
                });
    }
}