package com.example.greendr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

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

        FirebaseInstance.getDatabase().getReference("testConnection").setValue("App gestartet ✅");

        //speichert den Button aus dem ausgewähltem Layout
        Button openSecondBtn = findViewById(R.id.go_back_start);

        //Logout Bildschirmwechsel
        openSecondBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginView.this, StartView.class);
            startActivity(intent);
        });
    }
}
