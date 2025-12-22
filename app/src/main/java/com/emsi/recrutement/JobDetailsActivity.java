package com.emsi.recrutement;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class JobDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        // Get job data from intent
        String jobTitle = getIntent().getStringExtra("JOB_TITLE");
        String company = getIntent().getStringExtra("COMPANY");

        // Set job details
        TextView tvJobTitle = findViewById(R.id.tvJobTitle);
        TextView tvCompanyName = findViewById(R.id.tvCompanyName);

        if (tvJobTitle != null && jobTitle != null) {
            tvJobTitle.setText(jobTitle);
        }

        if (tvCompanyName != null && company != null) {
            tvCompanyName.setText(company);
        }

        setupButtons();
    }

    private void setupButtons() {

        // Apply button
        Button btnApplyNow = findViewById(R.id.btnApplyNow);
        if (btnApplyNow != null) {
            btnApplyNow.setOnClickListener(v -> {
                Toast.makeText(this, "Candidature envoyée avec succès!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }

        // Save job button
        Button btnSaveJob = findViewById(R.id.btnSaveJob);
        if (btnSaveJob != null) {
            btnSaveJob.setOnClickListener(v -> {
                Toast.makeText(this, "Offre sauvegardée", Toast.LENGTH_SHORT).show();
                btnSaveJob.setText("Sauvegardée");
            });
        }

        // Back button in toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }
}
