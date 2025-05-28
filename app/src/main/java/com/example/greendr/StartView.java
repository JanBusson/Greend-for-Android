package com.example.greendr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StartView extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_view);

        //speichert den Button aus dem ausgewähltem Layout
        Button openSecondBtn = findViewById(R.id.button_open_second);

        //Intent object für den Bildschirmwechsel
        openSecondBtn.setOnClickListener(v -> {
            Intent intent = new Intent(StartView.this, LoginView.class);
            startActivity(intent);
        });

    }
}