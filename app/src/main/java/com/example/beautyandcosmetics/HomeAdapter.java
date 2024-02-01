package com.example.beautyandcosmetics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import androidx.recyclerview.widget.RecyclerView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.CarouselViewHolder> {
    private int[] images = {R.drawable.rabab,R.drawable.kaoutar,R.drawable.khadija,R.drawable.keltoum,R.drawable.asma,R.drawable.latifa, R.drawable.doha, R.drawable.amal}; // Remplacez les ressources par vos images
    private Context context;

    public HomeAdapter(Context context) {
        this.context = context;
    }

    public class CarouselViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public CarouselViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.carouselItemImageView);
        }
    }

    @Override
    public CarouselViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carousel_item, parent, false);
        return new CarouselViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CarouselViewHolder holder, int position) {
        // Utilisez Glide pour charger l'image dans l'ImageView
        Glide.with(context)
                .load(images[position])
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }
}
