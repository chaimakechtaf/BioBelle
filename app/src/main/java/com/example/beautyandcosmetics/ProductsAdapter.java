package com.example.beautyandcosmetics;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ProductsAdapter extends ArrayAdapter<ShopItem> {

    private List<ShopItem> productList;

    public ProductsAdapter(Context context, List<ShopItem> productList) {
        super(context, 0, productList);
        this.productList = productList;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.products_item, parent, false);
        }

        ShopItem currentProduct = getItem(position);

        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView textViewName = convertView.findViewById(R.id.textViewName);
        TextView textViewPrice = convertView.findViewById(R.id.textViewPrice);
        ImageView deleteIcon = convertView.findViewById(R.id.delete_product);
        ImageView updateIcon = convertView.findViewById(R.id.update_product);

        if (currentProduct != null) {
            // Use Glide to load the image from the URL into the ImageView
            Glide.with(getContext())
                    .load(currentProduct.getImageUrl())
                    .into(imageView);

            textViewName.setText(currentProduct.getName());
            textViewPrice.setText(currentProduct.getPrice());
        }

        // Set click listener for delete icon
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle delete operation here
                if (currentProduct != null) {
                    // Call a method to delete the product from Firebase
                    deleteProduct(currentProduct.getName());
                }
            }
        });

        // Set click listener for update icon
        updateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle update operation here
                if (currentProduct != null) {
                    // Call a method to update the product
                    promptForUpdate(currentProduct);
                }
            }
        });

        return convertView;
    }

    private void promptForUpdate(final ShopItem product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Update Product");

        // Inflate a custom layout for the dialog
        View view = LayoutInflater.from(getContext()).inflate(R.layout.update_product, null);
        builder.setView(view);

        final EditText newNameEditText = view.findViewById(R.id.editTextNewName);
        final EditText newPriceEditText = view.findViewById(R.id.editTextNewPrice);

        // Set initial values in EditText fields
        newNameEditText.setText(product.getName());
        newPriceEditText.setText(product.getPrice());

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked Update button.
                String newName = newNameEditText.getText().toString().trim();
                String newPrice = newPriceEditText.getText().toString().trim();

                // Call a method to update the product in the adapter
                updateProduct(product.getName(), newName, newPrice);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked Cancel button. Do nothing.
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Add this method to update a product
    public void updateProduct(String productName, String newName, String newPrice) {
        for (int i = 0; i < getCount(); i++) {
            ShopItem product = getItem(i);
            if (product != null && product.getName().equals(productName)) {
                product.setName(newName);
                product.setPrice(newPrice);
                notifyDataSetChanged();

                // Update the product in Firebase
                updateProductInFirebase(productName, newName, newPrice);

                break;
            }
        }
    }

    // Method to update a product in Firebase
    private void updateProductInFirebase(String productName, String newName, String newPrice) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");

        // Query to find the product by name
        Query query = productsRef.orderByChild("name").equalTo(productName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Update the product data in Firebase
                    snapshot.getRef().child("name").setValue(newName);
                    snapshot.getRef().child("price").setValue(newPrice);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }


    // Add this method to remove a product from the list
    public void removeProduct(String productName) {
        for (int i = 0; i < getCount(); i++) {
            ShopItem product = getItem(i);
            if (product != null && product.getName().equals(productName)) {
                remove(product);
                notifyDataSetChanged();
                break;
            }
        }
    }

    private void deleteProduct(String productName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to delete this product?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked Yes button. Delete the product.
                DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");

                // Query to find the product by name
                Query query = productsRef.orderByChild("name").equalTo(productName);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue(); // This will remove the product from the database
                        }
                        // After deleting, update the adapter with the updated list
                        removeProduct(productName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked No button. Do nothing.
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

