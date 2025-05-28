package com.example.greendr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/*########################################################
LoginView ermöglicht das eingeben der Login Daten und prüft deren Korrektheit
Bei Übereinstimmung wird zum WelcomeScreen weitergeleitet
########################################################*/

public class LoginView extends AppCompatActivity {

    //prüfen des Passworts
    private boolean checkLogin(String userName, String password){
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);

        Log.d("FIREBASE", "StartView gestartet!");

        // Firebase Referenz holen
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("test");

        // Testdaten
        Map<String, Object> testData = new HashMap<>();
        testData.put("name", "Anna");
        testData.put("age", 22);

        // Daten an Firebase senden
        ref.setValue(testData);

        //speichert den Button aus dem ausgewähltem Layout
        Button openSecondBtn = findViewById(R.id.go_back_start);

        //Logout Bildschirmwechsel
        openSecondBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginView.this, StartView.class);
            startActivity(intent);
        });
    }
}
