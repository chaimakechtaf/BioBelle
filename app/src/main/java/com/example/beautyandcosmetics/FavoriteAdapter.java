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


public class FavoriteAdapter extends ArrayAdapter<FavoriteItem> {

    public FavoriteAdapter(Context context, List<FavoriteItem> products) {
        super(context, 0, products);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.fav_items, parent, false);
        }

        FavoriteItem currentProduct = getItem(position);

        ImageView productImage = listItemView.findViewById(R.id.imageView);
        Picasso.get().load(currentProduct.getImageURL()).into(productImage);

        TextView textProductName = listItemView.findViewById(R.id.textViewName);
        textProductName.setText(currentProduct.getName());

        TextView textPrice = listItemView.findViewById(R.id.textViewPrice);
        textPrice.setText(currentProduct.getPrice());

       

        return listItemView;
    }

    private void onDeleteClick(int position) {
        FavoriteItem deletedProduct = getItem(position);
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
        DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("favorites");
        Query query = favRef.orderByChild("productId").equalTo(productId);

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

