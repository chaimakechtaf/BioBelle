package com.example.beautyandcosmetics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private DatabaseReference productsDatabaseRef;

    private int currentPage = 0;
    private final long delay = 5000;
    private Handler handler = new Handler();
    private ViewPager2 viewPager;

    private HomeAdapter adapter;

    private DatabaseReference cartDatabaseRef;
    private TextView cartCountTextView;
    private DrawerLayout drawerLayout;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        adapter = new HomeAdapter(this);
        viewPager.setAdapter(adapter);
        handler.postDelayed(update, delay);

        productsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("products");
        cartDatabaseRef = FirebaseDatabase.getInstance().getReference().child("cart");

        cartCountTextView = findViewById(R.id.cartCountTextView);
        drawerLayout = findViewById(R.id.drawer_layout);
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.nav_shop) {
                    Intent intent = new Intent(MainActivity.this, ShopActivity.class);
                    startActivity(intent);

                }

                if (id == R.id.nav_about_us) {
                    Intent intent = new Intent(MainActivity.this, AboutActivity2.class);
                    startActivity(intent);

                }

                if (id == R.id.nav_profile) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);

                }
                if (id == R.id.nav_like) {
                    Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                    startActivity(intent);

                }

                if (id == R.id.nav_home) {
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(intent);
                }

                if (id == R.id.showMap) {
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(intent);


                }

                if (id == R.id.nav_contact) {
                    Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                    startActivity(intent);


                }

                if (id == R.id.logout) {
                    // Déconnexion de Firebase
                    FirebaseAuth.getInstance().signOut();

                    // Redirection vers l'écran de connexion
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Fermez cette activité pour éviter de revenir en arrière
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        ImageView shoppingCartButton = findViewById(R.id.shopping_cart_icon);
        shoppingCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        loadProductsByCategory("hair", R.id.hair);
        loadProductsByCategory("oil", R.id.oils);
        loadProductsByCategory("soap", R.id.soap);
        loadProductsByCategory("box", R.id.box);

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
                Map<String, Map<String, Integer>> userProductCountMap = new HashMap<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    String productKey = child.getKey();
                    CartItem product = child.getValue(CartItem.class);

                    if (product != null) {
                        String userId = product.getUserId();
                        String productName = product.getName();

                        Map<String, Integer> productCountMap = userProductCountMap.get(userId);
                        if (productCountMap == null) {
                            productCountMap = new HashMap<>();
                            userProductCountMap.put(userId, productCountMap);
                        }

                        int count = productCountMap.getOrDefault(productName, 0);
                        productCountMap.put(productName, count + 1);
                    }
                }

                int totalProductCount = 0;
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Map<String, Integer> productCountMap = userProductCountMap.get(currentUserId);
                if (productCountMap != null) {
                    totalProductCount = productCountMap.size();
                }

                cartCountTextView.setText(String.valueOf(totalProductCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors if necessary
            }
        });

        LinearLayout click = findViewById(R.id.click);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ShopActivity.class);
                startActivity(intent);
            }
        });    }

    private final Runnable update = new Runnable() {
        public void run() {
            if (currentPage == adapter.getItemCount() - 1) {
                currentPage = 0;
            } else {
                currentPage++;
            }
            viewPager.setCurrentItem(currentPage);
            handler.postDelayed(this, delay);
        }
    };

    private void loadProductsByCategory(final String categoryKeyword, final int linearLayoutId) {
        productsDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Product> productList = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Product product = child.getValue(Product.class);

                    if (product != null && product.getName().toLowerCase().contains(categoryKeyword)) {
                        productList.add(product);
                    }
                }

                // Display horizontal list of products
                displayHorizontalProductList(productList, linearLayoutId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MainActivity", "Error loading products: " + error.getMessage());
                // Handle errors if necessary
            }
        });
    }

    private void displayHorizontalProductList(List<Product> productList, int linearLayoutId) {
        LinearLayout linearLayout = findViewById(linearLayoutId);
        linearLayout.removeAllViews();

        // Inflate horizontal RecyclerView
        View horizontalListView = LayoutInflater.from(this).inflate(R.layout.horizontal_product_list, linearLayout, false);
        RecyclerView recyclerView = horizontalListView.findViewById(R.id.horizontalRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Create and set adapter
        RecycleAdapter productAdapter = new RecycleAdapter(productList);
        recyclerView.setAdapter(productAdapter);

        linearLayout.addView(horizontalListView);
    }
}