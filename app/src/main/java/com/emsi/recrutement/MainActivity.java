package com.emsi.recrutement;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If you don't have activity_main.xml, create it or use this quick test
        setupTestNavigation();
    }

    private void setupTestNavigation() {
        // Create a simple test layout programmatically
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setGravity(android.view.Gravity.CENTER);
        layout.setPadding(50, 50, 50, 50);

        // Test buttons
        String[] activities = {
                "Login", "Register", "Dashboard", "Profile",
                "JobSearch", "JobDetails", "MyApplications"
        };

        for (String activity : activities) {
            Button btn = new Button(this);
            btn.setText("Test: " + activity);
            btn.setPadding(20, 20, 20, 20);
            btn.setAllCaps(false);

            btn.setOnClickListener(v -> {
                navigateToActivity(activity);
            });

            layout.addView(btn);

            // Add spacing
            android.widget.Space space = new android.widget.Space(this);
            space.setMinimumHeight(20);
            layout.addView(space);
        }

        setContentView(layout);
    }

    private void navigateToActivity(String activityName) {
        Intent intent = null;

        switch (activityName) {
            case "Login":
                intent = new Intent(this, LoginActivity.class);
                break;
            case "Register":
                intent = new Intent(this, RegisterActivity.class);
                break;
            case "Dashboard":
                intent = new Intent(this, DashboardCandidateActivity.class);
                intent.putExtra("USERNAME", "Test User");
                break;
            case "Profile":
                intent = new Intent(this, ProfileCandidateActivity.class);
                break;
            case "JobSearch":
                intent = new Intent(this, JobSearchActivity.class);
                break;
            case "JobDetails":
                intent = new Intent(this, JobDetailsActivity.class);
                intent.putExtra("JOB_TITLE", "Développeur Android");
                intent.putExtra("COMPANY", "TechMaroc");
                break;
            case "MyApplications":
                intent = new Intent(this, MyApplicationsActivity.class);
                break;
        }

        if (intent != null) {
            startActivity(intent);
        }
    }
}