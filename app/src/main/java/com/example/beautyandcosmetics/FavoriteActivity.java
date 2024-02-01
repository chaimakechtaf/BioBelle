package com.example.beautyandcosmetics;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

public class FavoriteActivity extends AppCompatActivity {
    private DatabaseReference cartDatabaseRef;

    private DatabaseReference favoritesDatabaseRef;
    private TextView cartCountTextView;
    private DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;
    private List<FavoriteItem> productList;
    private FavoriteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        firebaseAuth = FirebaseAuth.getInstance();
        favoritesDatabaseRef = FirebaseDatabase.getInstance().getReference().child("favorites");




            cartDatabaseRef = FirebaseDatabase.getInstance().getReference().child("cart");

            // Assume you have a TextView with the id cartCountTextView
            cartCountTextView = findViewById(R.id.cartCountTextView);

            ListView listView = findViewById(R.id.listView);
            productList = new ArrayList<>();
            adapter = new FavoriteAdapter(this, productList);
            listView.setAdapter(adapter);


        // Dans votre activité d'origine (MainActivity ou ShopActivity, etc.)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Récupérez les informations du produit à partir de votre liste
                FavoriteItem clickedFavoriteItem = productList.get(position);

                // Créez un Intent pour lancer DisplayActivity
                Intent intent = new Intent(FavoriteActivity.this, DisplayActivity.class);

                // Ajoutez les informations du produit à l'Intent
                intent.putExtra("productName", clickedFavoriteItem.getName());
                intent.putExtra("productPrice", clickedFavoriteItem.getPrice());
                intent.putExtra("productImage", clickedFavoriteItem.getImageURL());
                intent.putExtra("productId", clickedFavoriteItem.getProductId()
                ); // Assurez-vous que votre FavoriteItem a un getId() ou un champ similaire

                // Lancez DisplayActivity avec l'Intent
                startActivity(intent);
            }
        });


        // Fetch data from Firebase
            fetchFavoriteItems();
        }

        private void fetchFavoriteItems() {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String currentUserId = currentUser.getUid();

                // Use a query to get favorites for the current user
                Query userFavoritesQuery = favoritesDatabaseRef.child(currentUserId);

                userFavoritesQuery.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            try {
                                FavoriteItem product = child.getValue(FavoriteItem.class);
                                if (product != null) {
                                    productList.add(product);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error converting data.", e);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Firebase Error: " + error.getMessage());
                        // Handle errors if necessary
                    }
                });
            }


            drawerLayout = findViewById(R.id.drawer_layout);

            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    int id = menuItem.getItemId();

                    if (id == R.id.nav_about_us) {
                        Intent intent = new Intent(FavoriteActivity.this, AboutActivity2.class);
                        startActivity(intent);
                        finish();
                    }

                    if (id == R.id.nav_home) {
                        Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                    if (id == R.id.nav_profile) {
                        Intent intent = new Intent(FavoriteActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    if (id == R.id.nav_shop) {
                        Intent intent = new Intent(FavoriteActivity.this, ShopActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    if (id == R.id.showMap) {
                        Intent intent = new Intent(FavoriteActivity.this, MapsActivity.class);
                        startActivity(intent);
                        finish();

                    }
                    if (id == R.id.nav_contact) {
                        Intent intent = new Intent(FavoriteActivity.this, ContactActivity.class);
                        startActivity(intent);
                        finish();

                    }

                    if (id == R.id.logout) {
                        // Déconnexion de Firebase
                        FirebaseAuth.getInstance().signOut();

                        // Redirection vers l'écran de connexion
                        Intent intent = new Intent(FavoriteActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Fermez cette activité pour éviter de revenir en arrière
                    }

                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
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
    }

