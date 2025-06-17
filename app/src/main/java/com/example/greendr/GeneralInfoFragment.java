package com.example.greendr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeneralInfoFragment extends Fragment {

    private EditText inputName, inputHomeTown, inputWorkplace, inputJobTitle, inputUniversity, inputBio;
    private RadioGroup radioGroupGender, radioGroupSexuality;
    private CheckBox checkboxEnglish, checkboxSpanish, checkboxFrench, checkboxGerman;
    private Button buttonFinish;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_general_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        inputName = view.findViewById(R.id.input_name);
        inputHomeTown = view.findViewById(R.id.input_home_town);
        inputWorkplace = view.findViewById(R.id.input_workplace);
        inputJobTitle = view.findViewById(R.id.input_job_title);
        inputUniversity = view.findViewById(R.id.input_university);
        inputBio = view.findViewById(R.id.input_bio);

        radioGroupGender = view.findViewById(R.id.radio_group_gender);
        radioGroupSexuality = view.findViewById(R.id.radio_group_sexuality);

        checkboxEnglish = view.findViewById(R.id.checkbox_english);
        checkboxSpanish = view.findViewById(R.id.checkbox_spanish);
        checkboxFrench = view.findViewById(R.id.checkbox_french);
        checkboxGerman = view.findViewById(R.id.checkbox_german);

        buttonFinish = view.findViewById(R.id.button_finish);

        buttonFinish.setOnClickListener(v -> saveData());
    }

    private void saveData() {
        String name = inputName.getText().toString();
        String homeTown = inputHomeTown.getText().toString();
        String workplace = inputWorkplace.getText().toString();
        String jobTitle = inputJobTitle.getText().toString();
        String university = inputUniversity.getText().toString();
        String bio = inputBio.getText().toString();

        String gender = "";
        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        if (selectedGenderId != -1) {
            RadioButton selectedGender = getView().findViewById(selectedGenderId);
            gender = selectedGender.getText().toString();
        }

        String sexuality = "";
        int selectedSexualityId = radioGroupSexuality.getCheckedRadioButtonId();
        if (selectedSexualityId != -1) {
            RadioButton selectedSexuality = getView().findViewById(selectedSexualityId);
            sexuality = selectedSexuality.getText().toString();
        }

        StringBuilder languages = new StringBuilder();
        if (checkboxEnglish.isChecked()) languages.append("English,");
        if (checkboxSpanish.isChecked()) languages.append("Spanish,");
        if (checkboxFrench.isChecked()) languages.append("French,");
        if (checkboxGerman.isChecked()) languages.append("German,");

        User user = new User(name, gender, sexuality, languages.toString(), homeTown, workplace, jobTitle, university, bio);

        if (currentUser != null) {
            databaseReference.child(currentUser.getUid()).setValue(user).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Data saved successfully", Toast.LENGTH_SHORT).show();
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).openFragment(new EcoTestFragment());
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to save data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static class User {
        public String name, gender, sexuality, languages, homeTown, workplace, jobTitle, university, bio;

        public User() {}

        public User(String name, String gender, String sexuality, String languages,
                    String homeTown, String workplace, String jobTitle, String university, String bio) {
            this.name = name;
            this.gender = gender;
            this.sexuality = sexuality;
            this.languages = languages;
            this.homeTown = homeTown;
            this.workplace = workplace;
            this.jobTitle = jobTitle;
            this.university = university;
            this.bio = bio;
        }
    }
}
