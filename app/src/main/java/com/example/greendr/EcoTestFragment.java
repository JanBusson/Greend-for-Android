package com.example.greendr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public class EcoTestFragment extends Fragment {

    private TextView questionText;
    private RadioGroup answersGroup;
    private RadioButton[] answerButtons;
    private Button continueButton, finishButton;

    private int questionIndex = 0;
    private double score = 0;

    private ArrayList<String> questions;
    private ArrayList<ArrayList<String>> answers;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_eco_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        questionText = view.findViewById(R.id.text_question);
        answersGroup = view.findViewById(R.id.radio_group_answers);
        continueButton = view.findViewById(R.id.button_continue);
        finishButton = view.findViewById(R.id.button_finish);

        answerButtons = new RadioButton[5];
        answerButtons[0] = view.findViewById(R.id.answer_1);
        answerButtons[1] = view.findViewById(R.id.answer_2);
        answerButtons[2] = view.findViewById(R.id.answer_3);
        answerButtons[3] = view.findViewById(R.id.answer_4);
        answerButtons[4] = view.findViewById(R.id.answer_5);

        initQuestions();
        showQuestion();

        continueButton.setOnClickListener(v -> {
            int selectedId = answersGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getContext(), "Bitte eine Antwort auswählen.", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i < answerButtons.length; i++) {
                if (answerButtons[i].getId() == selectedId) {
                    score += 10 - (i * 2.5);
                    break;
                }
            }
            questionIndex++;
            if (questionIndex < questions.size()) {
                showQuestion();
            } else {
                continueButton.setEnabled(false);
                finishButton.setVisibility(View.VISIBLE);
            }
        });

        finishButton.setOnClickListener(v -> {
            String message = getFinalMessage(score);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                DatabaseReference ref = FirebaseDatabase.getInstance()
                        .getReference("Users")
                        .child(user.getUid())
                        .child("ecoScore");

                ref.setValue(score).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Dein Score: " + score + "\n" + message, Toast.LENGTH_LONG).show();
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).openFragment(new WelcomeFragment());
                        }
                    } else {
                        Toast.makeText(getContext(), "Fehler beim Speichern des Scores", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showQuestion() {
        questionText.setText(questions.get(questionIndex));
        ArrayList<String> answerList = answers.get(questionIndex);
        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i].setText(answerList.get(i));
        }
        answersGroup.clearCheck();
    }

    private void initQuestions() {
        questions = new ArrayList<>(Arrays.asList(
                "1. How often do you use public transportation instead of driving?",
                "2. How do you typically handle your food waste?",
                "3. How energy-efficient is your home?",
                "4. How often do you purchase second-hand or recycled products?",
                "5. What is your approach to water conservation?",
                "6. How do you handle recycling at home?",
                "7. How often do you choose sustainable or eco-friendly brands when shopping?",
                "8. How do you manage your energy consumption at home?",
                "9. How do you handle clothing and textile waste?",
                "10. How frequently do you support or engage in community sustainability initiatives?"
        ));

        answers = new ArrayList<>();
        answers.add(new ArrayList<>(Arrays.asList("Every day", "A few times a week", "Occasionally", "Rarely", "Never")));
        answers.add(new ArrayList<>(Arrays.asList("Compost it", "Feed animals", "Food waste collection", "Trash", "Ignore")));
        answers.add(new ArrayList<>(Arrays.asList("Fully efficient", "Mostly efficient", "Some features", "Few features", "None")));
        answers.add(new ArrayList<>(Arrays.asList("Very frequently", "Often", "Sometimes", "Rarely", "Never")));
        answers.add(new ArrayList<>(Arrays.asList("Always save", "Usually save", "Sometimes", "Rarely", "Never")));
        answers.add(new ArrayList<>(Arrays.asList("Recycle all", "Most", "Occasionally", "Rarely", "Never")));
        answers.add(new ArrayList<>(Arrays.asList("Always", "Most of the time", "Sometimes", "Rarely", "Never")));
        answers.add(new ArrayList<>(Arrays.asList("Monitor and reduce", "Often try", "Occasionally", "Rarely", "Never")));
        answers.add(new ArrayList<>(Arrays.asList("Donate/recycle all", "Donate most", "Occasionally", "Rarely", "Never")));
        answers.add(new ArrayList<>(Arrays.asList("Very frequently", "Often", "Sometimes", "Rarely", "Never")));
    }

    private String getFinalMessage(double score) {
        if (score >= 75) {
            return "You are very sustainable. You can be proud of yourself!";
        } else if (score >= 50) {
            return "You are doing well! There's still room for improvement.";
        } else if (score >= 25) {
            return "You're on the right track. Keep improving!";
        } else {
            return "Please consider making more sustainable choices.";
        }
    }
}
