package com.example.beautyandcosmetics;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderActivity extends AppCompatActivity {

    private DatabaseReference checkoutRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);

        ImageView products = findViewById(R.id.Products);
        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderActivity.this, ProductsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ImageView home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderActivity.this, Inscription.class);
                startActivity(intent);
                finish();
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        checkoutRef = database.getReference("checkout");

        checkoutRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Checkout checkout = dataSnapshot.getValue(Checkout.class);
                if (checkout != null) {
                    TableRow tableRow = createTableRow(checkout, dataSnapshot.getKey());
                    TableLayout tableLayout = findViewById(R.id.tableLayout);
                    tableLayout.addView(tableRow);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                // Handle child data changed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                removeTableRow(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                // Handle child moved
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    private TableRow createTableRow(Checkout checkout, String key) {
        TableRow tableRow = new TableRow(OrderActivity.this);

        addTextViewToRow(tableRow, checkout.getFull_Name());
        addTextViewToRow(tableRow, checkout.getPhone());
        addTextViewToRow(tableRow, checkout.getEmail());
        addTextViewToRow(tableRow, checkout.getAddress());
        addTextViewToRow(tableRow, checkout.getCity());
        addTextViewToRow(tableRow, checkout.getTotalProducts());

        // Convert double to String for TotalPrice
        String totalPriceString = String.valueOf(checkout.getTotalPrice() + " MAD");
        addTextViewToRow(tableRow, totalPriceString);

        // Add a delete button with a click listener
        ImageView deleteButton = new ImageView(OrderActivity.this);
        deleteButton.setImageResource(R.drawable.delete); // Set your delete icon here
        deleteButton.setPadding(8, 8, 8, 8);
        deleteButton.setClickable(true);
        deleteButton.setFocusable(true);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClicked(key);
            }
        });
        tableRow.addView(deleteButton);

        // Set the key as a tag for the TableRow
        tableRow.setTag(key);

        return tableRow;
    }

    private void addTextViewToRow(TableRow tableRow, String text) {
        TextView textView = new TextView(OrderActivity.this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        tableRow.addView(textView);
    }

    private void removeTableRow(String key) {
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        int childCount = tableLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = tableLayout.getChildAt(i);
            if (view instanceof TableRow && view.getTag() != null && view.getTag().equals(key)) {
                tableLayout.removeView(view);
                break;
            }
        }
    }

    public void onDeleteClicked(String key) {
        // Créez une boîte de dialogue de confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this order?")
                .setTitle("Confirmation")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Supprimez l'élément après confirmation
                        removeTableRow(key);
                        checkoutRef.child(key).removeValue();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // L'utilisateur a choisi de ne pas supprimer, ne rien faire ici
                    }
                });

        // Affichez la boîte de dialogue
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
