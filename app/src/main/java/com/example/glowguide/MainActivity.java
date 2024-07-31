package com.example.glowguide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.glowguide.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button button;

    FirebaseUser user;
    Button exploreNearbyPlacesBtn;
    Button userProfileBtn;
    Button skincareBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.glowguide.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        skincareBtn = findViewById(R.id.skincareBtn);
        exploreNearbyPlacesBtn = findViewById(R.id.exploreNearbyPlacesBtn);
        userProfileBtn = findViewById(R.id.personalProfileBtn);
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }

        button.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        skincareBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SkincareActivity.class);
            startActivity(intent);
        });

        exploreNearbyPlacesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ExploreNearbyPlacesActivity.class);
            startActivity(intent);
        });

        binding.addNotificationBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AlarmSettingsActivity.class);
            startActivity(intent);
        });

        userProfileBtn.setOnClickListener(v -> {
            Log.d("MainActivity", "UserProfile button clicked");
            Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
            startActivity(intent);
        });

    }



}
