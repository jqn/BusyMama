package io.jqn.busymama;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import timber.log.Timber;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputEditText mEmailField;
    private TextInputEditText mPasswordField;
    private TextInputLayout mEmailInput;
    private TextInputLayout mPasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mEmailInput = findViewById(R.id.user_text_input);
        mEmailField = findViewById(R.id.user_edit_text);
        mPasswordInput = findViewById(R.id.password_text_input);
        mPasswordField = findViewById(R.id.password_edit_text);

        MaterialButton nextButton = findViewById(R.id.next_button);
        MaterialButton registerButton = findViewById(R.id.register_button);


        // Set an error if the password is less than  8 characters.
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateForm()) {
                    return;
                }

                signIn(mEmailField.getText().toString().trim(), mPasswordField.getText().toString().trim());

            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        Timber.d("get Current User onStart %s", user);
        // Initialize Firebase Auth
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        }

    }

    // Check password is valid
    private boolean isPasswordValid(@Nullable Editable text) {
        // Implement firebase here
        return text != null && text.length() >= 8;
    }

    private void signIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Timber.d("navigate to dashboard");
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(intent);
                        } else {
                            Timber.d("failure %s", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError(getString(R.string.error_email));
            valid = false;
        } else {
            mEmailInput.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError(getString(R.string.error_empty_password));
            valid = false;
        } else {
            mPasswordInput.setError(null);
        }


        return valid;
    }
}
