package com.example.beautyandcosmetics;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AboutActivity2 extends Activity {


    private DatabaseReference cartDatabaseRef;
    private TextView cartCountTextView;
    private DrawerLayout drawerLayout; // Déclarez DrawerLayout


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about2);

        cartDatabaseRef = FirebaseDatabase.getInstance().getReference().child("cart");

        // Assume you have a TextView with the id cartCountTextView
        cartCountTextView = findViewById(R.id.cartCountTextView);

        drawerLayout = findViewById(R.id.drawer_layout); // Initialisez DrawerLayout

        ImageView btn = findViewById(R.id.shopping_cart_icon);

        btn.setOnClickListener(
                new View.OnClickListener(){;
                    public void onClick (View btn) {
                        Intent i = new Intent(AboutActivity2.this, CartActivity.class);
                        startActivity(i);
                        finish();

                    }
                });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                // Vérifiez l'ID de l'élément de menu sélectionné
                int id = menuItem.getItemId();

                if (id == R.id.nav_shop) {
                    // L'élément "Home" a été sélectionné, redirigez vers la page "Home"
                    Intent intent = new Intent(AboutActivity2.this, ShopActivity.class);
                    startActivity(intent);
                    finish();

                }
                if (id == R.id.nav_contact) {
                    Intent intent = new Intent(AboutActivity2.this, ContactActivity.class);
                    startActivity(intent);
                    finish();


                }

                if (id == R.id.nav_profile) {
                    Intent intent = new Intent(AboutActivity2.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();

                }


                if (id == R.id.nav_home) {
                    // L'élément "Home" a été sélectionné, redirigez vers la page "Home"
                    Intent intent = new Intent(AboutActivity2.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }


                if (id == R.id.showMap) {
                    Intent intent = new Intent(AboutActivity2.this, MapsActivity.class);
                    startActivity(intent);
                    finish();

                }
                if (id == R.id.nav_like) {
                    Intent intent = new Intent(AboutActivity2.this, FavoriteActivity.class);
                    startActivity(intent);
                    finish();
                }

                if (id == R.id.logout) {
                    // Déconnexion de Firebase
                    FirebaseAuth.getInstance().signOut();

                    // Redirection vers l'écran de connexion
                    Intent intent = new Intent(AboutActivity2.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Fermez cette activité pour éviter de revenir en arrière
                }


                drawerLayout.closeDrawer(GravityCompat.START); // Fermez le menu après la sélection

                return true;
            }
        });

        ImageView shoppingCartButton = findViewById(R.id.shopping_cart_icon);
        shoppingCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutActivity2.this, CartActivity.class);
                startActivity(intent);
                finish();

            }
        });

        ImageView sidebarImage = findViewById(R.id.sidebar);
        sidebarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Listen for changes in the cart and update the counter
        cartDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Use a Map to store the count of each product for each user
                Map<String, Map<String, Integer>> userProductCountMap = new HashMap<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    CartItem product = child.getValue(CartItem.class);

                    if (product != null) {
                        String userId = product.getUserId();
                        String productName = product.getName();

                        // Get the map for the current user
                        Map<String, Integer> productCountMap = userProductCountMap.get(userId);
                        if (productCountMap == null) {
                            productCountMap = new HashMap<>();
                            userProductCountMap.put(userId, productCountMap);
                        }

                        // Increment the counter for this product and user
                        int count = productCountMap.getOrDefault(productName, 0);
                        productCountMap.put(productName, count + 1);
                    }
                }

                // Calculate the total number of distinct products for the current user
                int totalProductCount = 0;
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Map<String, Integer> productCountMap = userProductCountMap.get(currentUserId);
                if (productCountMap != null) {
                    totalProductCount = productCountMap.size();
                }

                // Update the TextView with the new total number of distinct products for the current user
                cartCountTextView.setText(String.valueOf(totalProductCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors if necessary
            }
        });


        ImageView face = findViewById(R.id.facebook);

        face.setOnClickListener(new View.OnClickListener(){;
                    public void onClick (View btn) {
                        String url ="https://www.facebook.com/profile.php?id=61554724320977";
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(i);
                    }
                });

        ImageView inst = findViewById(R.id.insta);

        inst.setOnClickListener(new View.OnClickListener(){;
            public void onClick (View btn) {
                String url ="https://www.instagram.com/biobelle20?igshid=NGVhN2U2NjQ0Yg==";
                Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(in);
            }
        });
    }
}