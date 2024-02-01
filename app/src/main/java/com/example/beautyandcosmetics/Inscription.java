package com.example.beautyandcosmetics;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Inscription extends Activity {

    private static final int PICK_FILE_REQUEST = 1;
    private ImageView imageViewProduct;
    private EditText nameEditText;
    private EditText brandEditText;
    private EditText priceEditText;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inscription);

        ImageView btn = findViewById(R.id.order);

        btn.setOnClickListener(
                new View.OnClickListener(){;
                    public void onClick (View btn) {
                        Intent i = new Intent(Inscription.this, OrderActivity.class);
                        startActivity(i);
                        finish();
                    }
                });

        ImageView products = findViewById(R.id.Products);
        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirigez vers CartActivity lorsque le bouton est cliqué
                Intent intent = new Intent(Inscription.this, ProductsActivity.class);
                startActivity(intent);
                finish();
            }
        });





        imageViewProduct = findViewById(R.id.imageViewProduct);
        nameEditText = findViewById(R.id.nameEditText);
        priceEditText = findViewById(R.id.priceEditText);
        Button btnAddimg = findViewById(R.id.btnAddimg);
        Button uploadButton = findViewById(R.id.uploadButton);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("products");
        storageReference = FirebaseStorage.getInstance().getReference();

        btnAddimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectFileClick();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProduct();
            }
        });
    }

    public void onSelectFileClick() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                selectedImageUri = data.getData();
                imageViewProduct.setImageURI(selectedImageUri);
            }
        }
    }


    private void uploadProduct() {
        String name = nameEditText.getText().toString().trim();
        String price = priceEditText.getText().toString().trim();

        if (!name.isEmpty() && !price.isEmpty() && selectedImageUri != null) {
            price = price + " MAD";
            Product product = new Product(name, price);

            // Generate a unique key for the product
            String productId = databaseReference.push().getKey();

            // Upload image to Firebase Storage
            StorageReference imageRef = storageReference.child("images/" + productId + ".jpg");
            UploadTask uploadTask = imageRef.putFile(selectedImageUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Image uploaded successfully, get the download URL
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    product.setImageUrl(imageUrl);

                    // Save the product to the database with the generated key
                    databaseReference.child(productId).setValue(product);

                    // Clear the fields
                    nameEditText.setText("");
                    priceEditText.setText("");
                    imageViewProduct.setImageResource(android.R.color.transparent);

                    // Modifiez votre code pour utiliser le Toast personnalisé
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_layout, null);

// Modifiez le texte si nécessaire
                    TextView text = layout.findViewById(R.id.textViewMessage);
                    text.setText("Product added successfuly");

// Créez le Toast personnalisé
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();                });
            }).addOnFailureListener(e -> {
                // Handle errors during image upload
                // Modifiez votre code pour utiliser le Toast personnalisé
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_layout, null);

// Modifiez le texte si nécessaire
                TextView text = layout.findViewById(R.id.textViewMessage);
                text.setText("Failed to upload image!");

// Créez le Toast personnalisé
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
            });
        } else {
            // Modifiez votre code pour utiliser le Toast personnalisé
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_layout, null);

// Modifiez le texte si nécessaire
            TextView text = layout.findViewById(R.id.textViewMessage);
            text.setText("Fields are required ");

// Créez le Toast personnalisé
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();



    }
}
