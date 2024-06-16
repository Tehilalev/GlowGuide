package com.example.glowguide;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class editProfile extends AppCompatActivity {

    EditText profileName, profileEmail, profilePhone, profilePassword;
    Button saveBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;

    private boolean isValidFullName(String name) {
        return !TextUtils.isEmpty(name) && name.matches("[a-zA-Z]+( [a-zA-Z]+)*");
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return !TextUtils.isEmpty(phoneNumber) && phoneNumber.length() == 10 && phoneNumber.matches("[0-9]+");
    }

    private boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 8 && password.matches(".*[A-Z].*");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        Intent data = getIntent();
        String fullName = data.getStringExtra("fName");
        String email = data.getStringExtra("email");
        String phone = data.getStringExtra("phone");
        String password = data.getStringExtra("password");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();

        profileName = findViewById(R.id.editName);
        profileEmail = findViewById(R.id.editEmail);
        profilePhone = findViewById(R.id.editPhone);
        profilePassword = findViewById(R.id.editPassword);
        saveBtn = findViewById(R.id.save);

        saveBtn.setOnClickListener(v -> {

            // Input validation
            if (!isValidFullName(profileName.getText().toString())) {
                Toast.makeText(editProfile.this, "Full name should contain only letters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidEmail(profileEmail.getText().toString())) {
                Toast.makeText(editProfile.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPhoneNumber(profilePhone.getText().toString())) {
                Toast.makeText(editProfile.this, "Phone number should contain exactly 10 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPassword(profilePassword.getText().toString())) {
                Toast.makeText(editProfile.this, "Password should contain at least 8 characters with one uppercase letter", Toast.LENGTH_SHORT).show();
                return;
            }

            //String email = profileEmail.getText().toString();
            String password1 = profilePassword.getText().toString();

            user.updatePassword(password1).addOnCompleteListener(task -> {
                if (task.isSuccessful())
                {
                    DocumentReference docRef = fStore.collection("users").document(user.getUid());
                    Map<String, Object> edited = new HashMap<>();
                    edited.put("fName", profileName.getText().toString());
                    //edited.put("email", email);
                    edited.put("phone", profilePhone.getText().toString());
                    edited.put("password", password1);
                    docRef.update(edited);
                    Toast.makeText(editProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
                    finish();
                }
                else {
                    Toast.makeText(editProfile.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        });

        profileName.setText(fullName);
        profileEmail.setText(email);
        profilePhone.setText(phone);
        profilePassword.setText(password);

        Log.d(TAG, "onCreate: " + fullName + " " + email + " " + phone + " " + password);
    }
}