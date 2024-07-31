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
            int score = calculateScore();
            String skinType = classifySkinType(score);
            saveResultToFirestore(skinType);
            save4AnswersToFirestore();
            startActivity((new Intent(getApplicationContext(), SkincareActivity.class)));
            finish();
        });
    }

    private String get4Answers(RadioGroup group) {
        int selectedId = group.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Log.e("NewQuestionnaire", "No selection made for a question.");
            return "0";
        }

        RadioButton selectedRadioButton = findViewById(selectedId);
        if (selectedRadioButton == null) {
            Log.e("NewQuestionnaire", "Selected radio button is null.");
            return "0";
        }

        return selectedRadioButton.getText().toString();
    }

    private void save4AnswersToFirestore() {
        String q1Answer = get4Answers(q1Group);
        String q6Answer = get4Answers(q6Group);
        String q7Answer = get4Answers(q7Group);
        String q8Answer = get4Answers(q8Group);

        DocumentReference docRef = fStore.collection("users").document(user.getUid());
        Map<String, Object> answers = new HashMap<>();
        answers.put("q1", q1Answer);
        answers.put("q6", q6Answer);
        answers.put("q7", q7Answer);
        answers.put("q8", q8Answer);

        docRef.update(answers)
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> Toast.makeText(NewQuestionnaire.this, "Error saving answers. Please try again.", Toast.LENGTH_SHORT).show());

    }


    private int calculateScore() {
        int score = 0;

        int q2Score = getSelectedScore(q2Group);
        int q3Score = getSelectedScore(q3Group);
        int q4Score = getQ4Score(); // Updated to handle multiple selections
        int q5Score = getSelectedScore(q5Group);

        score = q2Score + q3Score + q4Score + q5Score;

        return score;
    }

    private int getSelectedScore(RadioGroup group) {
        int selectedId = group.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Log.e("NewQuestionnaire", "No selection made for a question.");
            return 0;
        }

        RadioButton selectedRadioButton = findViewById(selectedId);
        if (selectedRadioButton == null) {
            Log.e("NewQuestionnaire", "Selected radio button is null.");
            return 0;
        }

        String selectedText = selectedRadioButton.getText().toString();

        switch (selectedText) {
            case "At least twice a day":
                return 100;
            case "Once a day":
                return 50;
            case "Rarely":
                return 0;
            case "My facial skin is dry/hard":
                return 100;
            case "My facial skin is oily in certain areas":
                return 50;
            case "The skin of my face is very oily":
                return 0;
            default:
                return 0;
        }
    }

    // New method to handle multiple selections for Question 4
    private int getQ4Score() {
        int score = 0;
        if (q4_acne_pimples.isChecked()) score -= 20;
        if (q4_redness.isChecked()) score -= 20;
        if (q4_irritation_shaving.isChecked()) score -= 20;
        if (q4_none.isChecked()) score += 20;
        return score;
    }


    private String classifySkinType(int score) {
        if (score >= 230 && score <= 320) {
            return "dry";
        } else if (score >= 170 && score <= 229) {
            return "mixed";
        } else if (score >= 80 && score <= 169) {
            return "sensitive";
        } else if (score >= 0 && score <= 79) {
            return "oily";
        } else {
            return "unknown";
        }
    }

    private void saveResultToFirestore(String skinType) {
        DocumentReference docRef = fStore.collection("users").document(user.getUid());
        docRef.update("skinType", skinType)
                .addOnSuccessListener(aVoid -> Toast.makeText(NewQuestionnaire.this, "Skin type saved successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(NewQuestionnaire.this, "Error saving skin type. Please try again.", Toast.LENGTH_SHORT).show());
    }

}
