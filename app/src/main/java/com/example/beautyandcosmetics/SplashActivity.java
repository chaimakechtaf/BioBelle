package com.example.beautyandcosmetics;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    // Durée d'affichage du logo en millisecondes (5 secondes)
    private static final int SPLASH_TIME_OUT = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Utilisation d'un Handler pour retarder le passage à l'écran suivant
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Vérifier si l'utilisateur est déjà connecté
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    // L'utilisateur est déjà connecté, redirigez-le vers l'écran principal
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                } else {
                    // L'utilisateur n'est pas connecté, redirigez-le vers l'écran de connexion
                    Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }

                finish(); // Ferme l'activité actuelle pour éviter de revenir à cet écran avec le bouton "Retour"
            }
        }, SPLASH_TIME_OUT);
    }
}
