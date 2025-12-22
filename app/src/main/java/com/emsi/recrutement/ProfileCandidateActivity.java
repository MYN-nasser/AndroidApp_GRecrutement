package com.emsi.recrutement;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

public class ProfileCandidateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_candidate);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        Button btnUploadCV = findViewById(R.id.btnUploadCV);
        if (btnUploadCV != null) {
            btnUploadCV.setOnClickListener(v ->
                    Toast.makeText(this, "Upload CV en développement", Toast.LENGTH_SHORT).show()
            );
        }

        Button btnUploadLetter = findViewById(R.id.btnUploadLetter);
        if (btnUploadLetter != null) {
            btnUploadLetter.setOnClickListener(v ->
                    Toast.makeText(this, "Upload Lettre en développement", Toast.LENGTH_SHORT).show()
            );
        }

        Button btnSaveProfile = findViewById(R.id.btnSaveProfile);
        if (btnSaveProfile != null) {
            btnSaveProfile.setOnClickListener(v -> {
                Toast.makeText(this, "Profil sauvegardé!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }
}
