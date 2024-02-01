package com.example.beautyandcosmetics;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        // Enable disk persistence (optional)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

