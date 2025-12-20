package com.yourapp.emsirecrutement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class JobSearchActivity extends AppCompatActivity {

    private RecyclerView rvJobs;
    private EditText etSearch;
    private ChipGroup chipGroupFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_search);

        setupViews();
        setupFilters();
        setupRecyclerView();
        setupSearch();
    }

    private void setupViews() {
        // Back button
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Initialize views
        rvJobs = findViewById(R.id.rvJobs);
        etSearch = findViewById(R.id.etSearch);
        chipGroupFilters = findViewById(R.id.chipGroupFilters);

        // Filter button
        Button btnFilter = findViewById(R.id.btnFilter);
        if (btnFilter != null) {
            btnFilter.setOnClickListener(v -> {
                if (chipGroupFilters.getVisibility() == View.VISIBLE) {
                    chipGroupFilters.setVisibility(View.GONE);
                } else {
                    chipGroupFilters.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void setupFilters() {
        // Setup chip listeners
        for (int i = 0; i < chipGroupFilters.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupFilters.getChildAt(i);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // Filter jobs
                    Toast.makeText(this, "Filtre appliqué: " + chip.getText(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupRecyclerView() {
        // Set layout manager
        rvJobs.setLayoutManager(new LinearLayoutManager(this));

        // TODO: Create JobAdapter and set it here
        // rvJobs.setAdapter(new JobAdapter(getDummyJobs()));

        // For now, show empty state if no adapter
        TextView layoutEmpty = findViewById(R.id.layoutEmpty);
        if (layoutEmpty != null) {
            layoutEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void setupSearch() {
        // Search when user presses enter
        if (etSearch != null) {
            etSearch.setOnEditorActionListener((v, actionId, event) -> {
                performSearch(etSearch.getText().toString());
                return true;
            });
        }
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer un mot-clé", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Recherche: " + query, Toast.LENGTH_SHORT).show();
        // TODO: Implement actual search
    }

    // Dummy data for testing
    private List<String> getDummyJobs() {
        List<String> jobs = new ArrayList<>();
        jobs.add("Développeur Android");
        jobs.add("Ingénieur Logiciel");
        jobs.add("Data Scientist");
        jobs.add("UI/UX Designer");
        return jobs;
    }
}