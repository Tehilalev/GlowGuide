package com.example.glowguide;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
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
    //private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        FirebaseAuth fAuth;
        FirebaseFirestore fStore;
        String userId;

        Objects.requireNonNull(getSupportActionBar()).setTitle("Personal Area");

        welcome = findViewById(R.id.textView_show_welcome);
        fullName = findViewById(R.id.textView_show_full_name);
        email = findViewById(R.id.textView_show_email);
        phone = findViewById(R.id.textView_show_mobile);
        password = findViewById(R.id.textView_show_password);

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


    }
}