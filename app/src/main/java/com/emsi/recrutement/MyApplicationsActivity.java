package com.emsi.recrutement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class MyApplicationsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private RecyclerView rvApplications;
    private LinearLayout layoutEmpty;
    private Button btnBrowseJobs;
    private DatabaseHelper dbHelper;
    private ApplicationAdapter adapter;
    private String userEmail;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_applications);

        // Récupérer l'email utilisateur
        userEmail = getIntent().getStringExtra("USER_EMAIL");
        if (userEmail == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialiser la base de données
        dbHelper = new DatabaseHelper(this);
        currentUser = dbHelper.getUserByEmail(userEmail);

        if (currentUser == null) {
            Toast.makeText(this, "Erreur: utilisateur introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Toolbar back button
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Mes candidatures");
            }
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
        loadApplications(0); // Charger toutes les candidatures par défaut
    }

    private void setupTabs() {
        if (tabLayout != null) {
            // Ajouter les tabs si elles n'existent pas déjà
            if (tabLayout.getTabCount() == 0) {
                tabLayout.addTab(tabLayout.newTab().setText("Toutes"));
                tabLayout.addTab(tabLayout.newTab().setText("En cours"));
                tabLayout.addTab(tabLayout.newTab().setText("Archivées"));
            }
            
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    loadApplications(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) { }

                @Override
                public void onTabReselected(TabLayout.Tab tab) { }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setupRecyclerView() {
        adapter = new ApplicationAdapter(null);
        rvApplications.setLayoutManager(new LinearLayoutManager(this));
        rvApplications.setAdapter(adapter);
    }

    private void setupEmptyState() {
        if (btnBrowseJobs != null) {
            btnBrowseJobs.setOnClickListener(v -> {
                Intent intent = new Intent(MyApplicationsActivity.this, JobSearchActivity.class);
                intent.putExtra("USER_EMAIL", userEmail);
                startActivity(intent);
            });
        }
    }

    private void loadApplications(int tabPosition) {
        List<DatabaseHelper.ApplicationInfo> allApplications = dbHelper.getUserApplications(currentUser.getId());
        List<DatabaseHelper.ApplicationInfo> filteredApplications;

        // Filtrer selon l'onglet sélectionné
        switch (tabPosition) {
            case 1: // En cours
                filteredApplications = new java.util.ArrayList<>();
                for (DatabaseHelper.ApplicationInfo app : allApplications) {
                    if ("En attente".equals(app.getStatus())) {
                        filteredApplications.add(app);
                    }
                }
                break;
            case 2: // Archivées
                filteredApplications = new java.util.ArrayList<>();
                for (DatabaseHelper.ApplicationInfo app : allApplications) {
                    if ("Acceptée".equals(app.getStatus()) || "Refusée".equals(app.getStatus())) {
                        filteredApplications.add(app);
                    }
                }
                break;
            default: // Toutes (0)
                filteredApplications = allApplications;
                break;
        }

        adapter.updateApplications(filteredApplications);
        updateEmptyState(filteredApplications.isEmpty());
    }

    private void updateEmptyState(boolean isEmpty) {
        if (layoutEmpty != null && rvApplications != null) {
            if (isEmpty) {
                layoutEmpty.setVisibility(View.VISIBLE);
                rvApplications.setVisibility(View.GONE);
            } else {
                layoutEmpty.setVisibility(View.GONE);
                rvApplications.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recharger les candidatures quand on revient sur cette activité
        if (currentUser != null && tabLayout != null) {
            int selectedTab = tabLayout.getSelectedTabPosition();
            loadApplications(selectedTab >= 0 ? selectedTab : 0);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("APPLICATION_SENT", false)) {
            Toast.makeText(this, "Candidature envoyée avec succès!", Toast.LENGTH_LONG).show();
            // Recharger les candidatures
            if (currentUser != null && tabLayout != null) {
                int selectedTab = tabLayout.getSelectedTabPosition();
                loadApplications(selectedTab >= 0 ? selectedTab : 0);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
