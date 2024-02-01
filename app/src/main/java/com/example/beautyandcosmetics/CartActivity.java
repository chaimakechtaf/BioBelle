package com.example.beautyandcosmetics;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private DatabaseReference cartDatabaseRef;
    private TextView cartCountTextView;
    private DrawerLayout drawerLayout;
    private TextView totalProductsTextView;
    private FirebaseAuth firebaseAuth;
    private List<CartItem> productList;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_activity);

        firebaseAuth = FirebaseAuth.getInstance();
        cartDatabaseRef = FirebaseDatabase.getInstance().getReference().child("cart");

        // Get the userId of the currently authenticated user
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            // Handle the case where there is no authenticated user
            // You might want to redirect to the login screen or take appropriate action
            return;
        }

        String currentUserId = currentUser.getUid();

        // Fetch items only for the current user
        Query userCartQuery = cartDatabaseRef.orderByChild("userId").equalTo(currentUserId);

        cartCountTextView = findViewById(R.id.cartCountTextView);

        ListView listView = findViewById(R.id.listView);
        productList = new ArrayList<>();
        adapter = new CartAdapter(this, productList);
        listView.setAdapter(adapter);

        totalProductsTextView = findViewById(R.id.Total_Prod);

        userCartQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();

                StringBuilder totalProductsBuilder = new StringBuilder("Total Products: ");

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CartItem product = snapshot.getValue(CartItem.class);
                    productList.add(product);

                    // Append product name and quantity to the StringBuilder
                    totalProductsBuilder.append(product.getName())
                            .append(" (")
                            .append(product.getQuantity())
                            .append("), ");
                }

                // Remove the trailing comma and space
                if (totalProductsBuilder.length() > 2) {
                    totalProductsBuilder.setLength(totalProductsBuilder.length() - 2);
                }

                adapter.notifyDataSetChanged();

                // Calculate and display total price
                TextView totalPriceTextView = findViewById(R.id.Total_Price);
                double totalPrice = calculateTotalPrice(productList);
                String totalPriceText = String.format(getString(R.string.total_price_format), totalPrice);
                totalPriceTextView.setText(totalPriceText);

                // Display total products (null check added)
                if (totalProductsTextView != null) {
                    totalProductsTextView.setText(totalProductsBuilder.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.nav_about_us) {
                    Intent intent = new Intent(CartActivity.this, AboutActivity2.class);
                    startActivity(intent);
                    finish();
                }

                if (id == R.id.nav_home) {
                    Intent intent = new Intent(CartActivity.this, MainActivity.class);
                    startActivity(intent);

                }
                if (id == R.id.nav_like) {
                    Intent intent = new Intent(CartActivity.this, FavoriteActivity.class);
                    startActivity(intent);
                    finish();
                }

                if (id == R.id.nav_profile) {
                    Intent intent = new Intent(CartActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();

                }
                if (id == R.id.nav_shop) {
                    Intent intent = new Intent(CartActivity.this, ShopActivity.class);
                    startActivity(intent);
                    finish();

                }

                if (id == R.id.showMap) {
                    Intent intent = new Intent(CartActivity.this, MapsActivity.class);
                    startActivity(intent);
                    finish();

                }
                if (id == R.id.nav_contact) {
                    Intent intent = new Intent(CartActivity.this, ContactActivity.class);
                    startActivity(intent);
                    finish();


                }

                if (id == R.id.logout) {
                    // Déconnexion de Firebase
                    FirebaseAuth.getInstance().signOut();

                    // Redirection vers l'écran de connexion
                    Intent intent = new Intent(CartActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Fermez cette activité pour éviter de revenir en arrière
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        Button btn = findViewById(R.id.checkout);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                Intent i = new Intent(CartActivity.this, DeliveryActivity.class);
                startActivity(i);
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

    }

    // Method to calculate the total price
    private double calculateTotalPrice(List<CartItem> productList) {
        double totalPrice = 0;

        for (CartItem product : productList) {
            String cleanPrice = product.getPrice().replaceAll("[^\\d.]+", "");
            if (!cleanPrice.isEmpty()) {
                totalPrice += Double.parseDouble(cleanPrice) * product.getQuantity();
            }
        }

        return totalPrice;
    }
}
