package com.example.greendr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class CommentsFragment extends Fragment {

    private ListView commentsList;
    private ArrayAdapter<String> adapter;
    private List<String> comments = new ArrayList<>();
    private DatabaseReference userRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        commentsList = view.findViewById(R.id.list_comments);
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, comments);
        commentsList.setAdapter(adapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Not logged in!", Toast.LENGTH_SHORT).show();
            return view;
        }

        userRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUser.getUid()).child("comments");

        loadComments();

        return view;
    }

    private void loadComments() {
        userRef.get().addOnSuccessListener(snapshot -> {
            comments.clear();
            for (DataSnapshot child : snapshot.getChildren()) {
                String text = child.child("text").getValue(String.class);
                String author = child.child("authorName").getValue(String.class);
                if (text != null && author != null) {
                    comments.add(author + ": " + text);
                }
            }
            adapter.notifyDataSetChanged();

            if (comments.isEmpty()) {
                Toast.makeText(getContext(), "No comments yet!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
            Toast.makeText(getContext(), "Failed to load comments: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }
}
