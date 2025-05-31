package com.example.greendr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StartFragment extends Fragment {

    public StartFragment() {
        // Leerer Konstruktor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_start, container, false);

        Button openSecondBtn = view.findViewById(R.id.button_open_second);
        Button registerBtn = view.findViewById(R.id.button_register);
        Button helpBtn = view.findViewById(R.id.button_help);

        openSecondBtn.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openFragment(new LoginFragment());
            }
        });

        registerBtn.setOnClickListener(v -> {
//            if (getActivity() instanceof MainActivity) {
//                ((MainActivity) getActivity()).openFragment(new RegisterFragment());
//            }
        });

        helpBtn.setOnClickListener(v -> {
//            if (getActivity() instanceof MainActivity) {
//                ((MainActivity) getActivity()).openFragment(new PasswordRecoveryFragment());
//            }
        });

        return view;
    }
}
