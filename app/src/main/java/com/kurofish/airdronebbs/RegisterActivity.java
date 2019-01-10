package com.kurofish.airdronebbs;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterTAG";

    private TextInputEditText rUserNameField;
    private TextInputEditText rEmailField;
    private TextInputEditText rPasswordField;
    private TextInputEditText rConfirmField;
    private Button rRegisterButton;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    private boolean isAllInfoValid = false;

    private View focusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        rUserNameField = findViewById(R.id.rUserNameEditText);
        rEmailField = findViewById(R.id.rEmailEditText);
        rPasswordField = findViewById(R.id.rPasswordEditText);
        rConfirmField = findViewById(R.id.rConfirmEditText);
        rRegisterButton = findViewById(R.id.rRegisterButton);
        progressBar = findViewById(R.id.rProgressBar);

        progressBar.setVisibility(View.INVISIBLE);

        rRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }


    private void signUp() {
        checkUserName();
        checkEmail();
        checkPassword();
        recheckPassword();
        Log.d(TAG, Boolean.toString(isAllInfoValid));

        if (!isAllInfoValid) {
            focusView.requestFocus();
            return;
        } else {
            progressBar.setVisibility(View.VISIBLE);

            String email = Objects.requireNonNull(rEmailField.getText()).toString();
            String password = Objects.requireNonNull(rPasswordField.getText()).toString();
            final String userName = Objects.requireNonNull(rUserNameField.getText().toString());
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(userName)
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User profile updated.");
                                                }
                                            }
                                        });
                                Toast.makeText(RegisterActivity.this, "Register succeed, will auto log in",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
        }
    }

    private void checkUserName() {
        if (rUserNameField.getText() == null) {
            Log.e(TAG, "Initial rUserNameField failed");
            isAllInfoValid = false;
            focusView = rUserNameField;
            return;
        } else {
            String userName = rUserNameField.getText().toString();

            if (TextUtils.isEmpty(userName)) {
                rUserNameField.setError(getString(R.string.error_field_required));
                isAllInfoValid = false;
                focusView = rUserNameField;
                return;
            }
            if (userName.length() < 4) {
                rUserNameField.setError("User name is too short");
                isAllInfoValid = false;
                focusView = rUserNameField;
                return;
            }
            isAllInfoValid = true;
        }
    }

    private void checkEmail() {
        if (rEmailField.getText() == null) {
            Log.e(TAG, "Initial rEmailField failed");
            isAllInfoValid = false;
            focusView = rEmailField;
            return;
        } else {
            String email = rEmailField.getText().toString();

            if (TextUtils.isEmpty(email)) {
                rEmailField.setError(getString(R.string.error_field_required));
                isAllInfoValid = false;
                focusView = rEmailField;
                return;
            }
            if (!email.contains("@")) {
                rEmailField.setError(getString(R.string.error_invalid_email));
                isAllInfoValid = false;
                focusView = rEmailField;
                return;
            }
            isAllInfoValid = true;
        }
    }

    private void checkPassword() {
        if (rPasswordField.getText() == null) {
            Log.e(TAG, "Initial rPasswordField failed");
            isAllInfoValid = false;
            focusView = rPasswordField;
            return;
        } else {
            String password = rPasswordField.getText().toString();

            if (TextUtils.isEmpty(password)) {
                rPasswordField.setError(getString(R.string.error_field_required));
                isAllInfoValid = false;
                focusView = rPasswordField;
                return;
            }
            if (password.length() < 6) {
                rPasswordField.setError(getString(R.string.error_invalid_password));
                isAllInfoValid = false;
                focusView = rPasswordField;
                return;
            }
            isAllInfoValid = true;
        }
    }

    private void recheckPassword() {
        if (rConfirmField.getText() == null) {
            Log.e(TAG, "Initial rConfirmField failed");
            isAllInfoValid = false;
            focusView = rConfirmField;
            return;
        } else {
            String confirm = rConfirmField.getText().toString();
            String password = rPasswordField.getText().toString();

            if (TextUtils.isEmpty(confirm)) {
                rConfirmField.setError(getString(R.string.error_field_required));
                isAllInfoValid = false;
                focusView = rConfirmField;
                return;
            }
            if (!confirm.equals(password)) {
                rConfirmField.setError("Confirm failed");
                isAllInfoValid = false;
                focusView = rConfirmField;
                return;
            }
            isAllInfoValid = true;
        }
    }
}
