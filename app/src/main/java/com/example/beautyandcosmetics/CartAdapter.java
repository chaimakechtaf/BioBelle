package com.example.beautyandcosmetics;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends ArrayAdapter<CartItem> {

    public CartAdapter(Context context, List<CartItem> products) {
        super(context, 0, products);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.cart_item_layout, parent, false);
        }

        CartItem currentProduct = getItem(position);

        ImageView productImage = listItemView.findViewById(R.id.imageView);
        Picasso.get().load(currentProduct.getImageURL()).into(productImage);

        TextView textProductName = listItemView.findViewById(R.id.textViewName);
        textProductName.setText(currentProduct.getName());

        TextView textPrice = listItemView.findViewById(R.id.textViewPrice);
        textPrice.setText(currentProduct.getPrice());

        TextView textQuantity = listItemView.findViewById(R.id.textQuantity);
        String quantityText = String.format(Locale.getDefault(),
                "Quantity: %d ", currentProduct.getQuantity());
        textQuantity.setText(quantityText);

        ImageView imageViewDelete = listItemView.findViewById(R.id.delete_product);
        imageViewDelete.setTag(position);
        imageViewDelete.setOnClickListener((v) -> onDeleteClick((int) v.getTag()));

        return listItemView;
    }

    private void onDeleteClick(int position) {
        CartItem deletedProduct = getItem(position);
        if (deletedProduct != null) {
            showDeleteConfirmationDialog(deletedProduct.getProductId(), position);
        }
    }

    private void showDeleteConfirmationDialog(String productId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to delete this product?");


        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProductFromFirebase(productId);
                remove(getItem(position));
                notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProductFromFirebase(String productId) {
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cart");
        Query query = cartRef.orderByChild("productId").equalTo(productId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
