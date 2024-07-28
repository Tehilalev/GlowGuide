package com.example.glowguide;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {

    private TextView welcome, fullName, email, phone, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("UserProfileActivity", "onCreate called");
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        Button editProfile;

        FirebaseAuth fAuth;
        FirebaseFirestore fStore;
        String userId;

        welcome = findViewById(R.id.textView_show_welcome);
        fullName = findViewById(R.id.textView_show_full_name);
        email = findViewById(R.id.textView_show_email);
        phone = findViewById(R.id.textView_show_mobile);
        password = findViewById(R.id.textView_show_password);

        editProfile = findViewById(R.id.editProfile);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                fullName.setText(value.getString("fName"));
                email.setText(value.getString("email"));
                phone.setText(value.getString("phone"));
                password.setText(value.getString("password"));
                welcome.setText("Welcome " + value.getString("fName"));

            }
        });

        editProfile.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), editProfile.class);
            i.putExtra("fName", fullName.getText().toString());
            i.putExtra("email", email.getText().toString());
            i.putExtra("phone", phone.getText().toString());
            i.putExtra("password", password.getText().toString());
            startActivity(i);

        });


        // Handle back button press using OnBackPressedDispatcher
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Optional, if you want to close the UserProfileActivity
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);


    }

}