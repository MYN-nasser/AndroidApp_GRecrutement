package com.emsi.recrutement;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class JobSearchActivity extends AppCompatActivity implements JobOfferAdapter.OnJobClickListener {

    private RecyclerView rvJobOffers;
    private TextView tvResultsCount;
    private SearchView searchView;
    private DatabaseHelper dbHelper;
    private JobOfferAdapter adapter;
    private String userEmail;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_job_search);

        // Récupérer l'email utilisateur
        userEmail = getIntent().getStringExtra("USER_EMAIL");
        if (userEmail == null) {
            // Si pas d'email, rediriger vers login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialiser la base de données
        dbHelper = new DatabaseHelper(this);
        currentUser = dbHelper.getUserByEmail(userEmail);

        // Initialiser les vues
        initViews();
        setupRecyclerView();
        setupSearchView();
        loadAllJobs();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mann), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh job list to update save states
        String currentQuery = searchView.getQuery().toString();
        if (currentQuery.isEmpty()) {
            loadAllJobs();
        } else {
            searchJobs(currentQuery);
        }
    }

    private void initViews() {
        rvJobOffers = findViewById(R.id.rv_job_offers);
        tvResultsCount = findViewById(R.id.tv_results_count);
        searchView = findViewById(R.id.search_view_offers);
    }

    private void setupRecyclerView() {
        adapter = new JobOfferAdapter(null, this);

        // Set current user ID for save/unsave functionality
        if (currentUser != null) {
            adapter.setCurrentUserId(currentUser.getId());
        }

        rvJobOffers.setLayoutManager(new LinearLayoutManager(this));
        rvJobOffers.setAdapter(adapter);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchJobs(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    loadAllJobs();
                } else {
                    searchJobs(newText);
                }
                return true;
            }
        });
    }

    private void loadAllJobs() {
        List<JobOffer> jobs = dbHelper.getAllJobs();
        adapter.updateJobs(jobs);
        updateResultsCount(jobs.size());
    }

    private void searchJobs(String query) {
        List<JobOffer> jobs = dbHelper.searchJobs(query);
        adapter.updateJobs(jobs);
        updateResultsCount(jobs.size());
    }

    private void updateResultsCount(int count) {
        if (tvResultsCount != null) {
            tvResultsCount.setText(count + " offre" + (count > 1 ? "s" : "") + " disponible" + (count > 1 ? "s" : ""));
        }
    }

    @Override
    public void onJobClick(JobOffer job) {
        // Naviguer vers JobDetailsActivity
        Intent intent = new Intent(this, JobDetailsActivity.class);
        intent.putExtra("JOB_ID", job.getId());
        intent.putExtra("USER_EMAIL", userEmail);
        startActivity(intent);
    }

    @Override
    public void onSaveJobClick(JobOffer job) {
        // Save functionality is handled by the adapter
        // This callback can be used for additional actions if needed
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}