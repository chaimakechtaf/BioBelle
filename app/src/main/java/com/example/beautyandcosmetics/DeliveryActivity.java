package com.example.beautyandcosmetics;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeliveryActivity extends AppCompatActivity {

    private EditText editTextFullName;
    private EditText editTextEmail;
    private EditText editTextPhone;
    private EditText editTextAddress;
    private Spinner spinnerCity;
    private TextView TotalProd;
    private TextView TotalPrice;

    // Firebase
    private DatabaseReference checkoutRef;
    private DatabaseReference cartRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery);

        editTextFullName = findViewById(R.id.etFullName);
        editTextEmail = findViewById(R.id.etEmail);
        editTextPhone = findViewById(R.id.etPhone);
        editTextAddress = findViewById(R.id.etAddress);
        spinnerCity = findViewById(R.id.spinnerCity);
        TotalProd = findViewById(R.id.Total_Prod);
        TotalPrice = findViewById(R.id.Total_Price);

        String[] cityOptions = {"Choose your City...", "Agadir", "Casablanca", "Fes", "Rabat", "Marrakech", "Tanger", "Taroudannt"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCity.setAdapter(adapter);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        checkoutRef = database.getReference("checkout");
        cartRef = database.getReference("cart");

        Button buttonSubmit = findViewById(R.id.confirmButton);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = editTextFullName.getText().toString();
                String email = editTextEmail.getText().toString();
                String phone = editTextPhone.getText().toString();
                String address = editTextAddress.getText().toString();
                String city = spinnerCity.getSelectedItem().toString();

                // Disable the button to prevent multiple clicks during calculation
                buttonSubmit.setEnabled(false);

                // Fetch total products and total price from cart
                calculateTotalPrice(new TotalPriceCallback() {
                    @Override
                    public void onTotalPriceCalculated(BigDecimal totalPrice, String totalProducts) {
                        // Enable the button before proceeding with the checkout
                        buttonSubmit.setEnabled(true);

                        // Continue with the checkout
                        Checkout checkout = new Checkout(fullName, email, phone, address, city, totalProducts, totalPrice.doubleValue());

                        // Save the Checkout object to Firebase
                        checkoutRef.push().setValue(checkout)
                                .addOnCompleteListener(DeliveryActivity.this, task -> {
                                    if (task.isSuccessful()) {
                                        // Data successfully inserted into Firebase

                                        startActivity(new Intent(DeliveryActivity.this, SucessActivity.class));
                                        finish();
                                        String message = "Your order has been successfully registered. Thank you!";
                                        Toast.makeText(DeliveryActivity.this, message, Toast.LENGTH_SHORT).show();


                                    } else {
                                        // Failed to insert data into Firebase
                                        String errorMessage = "Failed to register your order. Please try again.";
                                        Toast.makeText(DeliveryActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }
        });

        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("cart");
            databaseReference.orderByChild("userId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<CartItem> productList = new ArrayList<>();
                    productList.clear();

                    StringBuilder totalProductsBuilder = new StringBuilder("Total Products: ");
                    BigDecimal totalPrice = BigDecimal.ZERO;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CartItem product = snapshot.getValue(CartItem.class);

                        if (product != null) {
                            productList.add(product);

                            // Append product name and quantity to the StringBuilder
                            totalProductsBuilder.append(product.getName())
                                    .append(" (")
                                    .append(product.getQuantity())
                                    .append("), ");

                            // Calculate total price
                            String cleanPrice = product.getPrice().replaceAll("[^\\d.]+", "");
                            if (!TextUtils.isEmpty(cleanPrice)) {
                                BigDecimal productPrice = new BigDecimal(cleanPrice);
                                totalPrice = totalPrice.add(productPrice.multiply(BigDecimal.valueOf(product.getQuantity())));
                            }
                        }
                    }

                    // Remove the trailing comma and space
                    if (totalProductsBuilder.length() > 2) {
                        totalProductsBuilder.setLength(totalProductsBuilder.length() - 2);
                    }

                    // Update the adapter
                    CartAdapter adapter = new CartAdapter(DeliveryActivity.this, productList);

                    // Calculate and display total price
                    TextView totalPriceTextView = findViewById(R.id.Total_Price);
                    String totalPriceText = String.format(getString(R.string.total_price_format), totalPrice);
                    totalPriceTextView.setText(totalPriceText);

                    // Display total products
                    TotalProd.setText("" + totalProductsBuilder.toString());

                    // Set the total products and total price in the Checkout object
                    TotalPrice.setText(totalPriceText);
                    TotalProd.setText(totalProductsBuilder.toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                    Log.e("DeliveryActivity", "Failed to read cart data", databaseError.toException());
                }
            });
        }
    }

    private void calculateTotalPrice(TotalPriceCallback callback) {
        final BigDecimal[] totalPrice = {BigDecimal.ZERO};
        final StringBuilder totalProductsBuilder = new StringBuilder();

        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            cartRef.orderByChild("userId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CartItem product = snapshot.getValue(CartItem.class);

                        // Null check for product
                        if (product != null) {
                            String cleanPrice = product.getPrice().replaceAll("[^\\d.]+", "");

                            if (!cleanPrice.isEmpty()) {
                                BigDecimal productPrice = new BigDecimal(cleanPrice);
                                totalPrice[0] = totalPrice[0].add(productPrice.multiply(BigDecimal.valueOf(product.getQuantity())));

                                // Append the product name and quantity to the StringBuilder
                                totalProductsBuilder.append(product.getQuantity())
                                        .append(" x ")
                                        .append(product.getName())
                                        .append(", ");
                            }
                        }
                    }

                    // Remove the trailing comma and space
                    if (totalProductsBuilder.length() > 2) {
                        totalProductsBuilder.setLength(totalProductsBuilder.length() - 2);
                    }

                    // Invoke the callback with the calculated total price and total products
                    callback.onTotalPriceCalculated(totalPrice[0], totalProductsBuilder.toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                    Log.e("DeliveryActivity", "Failed to read cart data", databaseError.toException());
                }
            });
        }
    }

    private interface TotalPriceCallback {
        void onTotalPriceCalculated(BigDecimal totalPrice, String totalProducts);
    }
}
