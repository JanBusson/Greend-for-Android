package com.example.greendr;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Firebase global initialisieren
        FirebaseApp.initializeApp(this);
        FirebaseInstance.init();
    }
}
