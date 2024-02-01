package com.example.beautyandcosmetics;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.internet.MimeMessage;

public class ContactActivity extends AppCompatActivity {
    private DatabaseReference cartDatabaseRef;
    private TextView cartCountTextView;
    private DrawerLayout drawerLayout;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        EditText nameEditText = findViewById(R.id.CName);
        EditText emailEditText = findViewById(R.id.CEmail);
        EditText phoneEditText = findViewById(R.id.CPhone);
        EditText messageEditText = findViewById(R.id.CMessage);
        Button sendButton = findViewById(R.id.send);


        // Get the current user's ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the "users" table in Firebase
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

        // Retrieve data from the "users" table
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve user data
                    String name = dataSnapshot.child("fullName").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);

                    // Set the data to corresponding EditText fields
                    nameEditText.setText(name);
                    emailEditText.setText(email);
                    phoneEditText.setText(phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if necessary
                Log.e("FirebaseError", "Error retrieving user data: " + databaseError.getMessage());
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String message = messageEditText.getText().toString();

                sendEmail(name, email, phone, message);
            }
        });

        cartDatabaseRef = FirebaseDatabase.getInstance().getReference().child("cart");

        // Assume you have a TextView with the id cartCountTextView
        cartCountTextView = findViewById(R.id.cartCountTextView);

        drawerLayout = findViewById(R.id.drawer_layout);
        firebaseAuth = FirebaseAuth.getInstance();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.nav_home) {
                    Intent intent = new Intent(ContactActivity.this, MainActivity.class);
                    startActivity(intent);


                }
                if (id == R.id.nav_shop) {
                    Intent intent = new Intent(ContactActivity.this, ShopActivity.class);
                    startActivity(intent);
                    finish();
                }

                if (id == R.id.nav_about_us) {
                    Intent intent = new Intent(ContactActivity.this, AboutActivity2.class);
                    startActivity(intent);
                    finish();
                }



                if (id == R.id.showMap) {
                    Intent intent = new Intent(ContactActivity.this, MapsActivity.class);
                    startActivity(intent);
                    finish();
                }

                if (id == R.id.nav_profile) {
                    Intent intent = new Intent(ContactActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();

                }
                if (id == R.id.nav_like) {
                    Intent intent = new Intent(ContactActivity.this, FavoriteActivity.class);
                    startActivity(intent);
                    finish();
                }

                if (id == R.id.logout) {
                    // Déconnexion de Firebase
                    FirebaseAuth.getInstance().signOut();

                    // Redirection vers l'écran de connexion
                    Intent intent = new Intent(ContactActivity.this, LoginActivity.class);
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
                Intent intent = new Intent(ContactActivity.this, CartActivity.class);
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
    }

    private void sendEmail(String name, String email, String phone, String message) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                // Configurez les propriétés du courrier électronique
                Properties properties = new Properties();
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");
                properties.put("mail.smtp.host", "smtp.gmail.com");
                properties.put("mail.smtp.port", "587");

                // Adresse e-mail et mot de passe de l'expéditeur
                final String username = "chaimakechtaf7@gmail.com";
                final String password = "xhobuaoilidolmph";

                // Créez une session avec un authentificateur
                Session session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

                try {
                    Message mimeMessage = new MimeMessage(session);
                    mimeMessage.setFrom(new InternetAddress(username));
                    mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse("chaimakechtaf7@gmail.com"));
                    mimeMessage.setSubject("New message from Biobelle");
                    mimeMessage.setText("Name : " + name + "\nEmail : " + email + "\nPhone : " + phone + "\nMessage : " + message);

                    Transport.send(mimeMessage);

                    return true;
                } catch (Exception e) {
                    Log.e("EmailException", "Error sending email\n", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    // Modifiez votre code pour utiliser le Toast personnalisé
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_layout, null);

// Modifiez le texte si nécessaire
                    TextView text = layout.findViewById(R.id.textViewMessage);
                    text.setText("Message sent successfully");

// Créez le Toast personnalisé
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();                } else {

                    // Modifiez votre code pour utiliser le Toast personnalisé
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_layout, null);

// Modifiez le texte si nécessaire
                    TextView text = layout.findViewById(R.id.textViewMessage);
                    text.setText("Failed to send message. Please try again later.");

// Créez le Toast personnalisé
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                }
            }
        }.execute();
    }
}
