package com.kurofish.airdronebbs;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "Register";

    private TextInputEditText rUserNameField;
    private TextInputEditText rEmailField;
    private TextInputEditText rPasswordField;
    private TextInputEditText rConfirmField;
    private Button rRegisterButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

        mAuth = FirebaseAuth.getInstance();
    }


    private void signUp()
    {

    }
}
