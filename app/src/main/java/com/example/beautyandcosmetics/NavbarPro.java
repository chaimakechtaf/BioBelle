package com.example.beautyandcosmetics;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beautyandcosmetics.R;

public class NavbarPro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navbar_pro);

        final ImageView home = findViewById(R.id.home);
        final ImageView products = findViewById(R.id.Products);
        final ImageView order = findViewById(R.id.order);

        // Set initial colors
        final int defaultColor = getResources().getColor(R.color.fond_bouton); // Use your default color
        home.setColorFilter(defaultColor);
        products.setColorFilter(defaultColor);
        order.setColorFilter(defaultColor);

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirect to OrderActivity
                Intent i = new Intent(NavbarPro.this, OrderActivity.class);
                startActivity(i);

                // Change the color of the clicked icon to black
                order.setColorFilter(Color.BLACK);
                // Reset other icons to default color
                home.setColorFilter(defaultColor);
                products.setColorFilter(defaultColor);
            }
        });

        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirect to ProductsActivity
                Intent intent = new Intent(NavbarPro.this, ProductsActivity.class);
                startActivity(intent);

                // Change the color of the clicked icon to black
                products.setColorFilter(Color.BLACK);
                // Reset other icons to default color
                home.setColorFilter(defaultColor);
                order.setColorFilter(defaultColor);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirect to Inscription
                Intent intent = new Intent(NavbarPro.this, Inscription.class);
                startActivity(intent);

                // Change the color of the clicked icon to black
                home.setColorFilter(Color.BLACK);
                // Reset other icons to default color
                products.setColorFilter(defaultColor);
                order.setColorFilter(defaultColor);
            }
        });
    }
}
