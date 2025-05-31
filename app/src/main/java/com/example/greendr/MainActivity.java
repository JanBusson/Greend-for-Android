package com.example.greendr;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/*##################################################
Die Main Activity dient als Container für die anderen App Abschnitte.
Diese werden als Fragments umgesetzt. Das ist eine viel bessere Lösung als jeden Bereich
als neue Activity abzuspeichern.
##################################################*/
public class MainActivity extends AppCompatActivity {

    //Öffnet bei beginn der App das StartFragement
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            openFragment(new StartFragment());
        }
    }

    //Diese Methode hilft beim Knopfdruck auf einen anderen Bereich zu wechseln
    public void openFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
