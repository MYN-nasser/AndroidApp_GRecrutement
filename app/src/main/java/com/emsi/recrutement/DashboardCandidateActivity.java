package com.yourapp.emsirecrutement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.tabs.TabLayout;

public class MyApplicationsActivity extends AppCompatActivity {

    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_applications);

        // Back button
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        setupTabs();
        setupRecyclerView();
        setupEmptyState();
    }

    private void setupTabs() {
        tabLayout = findViewById(R.id.tabLayout);

        if (tabLayout != null) {
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    String tabText = tab.getText().toString();
                    Toast.makeText(MyApplicationsActivity.this,
                            "Affichage: " + tabText, Toast.LENGTH_SHORT).show();

                    // Show/hide empty state based on tab
                    TextView layoutEmpty = findViewById(R.id.layoutEmpty);
                    if (layoutEmpty != null) {
                        if (tab.getPosition() == 0) { // Toutes
                            layoutEmpty.setVisibility(View.VISIBLE);
                        } else {
                            layoutEmpty.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {}

                @Override
                public void onTabReselected(TabLayout.Tab tab) {}
            });
        }
    }

    private void setupRecyclerView() {
        RecyclerView rvApplications = findViewById(R.id.rvApplications);
        if (rvApplications != null) {
            rvApplications.setLayoutManager(new LinearLayoutManager(this));
            // TODO: Set adapter with application data
        }
    }

    private void setupEmptyState() {
        Button btnBrowseJobs = findViewById(R.id.btnBrowseJobs);
        if (btnBrowseJobs != null) {
            btnBrowseJobs.setOnClickListener(v -> {
                Intent intent = new Intent(MyApplicationsActivity.this, JobSearchActivity.class);
                startActivity(intent);
            });
        }

        // Show empty state by default
        TextView layoutEmpty = findViewById(R.id.layoutEmpty);
        if (layoutEmpty != null) {
            layoutEmpty.setVisibility(View.VISIBLE);
        }
    }

    // Handle intent from other activities
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // If coming from job application, show success message
        if (intent.getBooleanExtra("APPLICATION_SENT", false)) {
            Toast.makeText(this, "Candidature envoyée avec succès!", Toast.LENGTH_LONG).show();
        }
    }
}