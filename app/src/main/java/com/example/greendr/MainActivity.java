package com.example.greendr;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Das Layout mit dem Fragment-Container

        if (savedInstanceState == null) {
            openFragment(new StartFragment()); // oder WelcomeFragment/LoginFragment
        }
    }

    public void openFragment(StartFragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
