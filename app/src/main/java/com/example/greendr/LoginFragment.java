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

import com.google.firebase.auth.FirebaseAuth;

/*##################################################
Ermöglich User Login mit Firebase
##################################################*/
public class LoginFragment extends Fragment {

    //Firebase Authentication für die Verwaltung

    public LoginFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        EditText inputEmail = view.findViewById(R.id.input_email);
        EditText inputPassword = view.findViewById(R.id.input_password);
        Button loginButton = view.findViewById(R.id.button_login);
        Button gobackButton = view.findViewById(R.id.button_back);

        //Instanz der Firebase Authentifizierung
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //auslesen der Eingabefelder
        loginButton.setOnClickListener(v -> {
            String mail = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            //Check ob ausgefüllt
            if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), "Bitte alle Felder ausfüllen!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase-Login
            mAuth.signInWithEmailAndPassword(mail, password)
                    //Complete Listener gibt eine Rückmeldung ob das Login erfolgreich war
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), R.string.message_loginSuccess, Toast.LENGTH_SHORT).show();
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).openFragment(new WelcomeFragment());
                            }
                        } else {
                            Toast.makeText(getContext(), getString(R.string.message_loginError) + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        gobackButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

}