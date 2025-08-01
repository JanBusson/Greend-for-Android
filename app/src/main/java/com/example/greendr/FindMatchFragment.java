package com.example.greendr;

import android.util.Log;
import android.app.AlertDialog;
import android.widget.EditText;
import java.util.HashMap;
import java.util.Map;
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

    private TextView nameTextView, universityTextView, jobTitleTextView, hometownTextView, sexualityTextView, ageTextView;
    private Button likeButton, dislikeButton, backButton, commentButton;
    private MatchUser currentTarget;
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
        commentButton = view.findViewById(R.id.button_comment);

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
        commentButton.setOnClickListener(v -> openCommentDialog());
    }

    private void openCommentDialog() {
        if (currentTarget == null) {
            Toast.makeText(getContext(), "Kein aktiver Nutzer für Kommentar!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Comment");

        final EditText input = new EditText(getContext());
        input.setHint("Write your comment...");
        builder.setView(input);

        builder.setPositiveButton("Post", (dialog, which) -> {
            String commentText = input.getText().toString().trim();
            if (commentText.isEmpty()) {
                Toast.makeText(getContext(), "Comment cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            postComment(currentTarget.uid, commentText);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void postComment(String targetUid, String commentText) {
        DatabaseReference commentRef = userRef.child(targetUid).child("comments").push();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        Map<String, Object> commentData = new HashMap<>();
        commentData.put("text", commentText);
        commentData.put("authorId", currentUid);
        commentData.put("authorName", currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Unknown");
        commentData.put("timestamp", System.currentTimeMillis());

        commentRef.setValue(commentData)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Comment posted!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadCandidates() {
    userRef.get().addOnSuccessListener(snapshot -> {
        DataSnapshot me = snapshot.child(currentUid);
        Double myEco = me.child("ecoScore").getValue(Double.class);
        Double mySocial = me.child("socialScore").getValue(Double.class);

        Log.d("DEBUG", "Current UID: " + currentUid);
        Log.d("DEBUG", "My ecoScore: " + myEco + " | My socialScore: " + mySocial);

        if (myEco == null || mySocial == null) {
            Toast.makeText(getContext(), "Complete your profile first!", Toast.LENGTH_SHORT).show();
            Log.w("DEBUG", "Aborting: ecoScore or socialScore is missing for current user.");
            return;
        }

        long likesCount = me.child("likes").getChildrenCount();
        long dislikesCount = me.child("disliked").getChildrenCount();
        Log.d("DEBUG", "Current user likes count: " + likesCount + " | dislikes count: " + dislikesCount);

        candidates.clear();

        for (DataSnapshot child : snapshot.getChildren()) {
            String uid = child.getKey();
            if (uid.equals(currentUid)) {
                Log.d("DEBUG", "Skipping self: " + uid);
                continue;
            }

            boolean liked = me.child("likes").exists() && me.child("likes").hasChild(uid);
            boolean disliked = me.child("disliked").exists() && me.child("disliked").hasChild(uid);

            Log.d("DEBUG", "Checking candidate: " + uid + " | liked=" + liked + " | disliked=" + disliked);

            if (liked || disliked) {
                Log.d("DEBUG", "Skipping candidate due to like/dislike: " + uid);
                continue;
            }

            // Retrieve candidate data
            Double eco = child.child("ecoScore").getValue(Double.class);
            Double social = child.child("socialScore").getValue(Double.class);
            String name = child.child("name").getValue(String.class);

            // Age: handle both Long and String
            Long age = null;
            if (child.child("age").getValue() instanceof Long) {
                age = child.child("age").getValue(Long.class);
            } else if (child.child("age").getValue() instanceof String) {
                try { age = Long.parseLong(child.child("age").getValue(String.class)); }
                catch (Exception e) { Log.w("DEBUG", "Invalid age format for UID: " + uid); }
            }

            Log.d("DEBUG", "Candidate data: eco=" + eco + ", social=" + social + ", name=" + name + ", age=" + age);

            // Skip incomplete profiles
            if (eco == null || social == null || name == null || age == null) {
                Log.d("DEBUG", "Skipping incomplete profile: " + uid);
                continue;
            }

            // Calculate match score
            double matchScore = 0.6 * Math.abs(myEco - eco) + 0.4 * Math.abs(mySocial - social);
            candidates.add(new MatchUser(uid, name, null, null, null, null, age, matchScore));
            Log.d("DEBUG", "Candidate added: " + uid + " | matchScore=" + matchScore);
        }

        Log.d("DEBUG", "Total candidates loaded: " + candidates.size());
        
        Collections.sort(candidates, (a, b) -> Double.compare(a.score, b.score));
        showNext();
    });}

    private void showNext() {
        if (currentIndex < candidates.size()) {
            currentTarget = candidates.get(currentIndex);
            MatchUser current = currentTarget;
            nameTextView.setText(current.name);
            universityTextView.setText("University: " + (current.university != null ? current.university : "N/A"));
            jobTitleTextView.setText("Job: " + (current.jobTitle != null ? current.jobTitle : "N/A"));
            hometownTextView.setText("From: " + (current.homeTown != null ? current.homeTown : "N/A"));
            sexualityTextView.setText("Sexuality: " + (current.sexuality != null ? current.sexuality : "N/A"));
            ageTextView.setText("Age: " + current.age);
        } else {
            currentTarget = null;
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

        String matchId = FirebaseDatabase.getInstance().getReference("Matches").push().getKey();
        if (matchId != null) {
            DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference("Matches").child(matchId);
            matchRef.setValue(new Match(currentUid, target.uid, "dislike", System.currentTimeMillis()));
        }

        currentIndex++;
        showNext();
    }

    public static class MatchUser {
        public String uid, name, university, jobTitle, homeTown, sexuality;
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
        public String user1, user2, status;
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
