package com.example.glowguide;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SkincareActivity extends AppCompatActivity {
    Button newQuestionnaireBtn;

    private FirebaseFirestore fStore;
    private FirebaseUser user;

    private TextView resultTextView, skinTypeText,q4Sensitive, q1Sunscreen, q6, q7Water;
    private String drySkin, combinationSkin, sensitiveSkin, oilySkin, sunscreen, q6S, water;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SkincareActivity", "onCreate called");
        setContentView(R.layout.activity_skincare);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        newQuestionnaireBtn = findViewById(R.id.newQuestionnaireBtn);
        resultTextView = findViewById(R.id.resultTextView);
        skinTypeText = findViewById(R.id.skinTypeText);
        q4Sensitive = findViewById(R.id.q4Sensitive);
        q1Sunscreen = findViewById(R.id.q1Sunscreen);
        q6 = findViewById(R.id.q6);
        q7Water = findViewById(R.id.q7Water);

        drySkin = "Dry skin feels tight, rough, or flaky and often lacks moisture and natural oils. " +
                "This skin type can be prone to dullness and fine lines. " +
                "Regularly using moisturizing products and avoiding harsh cleansers can help " +
                "restore hydration and keep your skin feeling smooth and supple.";
        combinationSkin = "Combination skin features characteristics of more than one skin type on " +
                "different areas of the face. Typically, the T-zone (forehead, nose, and chin) may " +
                "be oily, while the cheeks and other areas can be normal to dry. This skin type " +
                "requires a balanced approach in skincare, targeting both oily and dry zones to " +
                "maintain a healthy complexion.";
        sensitiveSkin = "Sensitive skin is easily irritated and may react to environmental factors, " +
                "skincare products, or certain ingredients. It can be prone to redness, itching, " +
                "and dryness. If you have sensitive skin, opt for gentle, hypoallergenic products " +
                "and avoid harsh chemicals or fragrances to soothe and protect your skin.";
        oilySkin = "Oily skin is characterized by an excess production of sebum, which can give " +
                "your face a shiny appearance, especially in the T-zone (forehead, nose, and chin). " +
                "This type often experiences enlarged pores and may be prone to acne and blackheads " +
                "due to the extra oil. Regular cleansing and oil-control products can help manage " +
                "this skin type.";
        sunscreen = "put cream";
        q6S = "put something";
        water = "Staying hydrated is essential for maintaining healthy, glowing skin. " +
                "Drinking plenty of water helps keep your skin moisturized from the inside out, " +
                "which can improve its texture and overall appearance. Aim to drink at least 8 " +
                "glasses of water a day to support your skin's natural hydration and help it look " +
                "its best. Proper hydration can enhance the effectiveness of your skincare routine " +
                "and contribute to a more radiant complexion.";


        newQuestionnaireBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), NewQuestionnaire.class);
            startActivity(intent);
            finish();
        });

        loadPreviousResult();

    }

    @SuppressLint("SetTextI18n")
    private void loadPreviousResult() {
        DocumentReference docRef = fStore.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    if (document.contains("skinType")) {
                        String skinType = document.getString("skinType");
                        if (skinType != null) {
                            resultTextView.setText("Your skin type is: " + skinType);
                            resultTextView.setVisibility(View.VISIBLE);
                            displaySkinTypeExplanation(skinType);
                        } else {
                            resultTextView.setVisibility(View.GONE);

                        }
                    }

                    if (document.contains("q4")) {
                        String q4 = document.getString("q4");
                        if (q4 != null) {
                            if (q4.equals("Sensitive")) {
                                q4Sensitive.setText(sensitiveSkin);
                                q4Sensitive.setVisibility(View.VISIBLE);
                            } else {
                                q4Sensitive.setVisibility(View.GONE);
                            }
                        } else {
                            q4Sensitive.setVisibility(View.GONE);                            }
                    }

                    if (document.contains("q1")) {
                        String q1 = document.getString("q1");
                        if (q1 != null) {
                            if (q1.equals("No")) {
                                q1Sunscreen.setText(sunscreen);
                                q1Sunscreen.setVisibility(View.VISIBLE);
                            } else {
                                q1Sunscreen.setVisibility(View.GONE);
                            }
                        } else {
                            q1Sunscreen.setVisibility(View.GONE);                            }
                    }

                    if (document.contains("q6")) {
                        String q6_ = document.getString("q6");
                        if (q6_ != null) {
                            if (q6_.equals("Yes")) {
                                q6.setText(q6S);
                                q6.setVisibility(View.VISIBLE);
                            } else {
                                q6.setVisibility(View.GONE);
                            }
                        } else {
                            q6.setVisibility(View.GONE);
                        }
                    }

                    if (document.contains("q7")) {
                        String q7 = document.getString("q7");
                        if (q7 != null) {
                            if (q7.equals("Does not drink during the day") || q7.equals("0–5 cups")) {
                                q7Water.setText(water);
                                q7Water.setVisibility(View.VISIBLE);
                            } else {
                                q7Water.setVisibility(View.GONE);
                            }
                        } else {
                            q7Water.setVisibility(View.GONE);
                        }
                    }
                } else {
                    resultTextView.setText(""); // Do not display anything
                }
            } else {
                resultTextView.setText("Failed to load previous result. Please try again later.");
            }
        });
    }

    private void displaySkinTypeExplanation(String skinType) {
        switch (skinType) {
            case "Dry":
                skinTypeText.setText(drySkin);
                break;
            case "Combination":
                skinTypeText.setText(combinationSkin);
                break;
            case "Oily":
                skinTypeText.setText(oilySkin);
                break;
            default:
                skinTypeText.setText(" ");
                break;
        }

    }





}
