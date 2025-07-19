package com.example.greendr;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


//Zeigt das Profil eines Nutzers

public class FindMatchFragment extends Fragment {

    private DatabaseReference usersRef;
    private FirebaseUser currentUser;
    private List<User> userList = new ArrayList<>();
    private int currentIndex = 0;

    // Hier müssen die Text Views schon im Fragment sein, da die Inhalte dafür aus der DB kommen und somit nicht statischer Natur sind
    private TextView nameText, genderText, ageText, hometownText, jobTitleText, universityText, languagesText, sexualityText, workplaceText, bioText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_match, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Textfelder
        nameText = view.findViewById(R.id.text_name);
        genderText = view.findViewById(R.id.text_gender);
        hometownText = view.findViewById(R.id.text_hometown);
        jobTitleText = view.findViewById(R.id.text_job_title);
        universityText = view.findViewById(R.id.text_university);
        languagesText = view.findViewById(R.id.text_languages);
        sexualityText = view.findViewById(R.id.text_sexuality);
        workplaceText = view.findViewById(R.id.text_workplace);
        bioText = view.findViewById(R.id.text_bio);

        // Buttons
        Button likeButton = view.findViewById(R.id.button_like);
        Button rejectButton = view.findViewById(R.id.button_reject);


        //Klären was passiert
        likeButton.setOnClickListener(v -> {
            if (currentUser != null && !userList.isEmpty()) {
                User swipedUser = userList.get(currentIndex);
                saveSwipe(swipedUser.userId, "like");
            }
            showNextUser();
        });

        rejectButton.setOnClickListener(v ->{
            if (currentUser != null && !userList.isEmpty()) {
                User swipedUser = userList.get(currentIndex);
                saveSwipe(swipedUser.userId, "dislike");
            }
            showNextUser();
        });

        // TODO hier noch ein Button zum Kommentar schreiben

        loadUsers();


        return view;
    }

    private void loadUsers() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    try {
                        Object value = userSnap.getValue();
                        //Dieser Abschnitt prüft dass es sich beim User um ein HashMap handelt und nicht nur z.B. um einen String
                        if (value instanceof java.util.Map) {
                            User user = userSnap.getValue(User.class);
                            if (user != null) {
                                userList.add(user);
                            }
                        } else {
                            Log.w("FindMatchFragment", "Ungültiger Eintrag bei UID: " + userSnap.getKey());
                        }
                    } catch (Exception e) {
                        Log.e("FindMatchFragment", "Fehler beim Verarbeiten von UID: " + userSnap.getKey(), e);
                    }
                }
                showNextUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FindMatchFragment", "Database error: " + error.getMessage());
            }
        });
    }

    //deint dazu die Daten des gezogenen Nuters auszulesen
    private void showNextUser() {
        if (userList.size() == 0) return;

        //zum Testen wird ein zufälliger Nutzer genommen
        currentIndex = (currentIndex + 1) % userList.size();
        User user = userList.get(currentIndex);

        // getString() wird genutzt um die Platzhalter in den Strings aus den Ressourcen zu ersetzen
        nameText.setText(getString(R.string.label_name, user.name));
        genderText.setText(getString(R.string.label_gender, user.gender));
        hometownText.setText(getString(R.string.label_hometown, user.homeTown));
        jobTitleText.setText(getString(R.string.label_job_title, user.jobTitle));
        universityText.setText(getString(R.string.label_university, user.university));
        languagesText.setText(getString(R.string.label_languages, user.languages));
        sexualityText.setText(getString(R.string.label_sexuality, user.sexuality));
        workplaceText.setText(getString(R.string.label_workplace, user.workplace));
        bioText.setText(getString(R.string.label_bio, user.bio));
    }

    private void saveSwipe(String swipedUserId, String swipeType) {
        if (currentUser == null || swipedUserId == null || swipeType == null) return;

        DatabaseReference swipeRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(currentUser.getUid())
                .child("swipes")
                .child(swipedUserId);

        // Werte vorbereiten
        HashMap<String, Object> swipeData = new HashMap<>();
        swipeData.put("swipeType", swipeType);
        swipeData.put("timestamp", System.currentTimeMillis() / 1000); // Unix-Zeit

        swipeRef.setValue(swipeData)
                .addOnSuccessListener(aVoid -> Log.d("Swipe", "Swipe gespeichert: " + swipeType))
                .addOnFailureListener(e -> Log.e("Swipe", "Fehler beim Speichern des Swipes", e));
    }
}

