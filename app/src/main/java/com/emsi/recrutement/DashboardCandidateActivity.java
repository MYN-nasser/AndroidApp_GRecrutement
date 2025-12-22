package com.emsi.recrutement;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DashboardCandidateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_candidate);

        // Get username from intent
        String username = getIntent().getStringExtra("USERNAME");
        if (username != null) {
            TextView tvWelcome = findViewById(R.id.tvWelcome);
            tvWelcome.setText("Bonjour, " + username);
        }

        setupNavigation();
    }

    private void setupNavigation() {

        // Search Jobs card
        View searchJobsCard = findViewById(R.id.btnSearchJobs);
        if (searchJobsCard != null) {
            searchJobsCard.setOnClickListener(v ->
                    startActivity(new Intent(this, JobSearchActivity.class))
            );
        }

        // My Applications card
        View myApplicationsCard = findViewById(R.id.btnMyApplications);
        if (myApplicationsCard != null) {
            myApplicationsCard.setOnClickListener(v ->
                    startActivity(new Intent(this, MyApplicationsActivity.class))
            );
        }

        // Profile navigation button
        View profileButton = findViewById(R.id.nav_profile);
        if (profileButton != null) {
            profileButton.setOnClickListener(v ->
                    startActivity(new Intent(this, ProfileCandidateActivity.class))
            );
        }

        // Logout button
        Button btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
        }
    }
}
