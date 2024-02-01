package com.example.beautyandcosmetics;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {

    private GridView gridView;
    private DatabaseReference databaseReference;
    private ProductsAdapter productsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);


        ImageView btn = findViewById(R.id.order);

        btn.setOnClickListener(
                new View.OnClickListener(){;
                    public void onClick (View btn) {
                        Intent i = new Intent(ProductsActivity.this, OrderActivity.class);
                        startActivity(i);
                        finish();
                    }

                });



        ImageView home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirigez vers CartActivity lorsque le bouton est cliqué
                Intent intent = new Intent(ProductsActivity.this, Inscription.class);
                startActivity(intent);
                finish();
            }
        });





        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("products");

        // Example data
        List<ShopItem> productList = new ArrayList<>();

        // Read data from Firebase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ShopItem product = snapshot.getValue(ShopItem.class);
                    productList.add(product);
                }

                // Update the GridView after retrieving data
                updateGridView(productList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void updateGridView(List<ShopItem> productList) {
        // Create an adapter
        productsAdapter = new ProductsAdapter(this, productList);

        // Configure the GridView with GridLayout
        gridView = findViewById(R.id.gridView);
        gridView.setAdapter(productsAdapter);
        gridView.setNumColumns(2);

        // Set item click listener for further actions
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            // Handle click on item, you can open a detailed view or perform other actions if needed
        });
    }

    public void onUpdateProductClick(View view) {
        // Get the clicked product
        ShopItem clickedProduct = (ShopItem) view.getTag();

        // Check if clickedProduct is not null before proceeding
        if (clickedProduct != null) {
            showUpdateDialog(clickedProduct);
        } else {
            // Display an error message or perform another action accordingly
            Toast.makeText(this, "Clicked product is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void showUpdateDialog(ShopItem clickedProduct) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Product");
        View dialogView = getLayoutInflater().inflate(R.layout.update_product, null);
        builder.setView(dialogView);

        EditText newNameEditText = dialogView.findViewById(R.id.editTextNewName);
        EditText newPriceEditText = dialogView.findViewById(R.id.editTextNewPrice);

        newNameEditText.setText(clickedProduct.getName());
        newPriceEditText.setText(clickedProduct.getPrice());

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the new values from the dialog
                String newName = newNameEditText.getText().toString().trim();
                String newPrice = newPriceEditText.getText().toString().trim();

                Log.d("UpdateValues", "New Name: " + newName + ", New Price: " + newPrice);

                // Update the product in the adapter
                clickedProduct.setName(newName);
                clickedProduct.setPrice(newPrice);
                productsAdapter.notifyDataSetChanged();

                // Update the product in Firebase
                updateProductInFirebase(clickedProduct);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void updateProductInFirebase(ShopItem updatedProduct) {
        Log.d("FirebaseUpdate", "Updating product in Firebase");
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");

        // Find the product by its id
        Query query = productsRef.orderByChild("name").equalTo(updatedProduct.getName());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Update the fields of the product in Firebase
                    snapshot.getRef().child("name").setValue(updatedProduct.getName());
                    snapshot.getRef().child("price").setValue(updatedProduct.getPrice() + " MAD");
                    Log.d("FirebaseUpdate", "Product updated in Firebase");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.e("FirebaseUpdate", "Update failed: " + databaseError.getMessage());
            }
        });
    }
}
