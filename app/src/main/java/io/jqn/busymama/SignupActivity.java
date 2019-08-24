package io.jqn.busymama;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import timber.log.Timber;

public class SignupActivity extends AppCompatActivity {
    private EditText inputEmail;
    private EditText inputPassword;
    private Button signupButton;
    private ProgressBar progressBar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Get Firebase instance
        auth = FirebaseAuth.getInstance();

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);

        signupButton = findViewById(R.id.signup_button);

        progressBar = findViewById(R.id.progressBar);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Validate inputs
                if (TextUtils.isEmpty(email)) {
                    inputEmail.setError(getString(R.string.error_email));
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    inputEmail.setError(getString(R.string.error_invalid_email));
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    inputPassword.setError(getString(R.string.error_empty_password));
                    return;
                }

                if (password.length() < 8) {
                    inputPassword.setError(getString(R.string.error_password));
                    return;
                }

                inputEmail.setError(null);
                inputPassword.setError(null);

                progressBar.setVisibility(View.VISIBLE);

                // Create user
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        // if task fails give the user feedback
                        if(!task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(), Toast.LENGTH_LONG).show();
                        } else {
                            startActivity(new Intent(SignupActivity.this, DashboardActivity.class));
                            finish();
                        }
                    }
                });
            }
        });
    }
}
