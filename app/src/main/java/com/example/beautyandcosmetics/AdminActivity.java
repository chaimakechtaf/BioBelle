package com.example.beautyandcosmetics;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

public class AdminActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword, editTextUsername;
    private Button btnLogin;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);

        firebaseAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Vérifier les identifiants avant d'appeler signInWithEmailAndPassword
                if (email.equals("belegant204@gmail.com") && password.equals("BElegant204")) {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(AdminActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Connexion réussie
                                        Toast.makeText(AdminActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                                        // Sauvegarder l'ID utilisateur actuel dans CartItem
                                        String userId = firebaseAuth.getCurrentUser().getUid();
                                        CartItem.setCurrentUserId(userId);

                                        // Rediriger vers HomeActivity
                                        startActivity(new Intent(AdminActivity.this, Inscription.class));
                                        finish(); // Fermer MainActivity pour éviter de revenir en arrière
                                    } else {
                                        // Login failed
                                        Toast.makeText(AdminActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    // Identifiants incorrects
                    Toast.makeText(AdminActivity.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Ajoutez un écouteur au texte "Forget Password?"
        TextView forgetPasswordText = findViewById(R.id.Forget);
        forgetPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Affichez une boîte de dialogue pour la réinitialisation du mot de passe
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

        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredEmail = emailEditText.getText().toString().trim();
                // Vérifier si l'e-mail entré est celui attendu
                if (enteredEmail.equals("belegant204@gmail.com")) {
                    // E-mail correct, réinitialiser le mot de passe
                    sendPasswordResetEmail(enteredEmail);
                } else {
                    // Afficher un message d'erreur pour un e-mail incorrect
                    Toast.makeText(AdminActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                }
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
                            // Succès, l'e-mail de réinitialisation a été envoyé
                            Toast.makeText(AdminActivity.this, "Reset email sent", Toast.LENGTH_SHORT).show();
                        } else {
                            // Échec, afficher un message d'erreur
                            Toast.makeText(AdminActivity.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
