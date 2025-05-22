package com.example.greendr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //speichert den Button aus dem ausgewähltem Layout
        Button openSecondBtn = findViewById(R.id.button_open_second);

        //Intent object für den Bildschirmwechsel
        openSecondBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginView.class);
            startActivity(intent);
        });

    }
}