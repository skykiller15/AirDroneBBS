package com.kurofish.airdronebbs.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kurofish.airdronebbs.MainActivity;
import com.kurofish.airdronebbs.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginTAG";

    private TextInputEditText lEmailField;
    private TextInputEditText lPasswordField;

    private Button lLoginButton;
    private Button lRegisterButton;

    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    private Boolean isAllInfoValid = false;
    private View focusView = lEmailField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lEmailField = findViewById(R.id.lEmailEditText);
        lPasswordField = findViewById(R.id.lPasswordEditText);
        lLoginButton = findViewById(R.id.lLoginButton);
        lRegisterButton = findViewById(R.id.lRegisterButton);
        progressBar = findViewById(R.id.lProgressBar);
        mAuth = FirebaseAuth.getInstance();

        progressBar.setVisibility(View.INVISIBLE);

        lLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        lRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    private void login() {
        checkEmail();
        checkPassword();
        Log.d(TAG, Boolean.toString(isAllInfoValid));

        if (!isAllInfoValid) {
            focusView.requestFocus();
            return;
        }

        String email = lEmailField.getText().toString();
        String password = lPasswordField.getText().toString();

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            lPasswordField.setError("Password not correct");
                            lPasswordField.requestFocus();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        // [END_EXCLUDE]
                    }
                });
    }

    private void checkEmail() {
        if (lEmailField.getText() == null) {
            Log.e(TAG, "Inital lEmailField failed");
            isAllInfoValid = false;
            focusView = lEmailField;
            return;
        } else {
            String email = lEmailField.getText().toString();

            if (TextUtils.isEmpty(email)) {
                lEmailField.setError(getString(R.string.error_field_required));
                isAllInfoValid = false;
                focusView = lEmailField;
                return;
            }
            if (!email.contains("@")) {
                lEmailField.setError(getString(R.string.error_invalid_email));
                isAllInfoValid = false;
                focusView = lEmailField;
                return;
            }
            isAllInfoValid = true;
        }
    }

    private void checkPassword() {
        if (lPasswordField.getText() == null) {
            Log.e(TAG, "Initial lPasswordField failed");
            isAllInfoValid = false;
            focusView = lPasswordField;
            return;
        } else {
            String password = lPasswordField.getText().toString();

            if (TextUtils.isEmpty(password)) {
                lPasswordField.setError(getString(R.string.error_field_required));
                isAllInfoValid = false;
                focusView = lPasswordField;
                return;
            }
            if (password.length() < 6) {
                lPasswordField.setError(getString(R.string.error_invalid_password));
                isAllInfoValid = false;
                focusView = lPasswordField;
                return;
            }
            isAllInfoValid = true;
        }
    }
}

