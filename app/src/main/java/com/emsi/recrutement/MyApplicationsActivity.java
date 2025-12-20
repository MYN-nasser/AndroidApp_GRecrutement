package com.yourapp.emsirecrutement;

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

        // Get username from intent (for welcome message)
        String username = getIntent().getStringExtra("USERNAME");
        if (username != null) {
            TextView tvWelcome = findViewById(R.id.tvWelcome);
            tvWelcome.setText("Bonjour, " + username);
        }

        // Setup button click listeners
        setupNavigation();
    }

    private void setupNavigation() {
        // Search Jobs button
        View searchJobsCard = findViewById(R.id.gridQuickActions).findViewById(R.id.btnSearchJobs);
        if (searchJobsCard != null) {
            searchJobsCard.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardCandidateActivity.this, JobSearchActivity.class);
                startActivity(intent);
            });
        }

        // Profile button (via bottom navigation or card)
        View profileButton = findViewById(R.id.nav_profile);
        if (profileButton != null) {
            profileButton.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardCandidateActivity.this, ProfileCandidateActivity.class);
                startActivity(intent);
            });
        }

        // My Applications button
        View myApplicationsCard = findViewById(R.id.gridQuickActions).findViewById(R.id.btnMyApplications);
        if (myApplicationsCard != null) {
            myApplicationsCard.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardCandidateActivity.this, MyApplicationsActivity.class);
                startActivity(intent);
            });
        }

        // Logout button (if you have one)
        Button btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardCandidateActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
        }
    }
}