package com.example.beautyandcosmetics;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import java.util.HashMap;
import java.util.Map;

public class DisplayActivity extends AppCompatActivity {

    private DatabaseReference cartDatabaseRef;
    private DatabaseReference favoritesDatabaseRef;
    private TextView cartCountTextView;
    private int quantity = 1; // Initial quantity

    private TextView quantityTextView;
    private DrawerLayout drawerLayout;

    private String currentUserId;
    private String productName;
    private String productBrand;

    private String productId;
    private String productPrice;
    private String productImageURL;
    private boolean isLiked;

    private ImageView likeButton;
    private FavoriteItem favoriteItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_product);

        favoritesDatabaseRef = FirebaseDatabase.getInstance().getReference().child("favorites");
        cartDatabaseRef = FirebaseDatabase.getInstance().getReference().child("cart");

        cartCountTextView = findViewById(R.id.cartCountTextView);
        quantityTextView = findViewById(R.id.quantity);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialisation de l'état "aimé" depuis les préférences
        productName = getIntent().getStringExtra("productName");

        // Initialisation des préférences après productName
        SharedPreferences prefs = getSharedPreferences("LikeState", MODE_PRIVATE);
        isLiked = prefs.getBoolean(productName, false);

        ImageView moinsButton = findViewById(R.id.moins);
        ImageView plusButton = findViewById(R.id.plus);

        moinsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity > 1) {
                    quantity--;
                    quantityTextView.setText(String.valueOf(quantity));
                }
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity++;
                quantityTextView.setText(String.valueOf(quantity));
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        likeButton = findViewById(R.id.likeButton);

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleLikeState();
                updateLikeButtonColor(isLiked);
            }
        });



        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.nav_about_us) {
                    Intent intent = new Intent(DisplayActivity.this, AboutActivity2.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.nav_shop) {
                    Intent intent = new Intent(DisplayActivity.this, ShopActivity.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.nav_home) {
                    Intent intent = new Intent(DisplayActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (id == R.id.showMap) {
                    Intent intent = new Intent(DisplayActivity.this, MapsActivity.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.nav_contact) {
                    Intent intent = new Intent(DisplayActivity.this, ContactActivity.class);
                    startActivity(intent);
                    finish();

                } else if (id == R.id.nav_like) {
                    Intent intent = new Intent(DisplayActivity.this, FavoriteActivity.class);
                    startActivity(intent);
                    finish();

                } else if (id == R.id.logout) {
                    // Déconnexion de Firebase
                    FirebaseAuth.getInstance().signOut();

                    // Redirection vers l'écran de connexion
                    Intent intent = new Intent(DisplayActivity.this, LoginActivity.class);
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

        ImageView shoppingCartButton = findViewById(R.id.shopping_cart_icon);
        shoppingCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DisplayActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Intent intent = getIntent();
        productName = intent.getStringExtra("productName");
        productBrand = intent.getStringExtra("productBrand");
        productPrice = intent.getStringExtra("productPrice");
        productImageURL = intent.getStringExtra("productImage");

        TextView productNameTextView = findViewById(R.id.productName);
        TextView productPriceTextView = findViewById(R.id.productPrice);
        ImageView productImageView = findViewById(R.id.productImage);

        productNameTextView.setText(productName);
        productPriceTextView.setText(productPrice);

        Picasso.get().load(productImageURL).into(productImageView);

        cartDatabaseRef = FirebaseDatabase.getInstance().getReference().child("cart");





        Button addToCartButton = findViewById(R.id.addToCartButton);
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Votre code existant pour créer un nouvel élément de panier
                String cartItemId = cartDatabaseRef.push().getKey();
                CartItem cartItem = new CartItem(currentUserId, productName, productBrand, productPrice, productImageURL, quantity);

                // Vérifier si le produit existe déjà dans le panier
                cartDatabaseRef.orderByChild("productId").equalTo(productId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Le produit existe déjà dans le panier
                            // Modifiez votre code pour utiliser le Toast personnalisé
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_layout, null);

// Modifiez le texte si nécessaire
                            TextView text = layout.findViewById(R.id.textViewMessage);
                            text.setText("Product already in cart !");

// Créez le Toast personnalisé
                            Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();                        } else {
                            // Vérifier si le produit existe déjà dans le panier en utilisant son productId
                            cartDatabaseRef.orderByChild("productId").equalTo(cartItem.getProductId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // Le produit existe déjà dans le panier
                                        // Modifiez votre code pour utiliser le Toast personnalisé
                                        LayoutInflater inflater = getLayoutInflater();
                                        View layout = inflater.inflate(R.layout.toast_layout, null);

// Modifiez le texte si nécessaire
                                        TextView text = layout.findViewById(R.id.textViewMessage);
                                        text.setText("Product already in cart !");

// Créez le Toast personnalisé
                                        Toast toast = new Toast(getApplicationContext());
                                        toast.setDuration(Toast.LENGTH_SHORT);
                                        toast.setView(layout);
                                        toast.show();                                    } else {
                                        // Le produit n'existe pas encore dans le panier, ajoutez-le
                                        cartDatabaseRef.child(cartItemId).setValue(cartItem);
                                        // Modifiez votre code pour utiliser le Toast personnalisé
                                        LayoutInflater inflater = getLayoutInflater();
                                        View layout = inflater.inflate(R.layout.toast_layout, null);

// Modifiez le texte si nécessaire
                                        TextView text = layout.findViewById(R.id.textViewMessage);
                                        text.setText("Product added to cart");

// Créez le Toast personnalisé
                                        Toast toast = new Toast(getApplicationContext());
                                        toast.setDuration(Toast.LENGTH_SHORT);
                                        toast.setView(layout);
                                        toast.show();                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d("TAG", "Database Error: " + databaseError.getMessage());
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("TAG", "Database Error: " + databaseError.getMessage());
                    }
                });
            }
        });





        cartDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Map<String, Integer>> userProductCountMap = new HashMap<>();

                for (DataSnapshot child : snapshot.getChildren()) {
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



        // Initialisation de l'état "aimé" depuis les préférences
        isLiked = loadLikeState();
        updateLikeButtonColor(isLiked);

        // Vérifier si le produit est dans les favoris
        checkIfProductIsFavorite();


    }


    private void checkIfProductIsFavorite() {
        favoritesDatabaseRef.child(currentUserId)
                .orderByChild("name")
                .equalTo(productName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // If the product is in favorites, initialize the FavoriteItem object
                            favoriteItem = snapshot.getChildren().iterator().next().getValue(FavoriteItem.class);
                            updateLikeButtonColor(true);
                        } else {
                            // If the product is not in favorites, make sure the icon is not colored
                            updateLikeButtonColor(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle errors if necessary
                    }
                });
    }

    private void updateLikeButtonColor(boolean isLiked) {
        if (isLiked) {
            likeButton.setColorFilter(getResources().getColor(R.color.redColor), PorterDuff.Mode.SRC_IN);
        } else {
            likeButton.setColorFilter(null);
        }
    }


    private void toggleLikeState() {
        if (isProductLiked()) {
            removeFromFavorites();
        } else {
            addToFavorites();
        }
    }

    private boolean isProductLiked() {
        return favoriteItem != null && favoriteItem.isLiked();
    }

    private void addToFavorites() {
        // Vérifier si le produit est déjà dans les favoris
        favoritesDatabaseRef.child(currentUserId)
                .orderByChild("name")
                .equalTo(productName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (favoriteItem != null) {
                            // Le produit est déjà dans les favoris, pas besoin d'afficher de message
                            removeFromFavorites();
                        } else {
                            // Ajouter le produit aux favoris
                            String favoriteItemId = favoritesDatabaseRef.child(currentUserId).push().getKey();
                            favoriteItem = new FavoriteItem(currentUserId, productName, productPrice, productImageURL, true);

                            // Utiliser le productId comme clé unique
                            favoriteItem.setName(productName);

                            favoritesDatabaseRef.child(currentUserId).child(favoriteItemId).setValue(favoriteItem);

                            // Mettez à jour l'icône et l'état "aimé"
                            updateLikeButtonColor(true);
                            saveLikeState(true);
                            isLiked = true;
                            // Modifiez votre code pour utiliser le Toast personnalisé
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_layout, null);

// Modifiez le texte si nécessaire
                            TextView text = layout.findViewById(R.id.textViewMessage);
                            text.setText("Product added to favorites");

// Créez le Toast personnalisé
                            Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Gérer les erreurs si nécessaire
                    }
                });
    }






    private void removeFromFavorites() {
        // Supprimer le produit des favoris
        favoritesDatabaseRef.child(currentUserId)
                .orderByChild("name")
                .equalTo(productName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                childSnapshot.getRef().removeValue();
                            }
                            favoriteItem = null; // Réinitialiser favoriteItem car le produit n'est plus dans les favoris
                            updateLikeButtonColor(false); // Réinitialiser l'icône
                            saveLikeState(false);
                            // Modifiez votre code pour utiliser le Toast personnalisé
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_layout, null);

// Modifiez le texte si nécessaire
                            TextView text = layout.findViewById(R.id.textViewMessage);
                            text.setText("Product removed from favorites!");

// Créez le Toast personnalisé
                            Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Gérer les erreurs si nécessaire
                    }
                });
    }


    private void saveLikeState(boolean isLiked) {
        // Sauvegarde de l'état "aimé" dans les préférences
        SharedPreferences.Editor editor = getSharedPreferences("LikeState", MODE_PRIVATE).edit();
        editor.putBoolean(productName, isLiked);
        editor.apply();
    }

    private boolean loadLikeState() {
        // Charger l'état "aimé" depuis les préférences
        SharedPreferences prefs = getSharedPreferences("LikeState", MODE_PRIVATE);
        return prefs.getBoolean(productName, true);
    }
}