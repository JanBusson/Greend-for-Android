package com.example.greendr;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {

    public LoginFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        EditText inputName = view.findViewById(R.id.input_name);
        EditText inputPassword = view.findViewById(R.id.input_password);
        Button loginButton = view.findViewById(R.id.button_login);
        Button gobackButton = view.findViewById(R.id.button_back);

        loginButton.setOnClickListener(v -> {
            String name = inputName.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            //Check ob ausgefüllt
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), "Bitte alle Felder ausfüllen!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Hier kannst du später echte Prüfung hinzufügen
            if (checkLogin(name, password)) {
                Toast.makeText(getContext(), "Login erfolgreich!", Toast.LENGTH_SHORT).show();
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).openFragment(new WelcomeFragment());
                }
            } else {
                Toast.makeText(getContext(), "Login fehlgeschlagen!", Toast.LENGTH_SHORT).show();
            }
        });

        gobackButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    // Dummy-Login-Check
    private boolean checkLogin(String name, String password) {
        return name.equals("admin") && password.equals("1234");
    }
}