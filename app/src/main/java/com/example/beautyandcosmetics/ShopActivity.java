package com.example.beautyandcosmetics;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopActivity extends AppCompatActivity {

    private DatabaseReference cartDatabaseRef;
    private TextView cartCountTextView;
    private GridView gridView;
    private DrawerLayout drawerLayout;
    private DatabaseReference databaseReference;

    // Add a variable to store the complete list of products
    private List<ShopItem> allProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        cartDatabaseRef = FirebaseDatabase.getInstance().getReference().child("cart");
        cartCountTextView = findViewById(R.id.cartCountTextView);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("products");

        // Example data
        List<ShopItem> productList = new ArrayList<>();

        // Read data from Firebase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ShopItem product = snapshot.getValue(ShopItem.class);
                    productList.add(product);
                }

                // Save the complete list of products
                allProducts = new ArrayList<>(productList);

                // Update the GridView after retrieving data
                updateGridView(productList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

        // Initialize DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);

        ImageView btn = findViewById(R.id.shopping_cart_icon);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ShopActivity.this, CartActivity.class);
                startActivity(i);
                finish();
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.nav_about_us) {
                    Intent intent = new Intent(ShopActivity.this, AboutActivity2.class);
                    startActivity(intent);
                    finish();
                }

                if (id == R.id.nav_home) {
                    Intent intent = new Intent(ShopActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }


                if (id == R.id.showMap) {
                    Intent intent = new Intent(ShopActivity.this, MapsActivity.class);
                    startActivity(intent);
                    finish();
                }
                if (id == R.id.nav_profile) {
                    Intent intent = new Intent(ShopActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();

                }

                if (id == R.id.nav_contact) {
                    Intent intent = new Intent(ShopActivity.this, ContactActivity.class);
                    startActivity(intent);
                    finish();


                }
                if (id == R.id.nav_like) {
                    Intent intent = new Intent(ShopActivity.this, FavoriteActivity.class);
                    startActivity(intent);
                    finish();
                }

                if (id == R.id.logout) {
                    // Log out from Firebase
                    FirebaseAuth.getInstance().signOut();

                    // Redirect to the login screen
                    Intent intent = new Intent(ShopActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Close this activity to prevent going back
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

        // Initialize the search logic
        initSearch();
    }

    private void initSearch() {
        SearchView searchView = findViewById(R.id.searchEditText);

        // Listen for changes in the search field text
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Called when the user submits the search query (can be ignored)
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Called every time the search text changes
                // Update the UI based on the new query
                filterProducts(newText);
                return true;
            }
        });
    }

    private void filterProducts(String query) {
        // Create a new list to store the filtered products
        List<ShopItem> filteredProducts = new ArrayList<>();

        // Iterate through all products
        for (ShopItem product : allProducts) {
            // Check if the product name contains the search query (case-insensitive)
            if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                // Highlight the part of the name that matches the search query
                SpannableStringBuilder builder = new SpannableStringBuilder(product.getName());
                int start = product.getName().toLowerCase().indexOf(query.toLowerCase());
                int end = start + query.length();
                builder.setSpan(new BackgroundColorSpan(getResources().getColor(android.R.color.holo_red_light)),
                        start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                // Create a new product with the highlighted name
                ShopItem highlightedProduct = new ShopItem(builder.toString(), product.getPrice(), product.getImageUrl(), product.getId());
                filteredProducts.add(highlightedProduct);
            }
        }

        // Update the UI with the new filtered list
        updateGridView(filteredProducts);
    }

    private void updateGridView(List<ShopItem> productList) {
        // Create an adapter
        ShopAdapter adapter = new ShopAdapter(this, productList);

        // Configure the GridView with GridLayout
        gridView = findViewById(R.id.gridView);
        gridView.setAdapter(adapter);
        gridView.setNumColumns(2);

        // Add a click listener on the items of the GridView
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item from the adapter
                ShopItem selectedItem = (ShopItem) parent.getItemAtPosition(position);

                // Create an intent to display DisplayActivity with the data of the selected item
                Intent intent = new Intent(ShopActivity.this, DisplayActivity.class);
                intent.putExtra("productName", selectedItem.getName());
                intent.putExtra("productPrice", selectedItem.getPrice());
                intent.putExtra("productImage", selectedItem.getImageUrl()); // Use the image URL here
                intent.putExtra("productId", selectedItem.getId()); // Add this line

                startActivity(intent);
            }
        });

        // Load images into ImageView using Picasso
        for (int i = 0; i < gridView.getChildCount(); i++) {
            ImageView imageView = gridView.getChildAt(i).findViewById(R.id.imageView); // Assuming you have an ImageView in your grid item layout
            Picasso.get().load(productList.get(i).getImageUrl()).into(imageView);
        }
    }
}
