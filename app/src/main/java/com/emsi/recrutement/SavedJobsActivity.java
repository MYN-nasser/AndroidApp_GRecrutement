package com.emsi.recrutement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SavedJobsActivity extends AppCompatActivity implements JobOfferAdapter.OnJobClickListener {

    private RecyclerView rvSavedJobs;
    private LinearLayout layoutEmptyState;
    private JobOfferAdapter adapter;
    private DatabaseHelper dbHelper;
    private String userEmail;
    private int userId;
    private List<JobOffer> savedJobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_jobs);

        // Get user email from intent
        userEmail = getIntent().getStringExtra("USER_EMAIL");
        if (userEmail == null) {
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);

        // Get user ID
        User user = dbHelper.getUserByEmail(userEmail);
        if (user != null) {
            userId = user.getId();
        }

        setupToolbar();
        setupRecyclerView();
        loadSavedJobs();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        rvSavedJobs = findViewById(R.id.rvSavedJobs);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);

        rvSavedJobs.setLayoutManager(new LinearLayoutManager(this));
        adapter = new JobOfferAdapter(savedJobs, this);
        adapter.setCurrentUserId(userId);
        rvSavedJobs.setAdapter(adapter);
    }

    private void loadSavedJobs() {
        savedJobs = dbHelper.getSavedJobs(userId);

        if (savedJobs == null || savedJobs.isEmpty()) {
            rvSavedJobs.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvSavedJobs.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
            adapter.updateJobs(savedJobs);
        }
    }

    @Override
    public void onJobClick(JobOffer job) {
        // Navigate to job details
        Intent intent = new Intent(this, JobDetailsActivity.class);
        intent.putExtra("JOB_ID", job.getId());
        intent.putExtra("USER_EMAIL", userEmail);
        startActivity(intent);
    }

    @Override
    public void onSaveJobClick(JobOffer job) {
        // Toggle save state
        boolean isSaved = dbHelper.isJobSaved(userId, job.getId());

        if (isSaved) {
            // Unsave the job
            boolean success = dbHelper.unsaveJob(userId, job.getId());
            if (success) {
                Toast.makeText(this, "Offre retirée des favoris", Toast.LENGTH_SHORT).show();
                // Remove from list
                savedJobs.remove(job);
                adapter.updateJobs(savedJobs);

                // Check if list is now empty
                if (savedJobs.isEmpty()) {
                    rvSavedJobs.setVisibility(View.GONE);
                    layoutEmptyState.setVisibility(View.VISIBLE);
                }
            }
        } else {
            // Save the job (shouldn't happen in this screen, but handle it)
            boolean success = dbHelper.saveJob(userId, job.getId());
            if (success) {
                Toast.makeText(this, "Offre enregistrée", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh list when returning to this activity
        loadSavedJobs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
