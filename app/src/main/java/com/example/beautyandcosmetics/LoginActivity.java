package com.example.beautyandcosmetics;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button btnLogin;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);

        firebaseAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Check if the provided credentials match the admin credentials
                if (email.equals("belegant204@gmail.com") && password.equals("BElegant204")) {
                    // Admin login
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Admin login successful
                                        // Modifiez votre code pour utiliser le Toast personnalisé
                                        LayoutInflater inflater = getLayoutInflater();
                                        View layout = inflater.inflate(R.layout.toast_layout, null);

// Modifiez le texte si nécessaire
                                        TextView text = layout.findViewById(R.id.textViewMessage);
                                        text.setText("Admin login successful");

// Créez le Toast personnalisé
                                        Toast toast = new Toast(getApplicationContext());
                                        toast.setDuration(Toast.LENGTH_SHORT);
                                        toast.setView(layout);
                                        toast.show();
                                        // Perform any additional actions for admin login if needed

                                        // Redirect to Inscription (or any other admin-related activity)
                                        startActivity(new Intent(LoginActivity.this, Inscription.class));
                                        finish(); // Close LoginActivity to prevent going back
                                    } else {
                                        // Admin login failed
                                        // Modifiez votre code pour utiliser le Toast personnalisé
                                        LayoutInflater inflater = getLayoutInflater();
                                        View layout = inflater.inflate(R.layout.toast_layout, null);

// Modifiez le texte si nécessaire
                                        TextView text = layout.findViewById(R.id.textViewMessage);
                                        text.setText("Admin login failed!");

// Créez le Toast personnalisé
                                        Toast toast = new Toast(getApplicationContext());
                                        toast.setDuration(Toast.LENGTH_SHORT);
                                        toast.setView(layout);
                                        toast.show();                                    }
                                }
                            });
                } else {
                    // Regular user login
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Regular user login successful
                                        // Modifiez votre code pour utiliser le Toast personnalisé
                                        LayoutInflater inflater = getLayoutInflater();
                                        View layout = inflater.inflate(R.layout.toast_layout, null);

// Modifiez le texte si nécessaire
                                        TextView text = layout.findViewById(R.id.textViewMessage);
                                        text.setText("Login successful");

// Créez le Toast personnalisé
                                        Toast toast = new Toast(getApplicationContext());
                                        toast.setDuration(Toast.LENGTH_SHORT);
                                        toast.setView(layout);
                                        toast.show();
                                        // Perform any additional actions for regular user login if needed

                                        // Redirect to MainActivity (or any other user-related activity)
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish(); // Close LoginActivity to prevent going back
                                    } else {
                                        // Login failed
                                        // Modifiez votre code pour utiliser le Toast personnalisé
                                        LayoutInflater inflater = getLayoutInflater();
                                        View layout = inflater.inflate(R.layout.toast_layout, null);

// Modifiez le texte si nécessaire
                                        TextView text = layout.findViewById(R.id.textViewMessage);
                                        text.setText("Login failed!");

// Créez le Toast personnalisé
                                        Toast toast = new Toast(getApplicationContext());
                                        toast.setDuration(Toast.LENGTH_SHORT);
                                        toast.setView(layout);
                                        toast.show();                                    }
                                }
                            });
                }
            }
        });

        TextView SignUp = findViewById(R.id.SignUp);
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirect to RegisterActivity when the button is clicked
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });



        // Add a listener to the "Forget Password?" text
        TextView forgetPasswordText = findViewById(R.id.Forget);
        forgetPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Display a dialog for password reset
                showResetPasswordDialog();
            }
        });
    }

    private void showResetPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");
        builder.setMessage("Please enter your email address");

        final EditText emailEditText = new EditText(this);
        builder.setView(emailEditText);

        builder.setPositiveButton("send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = emailEditText.getText().toString().trim();
                sendPasswordResetEmail(email);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendPasswordResetEmail(String emailAddress) {
        firebaseAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Success, the reset email has been sent
                            // Modifiez votre code pour utiliser le Toast personnalisé
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_layout, null);

// Modifiez le texte si nécessaire
                            TextView text = layout.findViewById(R.id.textViewMessage);
                            text.setText("Reset email sent");

// Créez le Toast personnalisé
                            Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();                        } else {
                            // Failure, display an error message
                            // Modifiez votre code pour utiliser le Toast personnalisé
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_layout, null);

// Modifiez le texte si nécessaire
                            TextView text = layout.findViewById(R.id.textViewMessage);
                            text.setText("Failed to send reset email!");

// Créez le Toast personnalisé
                            Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();                        }
                    }
                });
    }
}
