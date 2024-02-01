package com.example.beautyandcosmetics;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;

public class NatusActivity extends AppCompatActivity {
    private boolean isLiked = false;
    private boolean isLiked2 = false;

    private ImageView likeButton;
    private ImageView likeButton2;
    private ViewPager2 viewPager;
    private NatusAdapter adapter;
    private DrawerLayout drawerLayout; // Déclarez DrawerLayout

    private int currentPage = 0;
    private final long delay = 5000; // Délai en millisecondes entre chaque défilement
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_natus);

        drawerLayout = findViewById(R.id.drawer_layout); // Initialisez DrawerLayout

        ImageView btn = findViewById(R.id.shopping_cart_icon);

        btn.setOnClickListener(
                new View.OnClickListener(){;
                    public void onClick (View btn) {
                        Intent i = new Intent(NatusActivity.this, CartActivity.class);
                        startActivity(i);
                    }
                });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                // Vérifiez l'ID de l'élément de menu sélectionné
                int id = menuItem.getItemId();


                if (id == R.id.nav_about_us) {
                    // L'élément "Home" a été sélectionné, redirigez vers la page "Home"
                    Intent intent = new Intent(NatusActivity.this, AboutActivity2.class);
                    startActivity(intent);
                }



                if (id == R.id.nav_home) {
                    // L'élément "Home" a été sélectionné, redirigez vers la page "Home"
                    Intent intent = new Intent(NatusActivity.this, MainActivity.class);
                    startActivity(intent);
                }



                if (id == R.id.nav_shop) {
                    // L'élément "Home" a été sélectionné, redirigez vers la page "Home"
                    Intent intent = new Intent(NatusActivity.this, ShopActivity.class);
                    startActivity(intent);
                }




                return true;
            }
        });





        likeButton = findViewById(R.id.likeIcon);
        likeButton2 = findViewById(R.id.likeIcon2);
        viewPager = findViewById(R.id.viewPager);

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Inversez l'état de like pour le premier bouton "like".
                isLiked = !isLiked;

                // Appliquez la teinte rouge si l'icône est "aimé", sinon réinitialisez la couleur.
                if (isLiked) {
                    likeButton.setColorFilter(getResources().getColor(R.color.redColor), PorterDuff.Mode.SRC_IN);
                } else {
                    likeButton.setColorFilter(null); // Réinitialisez la couleur
                }
            }
        });

        likeButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Inversez l'état de like pour le deuxième bouton "like".
                isLiked2 = !isLiked2;

                // Appliquez la teinte rouge si l'icône est "aimé", sinon réinitialisez la couleur.
                if (isLiked2) {
                    likeButton2.setColorFilter(getResources().getColor(R.color.redColor), PorterDuff.Mode.SRC_IN);
                } else {
                    likeButton2.setColorFilter(null); // Réinitialisez la couleur
                }
            }
        });


        ImageView sidebarImage = findViewById(R.id.sidebar);
        sidebarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ouvrez le menu de navigation lorsque l'image sidebar est cliquée
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        // Configurez le carousel avec défilement automatique (code ajouté)
        adapter = new NatusAdapter();
        viewPager.setAdapter(adapter);

        // Démarrez le défilement automatique
        handler.postDelayed(update, delay);



    }

    private final Runnable update = new Runnable() {
        public void run() {
            if (currentPage == adapter.getItemCount() - 1) {
                currentPage = 0;
            } else {
                currentPage++;
            }
            viewPager.setCurrentItem(currentPage);
            handler.postDelayed(this, delay);
        }
    };
}
