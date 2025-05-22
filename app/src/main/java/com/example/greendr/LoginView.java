package com.example.greendr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginView extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);

        //speichert den Button aus dem ausgewähltem Layout
        Button openSecondBtn = findViewById(R.id.go_back_start);

        //Intent object für den Bildschirmwechsel
        openSecondBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginView.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
