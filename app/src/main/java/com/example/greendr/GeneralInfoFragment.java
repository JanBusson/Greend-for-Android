package com.example.greendr;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

import java.util.HashMap;
import java.util.Map;

public class GeneralInfoFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseUser user;
    private DatabaseReference userRef;

    private EditText inputName, inputAge, inputHomeTown, inputWorkplace, inputJobTitle, inputUniversity, inputBio;
    private RadioGroup radioGroupGender, radioGroupSexuality;
    private CheckBox checkboxEnglish, checkboxSpanish, checkboxFrench, checkboxGerman;
    private Button buttonFinish;
    private ImageView imageProfile;
    private Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_general_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        }

        inputName = view.findViewById(R.id.input_name);
        inputAge = view.findViewById(R.id.input_age);
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
        imageProfile = view.findViewById(R.id.image_profile);

        imageProfile.setOnClickListener(v -> openImagePicker());

        buttonFinish.setOnClickListener(v -> {
            if (imageUri != null) {
                saveToFirebase(imageUri.toString());
            } else {
                Toast.makeText(getContext(), "Bitte ein Profilbild auswählen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Profilbild auswählen"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageProfile.setImageURI(imageUri);
        }
    }

    private void saveToFirebase(String imagePath) {
        if (userRef == null) {
            Toast.makeText(getContext(), "Kein Benutzer eingeloggt", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("name", inputName.getText().toString());
        data.put("age", inputAge.getText().toString());
        data.put("profileImageUri", imagePath);
        data.put("homeTown", inputHomeTown.getText().toString());
        data.put("workplace", inputWorkplace.getText().toString());
        data.put("jobTitle", inputJobTitle.getText().toString());
        data.put("university", inputUniversity.getText().toString());
        data.put("bio", inputBio.getText().toString());

        int genderId = radioGroupGender.getCheckedRadioButtonId();
        if (genderId != -1) {
            RadioButton selectedGender = getView().findViewById(genderId);
            data.put("gender", selectedGender.getText().toString());
        }

        int sexualityId = radioGroupSexuality.getCheckedRadioButtonId();
        if (sexualityId != -1) {
            RadioButton selectedSexuality = getView().findViewById(sexualityId);
            data.put("sexuality", selectedSexuality.getText().toString());
        }

        StringBuilder languages = new StringBuilder();
        if (checkboxEnglish.isChecked()) languages.append("English,");
        if (checkboxSpanish.isChecked()) languages.append("Spanish,");
        if (checkboxFrench.isChecked()) languages.append("French,");
        if (checkboxGerman.isChecked()) languages.append("German,");
        data.put("languages", languages.toString());

        userRef.updateChildren(data)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Information gespeichert", Toast.LENGTH_SHORT).show();
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).openFragment(new EcoTestFragment());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Fehler beim Speichern: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
