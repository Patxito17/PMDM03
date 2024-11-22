package com.gortmol.tupokedex;


import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.gortmol.tupokedex.databinding.ActivityLoginBinding;

import java.util.Collections;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private FirebaseAuth auth;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            goToMainActivity();
        } else {
            setupLoginButton();
            setupRegisterEmailPasswordButton();
            setupRegisterGoogleButton();
        }
    }

    private void setupLoginButton() {
        binding.login.setOnClickListener(view -> {
            String email = binding.email.getText().toString().trim();
            String password = binding.password.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Snackbar.make(binding.getRoot(), R.string.empty_fields, Snackbar.LENGTH_SHORT).show();
                return;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Snackbar.make(binding.getRoot(), R.string.invalid_email, Snackbar.LENGTH_SHORT).show();
                return;
            } else if (password.length() < 6) {
                Snackbar.make(binding.getRoot(), R.string.password_length, Snackbar.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            goToMainActivity();
                        } else {
                            Snackbar.make(binding.getRoot(), R.string.sign_in_failed, Snackbar.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void setupRegisterEmailPasswordButton() {
        binding.registerEmailPassword.setOnClickListener(view -> {
            Intent signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setTheme(R.style.Theme_TuPokedex)
                    .build();
            signInLauncher.launch(signInIntent);
        });
    }

    private void setupRegisterGoogleButton() {
        binding.registerGoogle.setOnClickListener(view -> {
            Intent signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(Collections.singletonList(new AuthUI.IdpConfig.GoogleBuilder().build()))
                    .setTheme(R.style.Theme_TuPokedex)
                    .build();
            signInLauncher.launch(signInIntent);
        });
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            goToMainActivity();
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                Snackbar.make(binding.getRoot(), R.string.sign_in_cancelled, Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                Snackbar.make(binding.getRoot(), R.string.no_internet_connection, Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                Snackbar.make(binding.getRoot(), R.string.unknown_error, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}