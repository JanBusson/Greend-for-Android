package com.example.greendr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Collections;
import java.util.List;

public class FindMatchFragment extends Fragment {

    private TextView nameTextView;
    private TextView universityTextView;
    private TextView jobTitleTextView;
    private TextView hometownTextView;
    private TextView sexualityTextView;
    private TextView ageTextView;
    private Button likeButton, dislikeButton, backButton;

    private List<MatchUser> candidates = new ArrayList<>();
    private int currentIndex = 0;
    private String currentUid;

    private DatabaseReference userRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_match, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameTextView = view.findViewById(R.id.text_name);
        universityTextView = view.findViewById(R.id.text_university);
        jobTitleTextView = view.findViewById(R.id.text_job_title);
        hometownTextView = view.findViewById(R.id.text_home_town);
        sexualityTextView = view.findViewById(R.id.text_sexuality);
        ageTextView = view.findViewById(R.id.text_age);

        likeButton = view.findViewById(R.id.button_like);
        dislikeButton = view.findViewById(R.id.button_dislike);
        backButton = view.findViewById(R.id.button_back_to_welcome);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;
        currentUid = currentUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        loadCandidates();

        likeButton.setOnClickListener(v -> handleLike());
        dislikeButton.setOnClickListener(v -> handleDislike());
        backButton.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openFragment(new WelcomeFragment());
            }
        });
    }

    private void loadCandidates() {
        userRef.get().addOnSuccessListener(snapshot -> {
            DataSnapshot me = snapshot.child(currentUid);
            Double myEco = me.child("ecoScore").getValue(Double.class);
            Double mySocial = me.child("socialScore").getValue(Double.class);

            if (myEco == null || mySocial == null) {
                Toast.makeText(getContext(), "Bitte vervollständige dein Profil zuerst!", Toast.LENGTH_SHORT).show();
                return; // verhindert Crash
            }


            candidates.clear();

            for (DataSnapshot child : snapshot.getChildren()) {
                String uid = child.getKey();
                if (uid.equals(currentUid)) continue;
                if (me.child("likes").hasChild(uid) || me.child("disliked").hasChild(uid)) continue;

                Double eco = child.child("ecoScore").getValue(Double.class);
                Double social = child.child("socialScore").getValue(Double.class);
                String name = child.child("name").getValue(String.class);
                String university = child.child("university").getValue(String.class);
                String jobTitle = child.child("jobTitle").getValue(String.class);
                String homeTown = child.child("homeTown").getValue(String.class);
                String sexuality = child.child("sexuality").getValue(String.class);

                // age kommt als String, also konvertieren
                Long age = null;
                try {
                    age = Long.parseLong(child.child("age").getValue(String.class));
                } catch (Exception ignored) {}

                if (eco == null || social == null || name == null || age == null) continue;

                double matchScore = 0.6 * Math.abs(myEco - eco) + 0.4 * Math.abs(mySocial - social);
                candidates.add(new MatchUser(uid, name, university, jobTitle, homeTown, sexuality, age, matchScore));
            }

            Collections.sort(candidates, (a, b) -> Double.compare(a.score, b.score));
            showNext();
        });
    }

    private void showNext() {
        if (currentIndex < candidates.size()) {
            MatchUser current = candidates.get(currentIndex);
            nameTextView.setText(current.name);
            universityTextView.setText("University: " + (current.university != null ? current.university : "N/A"));
            jobTitleTextView.setText("Job: " + (current.jobTitle != null ? current.jobTitle : "N/A"));
            hometownTextView.setText("From: " + (current.homeTown != null ? current.homeTown : "N/A"));
            sexualityTextView.setText("Sexuality: " + (current.sexuality != null ? current.sexuality : "N/A"));
            ageTextView.setText("Age: " + current.age);
        } else {
            nameTextView.setText("Keine weiteren Vorschläge");
            universityTextView.setText("");
            jobTitleTextView.setText("");
            hometownTextView.setText("");
            sexualityTextView.setText("");
            ageTextView.setText("");
        }
    }

    private void updateSocialScore(String targetUid) {
        userRef.child(targetUid).get().addOnSuccessListener(snapshot -> {
            Long likes = snapshot.child("likedByCount").getValue(Long.class);
            Long dislikes = snapshot.child("dislikedByCount").getValue(Long.class);
            double s0 = 10;
            double a = 1.0;
            double b = 0.5;

            double score = s0 + a * (likes != null ? likes : 0) - b * (dislikes != null ? dislikes : 0);
            userRef.child(targetUid).child("socialScore").setValue(score);
        });
    }

    private void handleLike() {
        if (currentIndex >= candidates.size()) return;
        MatchUser target = candidates.get(currentIndex);
        userRef.child(currentUid).child("likes").child(target.uid).setValue(true);

        userRef.child(target.uid).child("likedByCount").get().addOnSuccessListener(snapshot -> {
            long count = snapshot.exists() ? snapshot.getValue(Long.class) : 0;
            userRef.child(target.uid).child("likedByCount").setValue(count + 1);
            updateSocialScore(target.uid);
        });

        userRef.child(target.uid).child("likes").child(currentUid).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                userRef.child(currentUid).child("matches").child(target.uid).setValue(true);
                userRef.child(target.uid).child("matches").child(currentUid).setValue(true);
                Toast.makeText(getContext(), "It's a Match!", Toast.LENGTH_SHORT).show();
            }
        });

        // Save Match (LIKE) in /Matches
        String matchId = FirebaseDatabase.getInstance().getReference("Matches").push().getKey();
        if (matchId != null) {
            DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference("Matches").child(matchId);
            matchRef.setValue(new Match(currentUid, target.uid, "like", System.currentTimeMillis()));
        }

        currentIndex++;
        showNext();
    }

    private void handleDislike() {
        if (currentIndex >= candidates.size()) return;
        MatchUser target = candidates.get(currentIndex);
        userRef.child(currentUid).child("disliked").child(target.uid).setValue(true);

        userRef.child(target.uid).child("dislikedByCount").get().addOnSuccessListener(snapshot -> {
            long count = snapshot.exists() ? snapshot.getValue(Long.class) : 0;
            userRef.child(target.uid).child("dislikedByCount").setValue(count + 1);
            updateSocialScore(target.uid);
        });

        // Save Match (DISLIKE) in /Matches
        String matchId = FirebaseDatabase.getInstance().getReference("Matches").push().getKey();
        if (matchId != null) {
            DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference("Matches").child(matchId);
            matchRef.setValue(new Match(currentUid, target.uid, "dislike", System.currentTimeMillis()));
        }

        currentIndex++;
        showNext();
    }

    public static class MatchUser {
        public String uid;
        public String name;
        public String university;
        public String jobTitle;
        public String homeTown;
        public String sexuality;
        public Long age;
        public double score;

        public MatchUser(String uid, String name, String university, String jobTitle, String homeTown,
                         String sexuality, Long age, double score) {
            this.uid = uid;
            this.name = name;
            this.university = university;
            this.jobTitle = jobTitle;
            this.homeTown = homeTown;
            this.sexuality = sexuality;
            this.age = age;
            this.score = score;
        }
    }

    public static class Match {
        public String user1;
        public String user2;
        public String status;
        public long timestamp;

        public Match() {}

        public Match(String user1, String user2, String status, long timestamp) {
            this.user1 = user1;
            this.user2 = user2;
            this.status = status;
            this.timestamp = timestamp;
        }
    }
}
