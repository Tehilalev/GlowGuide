package com.example.glowguide;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);

        EditText currentPassword = findViewById(R.id.currentPassword);
        EditText newPassword = findViewById(R.id.newPassword);
        Button updatePasswordBtn = findViewById(R.id.updatePasswordBtn);
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();

        updatePasswordBtn.setOnClickListener(v -> {
            String currentPwd = currentPassword.getText().toString().trim();
            String newPwd = newPassword.getText().toString().trim();

            if (TextUtils.isEmpty(currentPwd)) {
                Toast.makeText(ChangePasswordActivity.this, "Please enter your current password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPassword(newPwd)) {
                Toast.makeText(ChangePasswordActivity.this, "New password should be at least 8 characters with one uppercase letter", Toast.LENGTH_SHORT).show();
                return;
            }

            assert user != null;
            AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), currentPwd);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newPwd).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            fStore.collection("users").document(user.getUid())
                                    .update("password", newPwd)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(ChangePasswordActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Log.e(TAG, "Error updating password in Firestore", e);
                                        Toast.makeText(ChangePasswordActivity.this, "Password update failed in Firestore", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Log.e(TAG, "Error updating password in Firebase Auth", task1.getException());
                            Toast.makeText(ChangePasswordActivity.this, "Password update failed in Firebase Auth", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Log.e(TAG, "Re-authentication failed", task.getException());
                    Toast.makeText(ChangePasswordActivity.this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                }
            });


        });


    }

    private boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 8 && password.matches(".*[A-Z].*");
    }
}