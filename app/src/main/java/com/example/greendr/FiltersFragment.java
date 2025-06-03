package com.example.greendr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.List;

public class FiltersFragment extends Fragment {

    private RangeSlider ageSlider;
    private CheckBox maleCheckbox, femaleCheckbox, nonBinaryCheckbox;
    private CheckBox interestedMenCheckbox, interestedWomenCheckbox, interestedNBCheckbox;
    private CheckBox drugsYesCheckbox, drugsNoCheckbox, drugsSometimesCheckbox;
    private ChipGroup chipGroupLanguages, chipGroupHobbies;
    private Button applyButton, resetButton;

    public FiltersFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filters, container, false);
        initViews(view);
        setupListeners();
        return view;
    }

    private void initViews(View view) {
        // Slider
        ageSlider = view.findViewById(R.id.slider_age);
        ageSlider.setValues(25f, 35f);

        // Gender CheckBoxes
        maleCheckbox = view.findViewById(R.id.checkbox_male);
        femaleCheckbox = view.findViewById(R.id.checkbox_female);
        nonBinaryCheckbox = view.findViewById(R.id.checkbox_nonbinary);

        // Interested in CheckBoxes
        interestedMenCheckbox = view.findViewById(R.id.checkbox_interested_men);
        interestedWomenCheckbox = view.findViewById(R.id.checkbox_interested_women);
        interestedNBCheckbox = view.findViewById(R.id.checkbox_interested_nb);

        // Drugs CheckBoxes
        drugsYesCheckbox = view.findViewById(R.id.checkbox_drugs_yes);
        drugsNoCheckbox = view.findViewById(R.id.checkbox_drugs_no);
        drugsSometimesCheckbox = view.findViewById(R.id.checkbox_drugs_sometimes);

        // ChipGroups
        chipGroupLanguages = view.findViewById(R.id.chipgroup_languages);
        chipGroupHobbies = view.findViewById(R.id.chipgroup_hobbies);

        // Buttons
        applyButton = view.findViewById(R.id.button_apply);
        resetButton = view.findViewById(R.id.button_reset);
    }

    private void setupListeners() {
        applyButton.setOnClickListener(v -> applyFilters());
        resetButton.setOnClickListener(v -> resetFilters());
    }

    private void applyFilters() {
        // Age Range
        float minAge = ageSlider.getValues().get(0);
        float maxAge = ageSlider.getValues().get(1);

        // Gender
        List<String> genders = new ArrayList<>();
        if (maleCheckbox.isChecked()) genders.add("Male");
        if (femaleCheckbox.isChecked()) genders.add("Female");
        if (nonBinaryCheckbox.isChecked()) genders.add("Non-binary");

        // Interested in
        List<String> interests = new ArrayList<>();
        if (interestedMenCheckbox.isChecked()) interests.add("Men");
        if (interestedWomenCheckbox.isChecked()) interests.add("Women");
        if (interestedNBCheckbox.isChecked()) interests.add("Non-binary");

        // Drug use
        List<String> drugPrefs = new ArrayList<>();
        if (drugsYesCheckbox.isChecked()) drugPrefs.add("Yes");
        if (drugsNoCheckbox.isChecked()) drugPrefs.add("No");
        if (drugsSometimesCheckbox.isChecked()) drugPrefs.add("Sometimes");

        // Languages
        List<String> selectedLanguages = getSelectedChips(chipGroupLanguages);

        // Hobbies
        List<String> selectedHobbies = getSelectedChips(chipGroupHobbies);

        // >>>> Hier kannst du deine Filter weiterverarbeiten oder an eine ViewModel-Klasse senden
        // Beispiel: Log oder Toast (nur zum Debuggen)
        System.out.println("Age: " + minAge + "-" + maxAge);
        System.out.println("Gender: " + genders);
        System.out.println("Interested in: " + interests);
        System.out.println("Drugs: " + drugPrefs);
        System.out.println("Languages: " + selectedLanguages);
        System.out.println("Hobbies: " + selectedHobbies);
    }

    private void resetFilters() {
        // Reset Slider
        ageSlider.setValues(25f, 35f);

        // Reset Checkboxes
        CheckBox[] checkboxes = {
                maleCheckbox, femaleCheckbox, nonBinaryCheckbox,
                interestedMenCheckbox, interestedWomenCheckbox, interestedNBCheckbox,
                drugsYesCheckbox, drugsNoCheckbox, drugsSometimesCheckbox
        };

        for (CheckBox cb : checkboxes) {
            cb.setChecked(false);
        }

        // Reset ChipGroups
        clearChips(chipGroupLanguages);
        clearChips(chipGroupHobbies);
    }

    private List<String> getSelectedChips(ChipGroup group) {
        List<String> selected = new ArrayList<>();
        for (int i = 0; i < group.getChildCount(); i++) {
            Chip chip = (Chip) group.getChildAt(i);
            if (chip.isChecked()) {
                selected.add(chip.getText().toString());
            }
        }
        return selected;
    }

    private void clearChips(ChipGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            ((Chip) group.getChildAt(i)).setChecked(false);
        }
    }
}
