package com.example.beautyandcosmetics;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private DatabaseReference cartDatabaseRef;
    private TextView cartCountTextView;
    private DrawerLayout drawerLayout;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDatabaseRef;
    private FirebaseUser currentUser;

    private TextView fullNameTextView,emailTextView, phoneTextView, usernameTextView;

    private ImageView edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);




        cartDatabaseRef = FirebaseDatabase.getInstance().getReference().child("cart");

        // Assume you have a TextView with the id cartCountTextView
        cartCountTextView = findViewById(R.id.cartCountTextView);

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        // Reference to the current user's data
        userDatabaseRef = firebaseDatabase.getReference("users").child(currentUser.getUid());

        // Initialize views
        fullNameTextView = findViewById(R.id.User);
        emailTextView = findViewById(R.id.Email);
        phoneTextView = findViewById(R.id.Phone);
        usernameTextView = findViewById(R.id.Username);

        // Load user data
        loadUserData();

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.nav_shop) {
                    Intent intent = new Intent(ProfileActivity.this, ShopActivity.class);
                    startActivity(intent);
                    finish();

                }

                if (id == R.id.nav_about_us) {
                    Intent intent = new Intent(ProfileActivity.this, AboutActivity2.class);
                    startActivity(intent);
                    finish();

                }

                if (id == R.id.nav_profile) {
                    Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();

                }



                if (id == R.id.showMap) {
                    Intent intent = new Intent(ProfileActivity.this, MapsActivity.class);
                    startActivity(intent);
                    finish();


                }
                if (id == R.id.nav_like) {
                    Intent intent = new Intent(ProfileActivity.this, FavoriteActivity.class);
                    startActivity(intent);
                    finish();
                }

                if (id == R.id.nav_contact) {
                    Intent intent = new Intent(ProfileActivity.this, ContactActivity.class);
                    startActivity(intent);
                    finish();


                }

                if (id == R.id.logout) {
                    // Déconnexion de Firebase
                    FirebaseAuth.getInstance().signOut();

                    // Redirection vers l'écran de connexion
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
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
                Intent intent = new Intent(ProfileActivity.this, CartActivity.class);
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

        edit = findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog();
            }
        });
    }




    private void loadUserData() {
        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserInformation userInformation = snapshot.getValue(UserInformation.class);

                    if (userInformation != null) {
                        // Populate views with user data
                        fullNameTextView.setText(userInformation.getFullName());
                        emailTextView.setText(userInformation.getEmail());
                        phoneTextView.setText(userInformation.getPhone());
                        usernameTextView.setText(userInformation.getUsername());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void showEditDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_edit_profile, null);

        final EditText fullNameEditText = view.findViewById(R.id.editFullName);
        final EditText emailEditText = view.findViewById(R.id.editEmail);
        final EditText phoneEditText = view.findViewById(R.id.editPhone);
        final EditText usernameEditText = view.findViewById(R.id.editUsername);

        // Load current user data
        fullNameEditText.setText(fullNameTextView.getText());
        emailEditText.setText(emailTextView.getText());
        phoneEditText.setText(phoneTextView.getText());
        usernameEditText.setText(usernameTextView.getText());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view)
                .setTitle("Edit Profile")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Update user information
                        updateUserInformation(
                                fullNameEditText.getText().toString(),
                                emailEditText.getText().toString(),
                                phoneEditText.getText().toString(),
                                usernameEditText.getText().toString()
                        );
                    }
                })
                .setNegativeButton("Cancel", null)
                .create().show();
    }

    private void updateUserInformation(String fullName, String email, String phone, String username) {
        // Update views
        fullNameTextView.setText(fullName);
        emailTextView.setText(email);
        phoneTextView.setText(phone);
        usernameTextView.setText(username);

        // Update Firebase Realtime Database
        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", fullName);
        updates.put("email", email);
        updates.put("phone", phone);
        updates.put("username", username);

        userDatabaseRef.updateChildren(updates);

        // Update Firebase Authentication email
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.updateEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Modifiez votre code pour utiliser le Toast personnalisé
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.toast_layout, null);

// Modifiez le texte si nécessaire
                                TextView text = layout.findViewById(R.id.textViewMessage);
                                text.setText("Profile modified successfully ");

// Créez le Toast personnalisé
                                Toast toast = new Toast(getApplicationContext());
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.setView(layout);
                                toast.show();                            } else {
                                // Handle the error
                            }
                        }
                    });
        }
    }

}
