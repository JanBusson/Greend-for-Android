package com.example.greendr;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WelcomeFragment extends Fragment {

    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        Button matchBtn = view.findViewById(R.id.button_find_match);
        Button chatBtn = view.findViewById(R.id.button_chat);
        Button logoutBtn = view.findViewById(R.id.button_logout);

        matchBtn.setOnClickListener(v -> {
            // TODO: MatchActivity starten
            Log.d("WelcomeFragment", "Match finden geklickt");
        });

        chatBtn.setOnClickListener(v -> {
            // TODO: ChatActivity starten
            Log.d("WelcomeFragment", "Chatten geklickt");
        });

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            // TODO: Zurück zum LoginScreen navigieren
            Log.d("WelcomeFragment", "Logout ausgeführt");
        });

        return view;
    }
}