package com.yourapp.emsirecrutement;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileCandidateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_candidate);

        setupButtons();
        loadUserData();
    }

    private void setupButtons() {
        // Upload CV button
        Button btnUploadCV = findViewById(R.id.btnUploadCV);
        if (btnUploadCV != null) {
            btnUploadCV.setOnClickListener(v -> {
                Toast.makeText(this, "Fonction upload CV en développement", Toast.LENGTH_SHORT).show();
                // TODO: Implement file picker
            });
        }

        // Upload Letter button
        Button btnUploadLetter = findViewById(R.id.btnUploadLetter);
        if (btnUploadLetter != null) {
            btnUploadLetter.setOnClickListener(v -> {
                Toast.makeText(this, "Fonction upload lettre en développement", Toast.LENGTH_SHORT).show();
            });
        }

        // Edit Personal Info button
        TextView tvEditPersonal = findViewById(R.id.tvEditPersonal);
        if (tvEditPersonal != null) {
            tvEditPersonal.setOnClickListener(v -> {
                Toast.makeText(this, "Édition des informations en développement", Toast.LENGTH_SHORT).show();
            });
        }

        // Save Profile button
        Button btnSaveProfile = findViewById(R.id.btnSaveProfile);
        if (btnSaveProfile != null) {
            btnSaveProfile.setOnClickListener(v -> {
                Toast.makeText(this, "Profil sauvegardé!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }

        // Back button (toolbar navigation)
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    private void loadUserData() {
        // Load dummy data for now
        TextView tvName = findViewById(R.id.tvName);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvPhone = findViewById(R.id.tvPhone);
        TextView tvLocation = findViewById(R.id.tvLocation);

        if (tvName != null) tvName.setText("Ahmed BENANI");
        if (tvEmail != null) tvEmail.setText("ahmed.benani@emsi.ma");
        if (tvPhone != null) tvPhone.setText("+212 6 12 34 56 78");
        if (tvLocation != null) tvLocation.setText("Casablanca");
    }
}