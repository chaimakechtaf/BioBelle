package com.example.beautyandcosmetics;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Navbar extends AppCompatActivity {

    private DatabaseReference cartDatabaseRef;
    private TextView cartCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navbar);

        cartDatabaseRef = FirebaseDatabase.getInstance().getReference().child("cart");

        // Assume you have a TextView with the id cartCountTextView
        cartCountTextView = findViewById(R.id.cartCountTextView);

        @SuppressLint("WrongViewCast") Button btn = findViewById(R.id.sidebar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Navbar.this, MainActivity.class);
                startActivity(i);
            }
        });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView img = findViewById(R.id.shopping_cart_icon);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Navbar.this, CartActivity.class);
                startActivity(i);
            }
        });

        // Listen for changes in the cart and update the count
        cartDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long cartCount = 0;

                // Loop through the children to count the items
                for (DataSnapshot child : snapshot.getChildren()) {
                    cartCount += child.getChildrenCount(); // Increment count for each item
                }

                // Update the TextView with the new count
                cartCountTextView.setText(String.valueOf(cartCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors if needed
            }
        });
    }
}

