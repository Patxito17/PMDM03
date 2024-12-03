package com.gortmol.tupokedex;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.gortmol.tupokedex.data.FirestoreHelper;
import com.gortmol.tupokedex.fragments.SettingsFragment;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLanguageLikeSystem();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            goToMainActivity();
        } else if (savedInstanceState == null) {
            setSignInLauncher();
        }
    }

    private void setLanguageLikeSystem() {
        LocaleListCompat appLocales = LocaleListCompat.getEmptyLocaleList();
        AppCompatDelegate.setApplicationLocales(appLocales);
    }

    private void setSignInLauncher() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.ic_launcher)
                .setTheme(R.style.Theme_TuPokedex)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            Log.d(TAG, "Usuario autenticado: " + FirebaseAuth.getInstance().getCurrentUser().getEmail());
            goToMainActivity();
        }
    }

    private void goToMainActivity() {
        SharedPreferences sp = getSharedPreferences(SettingsFragment.PREF_NAME, MODE_PRIVATE);
        FirestoreHelper.getInstance().downloadUserSettings(FirebaseAuth.getInstance().getCurrentUser(), settings -> {
            for (String key : settings.keySet()) {
                if (key.equals(SettingsFragment.PREF_DELETE_POKEMON)) {
                    sp.edit().putBoolean(key, (boolean) settings.get(key)).apply();
                    Log.d(TAG, "Configuración cargada en SharedPreferences: " + key + " = " + settings.get(key));
                } else {
                    sp.edit().putString(key, String.valueOf(settings.get(key))).apply();
                    Log.d(TAG, "Configuración cargada en SharedPreferences: " + key + " = " + settings.get(key));
                }
            }
        });
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}