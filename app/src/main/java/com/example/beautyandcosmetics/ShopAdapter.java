package com.example.beautyandcosmetics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

public class ShopAdapter extends ArrayAdapter<ShopItem> {

    public ShopAdapter(Context context, List<ShopItem> productList) {
        super(context, 0, productList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.shop_item, parent, false);
        }

        ShopItem currentProduct = getItem(position);

        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView textViewName = convertView.findViewById(R.id.textViewName);
        TextView textViewPrice = convertView.findViewById(R.id.textViewPrice);

        if (currentProduct != null) {
            // Use Glide to load the image from the URL into the ImageView
            Glide.with(getContext())
                    .load(currentProduct.getImageUrl())
                    .into(imageView);

            textViewName.setText(currentProduct.getName());
            textViewPrice.setText(currentProduct.getPrice());
        }

        return convertView;
    }
}
