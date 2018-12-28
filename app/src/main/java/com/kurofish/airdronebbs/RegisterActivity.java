package com.kurofish.airdronebbs;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "Register";

    private TextInputEditText rUserNameField;
    private TextInputEditText rEmailField;
    private TextInputEditText rPasswordField;
    private TextInputEditText rConfirmField;
    private Button rRegisterButton;

    private FirebaseAuth mAuth;

    private boolean isAllInfoVaild = false;

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

        if (!isAllInfoVaild) {
            return;
        } else {
            String email = Objects.requireNonNull(rEmailField.getText()).toString();
            String password = Objects.requireNonNull(rPasswordField.getText()).toString();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void checkUserName() {
        if (rUserNameField.getText() == null) {
            Log.e(TAG, "Initial rUserNameField failed");
            isAllInfoVaild = false;
            return;
        } else {
            String userName = rUserNameField.getText().toString();

            if (TextUtils.isEmpty(userName)) {
                rUserNameField.setError(getString(R.string.error_field_required));
                isAllInfoVaild = false;
                return;
            }
            if (userName.length() < 4) {
                rUserNameField.setError("User name is too short");
                isAllInfoVaild = false;
                return;
            }
            isAllInfoVaild = true;
        }
    }

    private void checkEmail() {
        if (rEmailField.getText() == null) {
            Log.e(TAG, "Initial rEmailField failed");
            isAllInfoVaild = false;
            return;
        } else {
            String email = rEmailField.getText().toString();

            if (TextUtils.isEmpty(email)) {
                rEmailField.setError(getString(R.string.error_field_required));
                isAllInfoVaild = false;
                return;
            }
            if (!email.contains("@")) {
                rEmailField.setError(getString(R.string.error_invalid_email));
                isAllInfoVaild = false;
                return;
            }
            isAllInfoVaild = true;
        }
    }

    private void checkPassword() {
        if (rPasswordField.getText() == null) {
            Log.e(TAG, "Initial rPasswordField failed");
            isAllInfoVaild = false;
            return;
        } else {
            String password = rPasswordField.getText().toString();

            if (TextUtils.isEmpty(password)) {
                rPasswordField.setError(getString(R.string.error_field_required));
                isAllInfoVaild = false;
                return;
            }
            if (password.length() < 6) {
                rPasswordField.setError(getString(R.string.error_invalid_password));
                isAllInfoVaild = false;
                return;
            }
            isAllInfoVaild = true;
        }
    }

    private void recheckPassword() {
        if (rConfirmField.getText() == null) {
            Log.e(TAG, "Initial rConfirmField failed");
            isAllInfoVaild = false;
            return;
        } else {
            String confirm = rConfirmField.getText().toString();
            String password = rPasswordField.getText().toString();

            if (!confirm.equals(password)) {
                rConfirmField.setError("Confirm failed");
                isAllInfoVaild = false;
                return;
            }
            isAllInfoVaild = true;
        }
    }
}
