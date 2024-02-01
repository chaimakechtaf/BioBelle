package com.example.beautyandcosmetics;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SucessActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        Button btn = findViewById(R.id.RetourHome);

        btn.setOnClickListener(
                new View.OnClickListener(){;
                    public void onClick (View btn) {
                        Intent i = new Intent(SucessActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }

                });
    }
}
