package com.emsi.recrutement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

public class MyApplicationsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private RecyclerView rvApplications;
    private LinearLayout layoutEmpty;
    private Button btnBrowseJobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_applications);

        // Toolbar back button
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Views
        tabLayout = findViewById(R.id.tabLayout);
        rvApplications = findViewById(R.id.rvApplications);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        btnBrowseJobs = findViewById(R.id.btnBrowseJobs);

        setupTabs();
        setupRecyclerView();
        setupEmptyState();
    }

    private void setupTabs() {
        if (tabLayout != null) {
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    updateEmptyState(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) { }

                @Override
                public void onTabReselected(TabLayout.Tab tab) { }
            });
        }
    }

    private void setupRecyclerView() {
        rvApplications.setLayoutManager(new LinearLayoutManager(this));
        // TODO: Set your adapter here with application data
    }

    private void setupEmptyState() {
        btnBrowseJobs.setOnClickListener(v -> {
            Intent intent = new Intent(MyApplicationsActivity.this, JobSearchActivity.class);
            startActivity(intent);
        });

        // Show empty state initially
        updateEmptyState(0);
    }

    private void updateEmptyState(int tabPosition) {
        // For now, always show empty state; you can customize based on tabPosition
        layoutEmpty.setVisibility(View.VISIBLE);
        rvApplications.setVisibility(View.GONE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("APPLICATION_SENT", false)) {
            Toast.makeText(this, "Candidature envoyée avec succès!", Toast.LENGTH_LONG).show();
        }
    }
}
