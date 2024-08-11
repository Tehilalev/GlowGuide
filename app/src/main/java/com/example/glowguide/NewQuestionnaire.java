package com.example.glowguide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class NewQuestionnaire extends AppCompatActivity {

    private FirebaseFirestore fStore;
    private FirebaseUser user;

    private RadioGroup q1Group, q2Group, q3Group, q5Group, q6Group, q7Group, q8Group;
    private CheckBox q4_acne_pimples, q4_redness, q4_irritation_shaving, q4_none;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_questionnaire);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        q1Group = findViewById(R.id.q1Group);
        q2Group = findViewById(R.id.q2Group);
        q3Group = findViewById(R.id.q3Group);
        q4_acne_pimples = findViewById(R.id.q4_acne_pimples);
        q4_redness = findViewById(R.id.q4_redness);
        q4_irritation_shaving = findViewById(R.id.q4_irritation_shaving);
        q4_none = findViewById(R.id.q4_none);
        q5Group = findViewById(R.id.q5Group);
        q6Group = findViewById(R.id.q6Group);
        q7Group = findViewById(R.id.q7Group);
        q8Group = findViewById(R.id.q8Group);
        Button submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(v -> {
            if (areAllQuestionsAnswered()) {
                String skinType = decisionTree();
                saveResultToFirestore(skinType);
                save5AnswersToFirestore();
                startActivity((new Intent(getApplicationContext(), SkincareActivity.class)));
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Please answer all the questions.", Toast.LENGTH_SHORT).show();

            }

        });
    }


    private String getCheckboxAnswers(CheckBox... checkBoxes) {
        // StringBuilder selectedAnswers = new StringBuilder();
        int score = 0;

        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) {
                if (checkBox.getText().toString().equals("none of these")) {
                    score += 0;
                } else {
                    score += 20;
                }
                // selectedAnswers.append(checkBox.getText().toString()).append(", ");
            }
        }

        if (score == 0) {
            return "No";
        } else {
            return "Sensitive";
        }

    }


    private void save5AnswersToFirestore() {
        String q1Answer = getRadioGroupAnswers(q1Group);
        String q4Answer = getCheckboxAnswers(q4_acne_pimples, q4_redness, q4_irritation_shaving, q4_none);
        String q6Answer = getRadioGroupAnswers(q6Group);
        String q7Answer = getRadioGroupAnswers(q7Group);
        String q8Answer = getRadioGroupAnswers(q8Group);

        DocumentReference docRef = fStore.collection("users").document(user.getUid());
        Map<String, Object> answers = new HashMap<>();
        answers.put("q1", q1Answer);
        answers.put("q4", q4Answer);
        answers.put("q6", q6Answer);
        answers.put("q7", q7Answer);
        answers.put("q8", q8Answer);

        docRef.update(answers)
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> Toast.makeText(NewQuestionnaire.this, "Error saving answers. Please try again.", Toast.LENGTH_SHORT).show());

    }


    private void saveResultToFirestore(String skinType) {
        DocumentReference docRef = fStore.collection("users").document(user.getUid());
        docRef.update("skinType", skinType)
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> Toast.makeText(NewQuestionnaire.this, "Error saving skin type. Please try again.", Toast.LENGTH_SHORT).show());
    }

    private boolean areAllQuestionsAnswered() {
        boolean _q1Group = q1Group.getCheckedRadioButtonId() != -1;
        boolean _q2Group = q2Group.getCheckedRadioButtonId() != -1;
        boolean _q3Group = q3Group.getCheckedRadioButtonId() != -1;
        boolean _q5Group = q5Group.getCheckedRadioButtonId() != -1;
        boolean _q6Group = q6Group.getCheckedRadioButtonId() != -1;
        boolean _q7Group = q7Group.getCheckedRadioButtonId() != -1;
        boolean _q8Group = q8Group.getCheckedRadioButtonId() != -1;

        boolean _q4Group = q4_acne_pimples.isChecked() || q4_redness.isChecked() ||
                q4_irritation_shaving.isChecked() || q4_none.isChecked();

        return _q1Group && _q2Group && _q3Group && _q5Group && _q6Group && _q7Group && _q8Group && _q4Group;

    }

    private String getRadioGroupAnswers(RadioGroup group) {
        int selectedId = group.getCheckedRadioButtonId();

        RadioButton selectedRadioButton = findViewById(selectedId);
        if (selectedRadioButton == null) {
            Log.e("NewQuestionnaire", "Selected radio button is null.");
            return "0";
        }
        return selectedRadioButton.getText().toString();
    }

    private String decisionTree() {
        if (getRadioGroupAnswers(q2Group).equals("At least twice a day") &&
                getRadioGroupAnswers(q3Group).equals("My facial skin is dry/hard") &&
                getRadioGroupAnswers(q5Group).equals("My facial skin feels very tight")) {
            return "Dry";
        } else if (getRadioGroupAnswers(q2Group).equals("At least twice a day") &&
                getRadioGroupAnswers(q3Group).equals("My facial skin is dry/hard") &&
                getRadioGroupAnswers(q5Group).equals("My facial skin feels clean and pleasant")) {
            return "Dry";
        } else if (getRadioGroupAnswers(q2Group).equals("At least twice a day") &&
                getRadioGroupAnswers(q3Group).equals("My facial skin is dry/hard") &&
                getRadioGroupAnswers(q5Group).equals("I feel like my T-zone is dry")) {
            return "Combination";

            // 3 combinations

        } else if (getRadioGroupAnswers(q2Group).equals("At least twice a day") &&
                getRadioGroupAnswers(q3Group).equals("My facial skin is oily in certain areas") &&
                getRadioGroupAnswers(q5Group).equals("My facial skin feels very tight")) {
            return "Combination";
        } else if (getRadioGroupAnswers(q2Group).equals("At least twice a day") &&
                getRadioGroupAnswers(q3Group).equals("My facial skin is oily in certain areas") &&
                getRadioGroupAnswers(q5Group).equals("My facial skin feels clean and pleasant")) {
            return "Combination";
        } else if (getRadioGroupAnswers(q2Group).equals("At least twice a day") &&
                getRadioGroupAnswers(q3Group).equals("My facial skin is oily in certain areas") &&
                getRadioGroupAnswers(q5Group).equals("I feel like my T-zone is dry")) {
            return "Oily";

            // 3 combinations

        } else if (getRadioGroupAnswers(q2Group).equals("At least twice a day") &&
                getRadioGroupAnswers(q3Group).equals("The skin of my face is very oily") &&
                getRadioGroupAnswers(q5Group).equals("My facial skin feels very tight")) {
            return "Combination";
        } else if (getRadioGroupAnswers(q2Group).equals("At least twice a day") &&
                getRadioGroupAnswers(q3Group).equals("The skin of my face is very oily") &&
                getRadioGroupAnswers(q5Group).equals("My facial skin feels clean and pleasant")) {
            return "Oily";
        } else if (getRadioGroupAnswers(q2Group).equals("At least twice a day") &&
                getRadioGroupAnswers(q3Group).equals("The skin of my face is very oily") &&
                getRadioGroupAnswers(q5Group).equals("I feel like my T-zone is dry")) {
            return "Oily";

            // 3 combinations
            // total 9 combinations

        } else if (getRadioGroupAnswers(q2Group).equals("Once a day") &&
                getRadioGroupAnswers(q3Group).equals("My facial skin is dry/hard") &&
                getRadioGroupAnswers(q5Group).equals("My facial skin feels very tight")) {
            return "Dry";
        } else if (getRadioGroupAnswers(q2Group).equals("Once a day") &&
                getRadioGroupAnswers(q3Group).equals("My facial skin is dry/hard") &&
                getRadioGroupAnswers(q5Group).equals("My facial skin feels clean and pleasant")) {
            return "Combination";
        } else if (getRadioGroupAnswers(q2Group).equals("Once a day") &&
                getRadioGroupAnswers(q3Group).equals("My facial skin is dry/hard") &&
                getRadioGroupAnswers(q5Group).equals("I feel like my T-zone is dry")) {
            return "Combination";

            // 3 combinations

        } else if (getRadioGroupAnswers(q2Group).equals("Once a day") &&
                getRadioGroupAnswers(q3Group).equals("My facial skin is oily in certain areas") &&
                getRadioGroupAnswers(q5Group).equals("My facial skin feels very tight")) {
            return "Combination";
        } else if (getRadioGroupAnswers(q2Group).equals("Once a day") &&
                getRadioGroupAnswers(q3Group).equals("My facial skin is oily in certain areas") &&
                getRadioGroupAnswers(q5Group).equals("My facial skin feels clean and pleasant")) {
            return "Combination";
        } else if (getRadioGroupAnswers(q2Group).equals("Once a day") &&
                getRadioGroupAnswers(q3Group).equals("My facial skin is oily in certain areas") &&
                getRadioGroupAnswers(q5Group).equals("I feel like my T-zone is dry")) {
            return "Oily";

            // 3 combinations

        } else if (getRadioGroupAnswers(q2Group).equals("Once a day") &&
                getRadioGroupAnswers(q3Group).equals("The skin of my face is very oily") &&
                getRadioGroupAnswers(q5Group).equals("My facial skin feels very tight")) {
            return "Oily";
        } else if (getRadioGroupAnswers(q2Group).equals("Once a day") &&
                getRadioGroupAnswers(q3Group).equals("The skin of my face is very oily") &&
                getRadioGroupAnswers(q5Group).equals("My facial skin feels clean and pleasant")) {
            return "Oily";
        } else if (getRadioGroupAnswers(q2Group).equals("Once a day") &&
                getRadioGroupAnswers(q3Group).equals("The skin of my face is very oily") &&
                getRadioGroupAnswers(q5Group).equals("I feel like my T-zone is dry")) {
            return "Oily";

            // 3 combinations
            // total 18 combinations
        } else if (getRadioGroupAnswers(q2Group).equals("Rarely") &&
                getRadioGroupAnswers(q3Group).equals("My facial skin is dry/hard") &&
                getRadioGroupAnswers(q5Group).equals("My facial skin feels very tight")) {
            return "Dry";
        } else if (getRadioGroupAnswers(q2Group).equals("Rarely") &&
                getRadioGroupAnswers(q3Group).equals("My facial skin is dry/hard") &&
                getRadioGroupAnswers(q5Group).equals("My facial skin feels clean and pleasant")) {
            return "Combination";
        } else if (getRadioGroupAnswers(q2Group).equals("Rarely") &&
                getRadioGroupAnswers(q3Group).equals("My facial skin is dry/hard") &&
                getRadioGroupAnswers(q5Group).equals("I feel like my T-zone is dry")) {
            return "Combination";

            // 3 combinations

        } else if (getRadioGroupAnswers(q2Group).equals("Rarely") &&
                getRadioGroupAnswers(q3Group).equals("My facial skin is oily in certain areas") &&
                getRadioGroupAnswers(q5Group).equals("My facial skin feels very tight")) {
            return "Combination";
        } else if (getRadioGroupAnswers(q2Group).equals("Rarely") &&
                getRadioGroupAnswers(q3Group).equals("My facial skin is oily in certain areas") &&
                getRadioGroupAnswers(q5Group).equals("My facial skin feels clean and pleasant")) {
            return "Combination";
        } else if (getRadioGroupAnswers(q2Group).equals("Rarely") &&
                getRadioGroupAnswers(q3Group).equals("My facial skin is oily in certain areas") &&
                getRadioGroupAnswers(q5Group).equals("I feel like my T-zone is dry")) {
            return "Oily";

            // 3 combinations

        } else if (getRadioGroupAnswers(q2Group).equals("Rarely") &&
                getRadioGroupAnswers(q3Group).equals("The skin of my face is very oily") &&
                getRadioGroupAnswers(q5Group).equals("My facial skin feels very tight")) {
            return "Oily";
        } else if (getRadioGroupAnswers(q2Group).equals("Rarely") &&
                getRadioGroupAnswers(q3Group).equals("The skin of my face is very oily") &&
                getRadioGroupAnswers(q5Group).equals("My facial skin feels clean and pleasant")) {
            return "Oily";
        } else if (getRadioGroupAnswers(q2Group).equals("Rarely") &&
                getRadioGroupAnswers(q3Group).equals("The skin of my face is very oily") &&
                getRadioGroupAnswers(q5Group).equals("I feel like my T-zone is dry")) {
            return "Oily";

            // 3 combinations
            // total 27 combinations
        } else {
            return "unknown";
        }
    }

}
