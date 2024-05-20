package com.example.glowguide;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Register extends AppCompatActivity {
    TextInputEditText first_name, last_name,email, phone, password;
    Button button_reg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    // Input validation methods
    private boolean isValidName(String name) {
        return !TextUtils.isEmpty(name) && name.matches("[a-zA-Z]+");
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
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        first_name = findViewById(R.id.firstname);
        last_name = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        button_reg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        button_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String firstname, lastname,_email,  _phone, _password;
                firstname = String.valueOf(first_name.getText());
                lastname =  String.valueOf(last_name.getText());
                _phone =  String.valueOf(phone.getText());
                _email =  String.valueOf(email.getText());
                _password =  String.valueOf(password.getText());

                // Input validation
                if (!isValidName(firstname)) {
                    Toast.makeText(Register.this, "First name should contain only letters", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (!isValidName(lastname)) {
                    Toast.makeText(Register.this, "Last name should contain only letters", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (!isValidEmail(_email)) {
                    Toast.makeText(Register.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (!isValidPhoneNumber(_phone)) {
                    Toast.makeText(Register.this, "Phone number should contain exactly 10 digits", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (!isValidPassword(_password)) {
                    Toast.makeText(Register.this, "Password should contain at least 8 characters with one uppercase letter", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                mAuth.createUserWithEmailAndPassword(_email, _password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Account created",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Register.this, "Authentication failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });


    }


}

