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
        Button filterBtn = view.findViewById(R.id.button_filter);
        Button mymatchesBtn = view.findViewById(R.id.button_liked);

//        Wird bei den Matches angezeigt
//        Button chatBtn = view.findViewById(R.id.button_chat);
        Button logoutBtn = view.findViewById(R.id.button_logout);

        matchBtn.setOnClickListener(v -> {
            // TODO: MatchActivity starten
            Log.d("WelcomeFragment", "Match finden geklickt");
        });

        //TODO: Filter durchgehen und schauen dass alle Elemente "Sinnvolll sind"
        filterBtn.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openFragment(new FiltersFragment());
            }
        });

        mymatchesBtn.setOnClickListener(v -> {
//            if (getActivity() instanceof MainActivity) {
//                ((MainActivity) getActivity()).openFragment(new MyMatchesFragment());
//            }
        });

//        siehe oben
//        chatBtn.setOnClickListener(v -> {
//            // TODO: ChatActivity starten
//            Log.d("WelcomeFragment", "Chatten geklickt");
//        });

        //Ausloggen
        logoutBtn.setOnClickListener(v -> {
            //Ausloggen aus Firebase Auth
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(requireActivity(), MainActivity.class);
            //schließt alle im Backstack bis bei Main Activity angekommen
            //MainActivity auf einen neuen Task (neuer Stapel)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //schließt activity komplett
            requireActivity().finish();
        });

        return view;
    }
}