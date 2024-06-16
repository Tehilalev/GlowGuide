package com.example.glowguide;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    TextInputEditText email, password;
    Button button_login;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView register, forgotTextLink;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        button_login = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        register = findViewById(R.id.registerNow);
        forgotTextLink = findViewById(R.id.forgotPassword);

        register.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Register.class);
            startActivity(intent);
            finish();
        });

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String _email, _password;
                _email =  String.valueOf(email.getText());
                _password =  String.valueOf(password.getText());


                if(TextUtils.isEmpty(_email)){
                    Toast.makeText(Login.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(_password)){
                    Toast.makeText(Login.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(_email, _password)
                        .addOnCompleteListener(task -> {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Login.this, "Authentication failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        forgotTextLink.setOnClickListener(v -> {

            EditText resetMail = new EditText(v.getContext());
            final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
            passwordResetDialog.setTitle("Reset Password?");
            passwordResetDialog.setMessage("Enter Your Email To Received Reset Link.");
            passwordResetDialog.setView(resetMail);

            passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                      //extract the email and send reset link

                    String mail = resetMail.getText().toString();
                    mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(Login.this, "Reset Link Sent To Your Email", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Login.this, "Error! Reset Link Is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });

            passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                      // close the dialog
                }
            });

            passwordResetDialog.create().show();
        });


    }
}