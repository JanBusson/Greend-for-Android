package com.example.greendr;

import android.content.Intent;
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

public class StartFragment extends Fragment {

    public StartFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.start_view, container, false);

//        Button openSecondBtn = view.findViewById(R.id.button_open_second);
//        openSecondBtn.setOnClickListener(v -> {
//            // Fragmentwechsel statt Intent!
//            if (getActivity() instanceof MainActivity) {
//                ((MainActivity) getActivity()).openFragment(new LoginFragment());
//            }
//        });

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
