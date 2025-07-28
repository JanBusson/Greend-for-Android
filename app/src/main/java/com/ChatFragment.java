package com.example.greendr;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatFragment extends Fragment {

    private LinearLayout chatContainer;
    private ScrollView chatScroll;
    private EditText inputMessage;
    private Button sendButton, backButton;

    private DatabaseReference chatRef;
    private String currentUid;
    private String matchUid;
    private String chatId;

    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatContainer = view.findViewById(R.id.chat_container);
        chatScroll = view.findViewById(R.id.chat_scroll);
        inputMessage = view.findViewById(R.id.input_message);
        sendButton = view.findViewById(R.id.button_send);
        backButton = view.findViewById(R.id.button_back);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        currentUid = user.getUid();

        // Match UID wird über Bundle übergeben
        if (getArguments() != null) {
            matchUid = getArguments().getString("matchUid");
        }

        if (matchUid == null) {
            Toast.makeText(getContext(), "No match selected!", Toast.LENGTH_SHORT).show();
            return;
        }

        chatId = generateChatId(currentUid, matchUid);
        chatRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatId);

        loadMessages();

        sendButton.setOnClickListener(v -> sendMessage());
        backButton.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openFragment(new MyMatchesFragment());
            }
        });
    }

    private String generateChatId(String uid1, String uid2) {
        // Immer gleiche ID unabhängig von Sender/Empfänger
        return uid1.compareTo(uid2) < 0 ? uid1 + "_" + uid2 : uid2 + "_" + uid1;
    }

    private void loadMessages() {
        chatRef.child("chatlog").get().addOnSuccessListener(snapshot -> {
            chatContainer.removeAllViews();
            List<Message> messages = new ArrayList<>();

            for (DataSnapshot msgSnap : snapshot.getChildren()) {
                Message msg = msgSnap.getValue(Message.class);
                if (msg != null) messages.add(msg);
            }

            // Nach Timestamp sortieren
            Collections.sort(messages, (a, b) -> Long.compare(a.timestamp, b.timestamp));

            for (Message msg : messages) {
                addMessageToUI(msg);
            }

            chatScroll.post(() -> chatScroll.fullScroll(View.FOCUS_DOWN));
        });
    }

    private void sendMessage() {
        String text = inputMessage.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        Message msg = new Message(currentUid, text, System.currentTimeMillis());
        chatRef.child("chatlog").push().setValue(msg)
                .addOnSuccessListener(aVoid -> {
                    inputMessage.setText("");
                    addMessageToUI(msg);
                    chatScroll.post(() -> chatScroll.fullScroll(View.FOCUS_DOWN));
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to send!", Toast.LENGTH_SHORT).show());
    }

    private void addMessageToUI(Message msg) {
        TextView msgView = new TextView(getContext());
        String time = timeFormat.format(new Date(msg.timestamp));
        if (msg.sender.equals(currentUid)) {
            msgView.setText("Me (" + time + "): " + msg.text);
            msgView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        } else {
            msgView.setText("Them (" + time + "): " + msg.text);
            msgView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }
        chatContainer.addView(msgView);
    }

    public static class Message {
        public String sender;
        public String text;
        public long timestamp;

        public Message() { }
        public Message(String sender, String text, long timestamp) {
            this.sender = sender;
            this.text = text;
            this.timestamp = timestamp;
        }
    }
}
