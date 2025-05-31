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
        View view = inflater.inflate(R.layout.welcome_view, container, false);

        Button logoutBtn = view.findViewById(R.id.logout);

        logoutBtn.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String uid = user.getUid();

                DatabaseReference userRef = FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(uid);

                userRef.setValue(new UserProfile("Anna", 25, "Sport, Musik"));
            }

            // Fragmentwechsel zurück zur Startansicht
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openFragment(new StartFragment());
            }
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();

        String testEmail = "testuser@example.com";
        String testPassword = "123456";

        auth.createUserWithEmailAndPassword(testEmail, testPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        String uid = user.getUid();

                        UserProfile testProfile = new UserProfile("Anna", 25, "Sport, Musik");

                        DatabaseReference userRef = FirebaseInstance.getDatabase()
                                .getReference("users").child(uid);

                        userRef.setValue(testProfile);

                        Log.d("FirebaseAuth", "Testuser erstellt: " + uid);
                    } else {
                        Log.e("FirebaseAuth", "Fehler: " + task.getException().getMessage());
                    }
                });


        return view;
    }
}