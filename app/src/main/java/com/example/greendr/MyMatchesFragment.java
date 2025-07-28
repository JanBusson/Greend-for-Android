package com.example.greendr;
/*##################################################
Zeigt die jeweiigen Matches and
##################################################*/

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MyMatchesFragment extends Fragment {

    private LinearLayout matchesContainer;
    private Button backButton;
    private DatabaseReference userRef;
    private String currentUid;
    private List<MatchUser> matchesList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_matches, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        matchesContainer = view.findViewById(R.id.matches_container);
        backButton = view.findViewById(R.id.button_back_to_welcome);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;
        currentUid = currentUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        loadMatches();

        backButton.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openFragment(new WelcomeFragment());
            }
        });
    }

    private void loadMatches() {
        userRef.child(currentUid).child("matches").get().addOnSuccessListener(snapshot -> {
            matchesList.clear();
            matchesContainer.removeAllViews();

            for (DataSnapshot matchSnap : snapshot.getChildren()) {
                String matchUid = matchSnap.getKey();
                userRef.child(matchUid).get().addOnSuccessListener(userSnap -> {
                    String name = userSnap.child("name").getValue(String.class);
                    String university = userSnap.child("university").getValue(String.class);
                    String jobTitle = userSnap.child("jobTitle").getValue(String.class);
                    String homeTown = userSnap.child("homeTown").getValue(String.class);
                    String sexuality = userSnap.child("sexuality").getValue(String.class);
                    Long age = null;
                    try {
                        age = Long.parseLong(userSnap.child("age").getValue(String.class));
                    } catch (Exception ignored) {}

                    MatchUser matchUser = new MatchUser(matchUid, name, university, jobTitle, homeTown, sexuality, age);
                    matchesList.add(matchUser);
                    addMatchView(matchUser);
                });
            }

            if (!snapshot.hasChildren()) {
                Toast.makeText(getContext(), "Keine Matches gefunden", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMatchView(MatchUser match) {
        View matchView = LayoutInflater.from(getContext()).inflate(R.layout.item_match, matchesContainer, false);

        TextView nameText = matchView.findViewById(R.id.text_match_name);
        TextView detailsText = matchView.findViewById(R.id.text_match_details);
        Button removeButton = matchView.findViewById(R.id.button_remove_match);

        nameText.setText(match.name != null ? match.name : "Unbekannt");
        detailsText.setText((match.age != null ? "Alter: " + match.age : "") +
                "\nUni: " + (match.university != null ? match.university : "N/A") +
                "\nJob: " + (match.jobTitle != null ? match.jobTitle : "N/A"));

        removeButton.setOnClickListener(v -> {
            userRef.child(currentUid).child("matches").child(match.uid).removeValue();
            matchesContainer.removeView(matchView);
            Toast.makeText(getContext(), match.name + " entfernt", Toast.LENGTH_SHORT).show();
        });

        matchesContainer.addView(matchView);
    }

    public static class MatchUser {
        public String uid;
        public String name;
        public String university;
        public String jobTitle;
        public String homeTown;
        public String sexuality;
        public Long age;

        public MatchUser(String uid, String name, String university, String jobTitle, String homeTown, String sexuality, Long age) {
            this.uid = uid;
            this.name = name;
            this.university = university;
            this.jobTitle = jobTitle;
            this.homeTown = homeTown;
            this.sexuality = sexuality;
            this.age = age;
        }
    }
}
