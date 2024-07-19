package com.example.glowguide;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
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

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        exploreNearbyPlacesBtn = findViewById(R.id.exploreNearbyPlacesBtn);
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

        exploreNearbyPlacesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ExploreNearbyPlacesActivity.class);
            startActivity(intent);
        });

        binding.addNotificationBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AlarmSettingsActivity.class);
            startActivity(intent);
        });
    }



}
