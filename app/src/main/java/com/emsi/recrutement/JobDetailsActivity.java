package com.emsi.recrutement;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class JobDetailsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private JobOffer currentJob;
    private User currentUser;
    private String userEmail;
    private int jobId;
    private boolean hasApplied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        // Récupérer les données depuis l'intent
        jobId = getIntent().getIntExtra("JOB_ID", -1);
        userEmail = getIntent().getStringExtra("USER_EMAIL");

        if (jobId == -1 || userEmail == null) {
            Toast.makeText(this, "Erreur: données manquantes", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialiser la base de données
        dbHelper = new DatabaseHelper(this);
        currentUser = dbHelper.getUserByEmail(userEmail);
        currentJob = dbHelper.getJobById(jobId);

        if (currentJob == null || currentUser == null) {
            Toast.makeText(this, "Erreur: données introuvables", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Vérifier si l'utilisateur a déjà postulé
        hasApplied = dbHelper.hasApplied(currentUser.getId(), jobId);

        // Charger les données dans l'interface
        loadJobData();
        setupToolbar();
        setupButtons();
    }

    private void loadJobData() {
        TextView tvJobTitle = findViewById(R.id.tvJobTitle);
        TextView tvCompanyName = findViewById(R.id.tvCompanyName);
        TextView tvLocation = findViewById(R.id.tvLocation);
        TextView tvSalary = findViewById(R.id.tvSalary);

        if (tvJobTitle != null) {
            tvJobTitle.setText(currentJob.getTitle());
        }

        if (tvCompanyName != null) {
            tvCompanyName.setText(currentJob.getCompany());
        }

        if (tvLocation != null && currentJob.getLocation() != null) {
            tvLocation.setText(currentJob.getLocation());
        }

        if (tvSalary != null && currentJob.getSalary() != null) {
            tvSalary.setText(currentJob.getSalary());
        }

        // TODO: Charger la description et les exigences si des TextView existent dans le layout
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("");
            }
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupButtons() {
        Button btnApplyNow = findViewById(R.id.btnApplyNow);
        Button btnSaveJob = findViewById(R.id.btnSaveJob);

        if (btnApplyNow != null) {
            if (hasApplied) {
                btnApplyNow.setText("Déjà postulé");
                btnApplyNow.setEnabled(false);
            } else {
                btnApplyNow.setOnClickListener(v -> applyForJob());
            }
        }

        if (btnSaveJob != null) {
            btnSaveJob.setOnClickListener(v -> {
                Toast.makeText(this, "Fonctionnalité de sauvegarde à venir", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void applyForJob() {
        long result = dbHelper.addApplication(currentUser.getId(), jobId);

        if (result != -1) {
            Toast.makeText(this, "Candidature envoyée avec succès!", Toast.LENGTH_SHORT).show();
            hasApplied = true;

            // Mettre à jour le bouton
            Button btnApplyNow = findViewById(R.id.btnApplyNow);
            if (btnApplyNow != null) {
                btnApplyNow.setText("Déjà postulé");
                btnApplyNow.setEnabled(false);
            }

            // Optionnel: Rediriger vers MyApplicationsActivity après un court délai
            new android.os.Handler().postDelayed(() -> {
                Intent intent = new Intent(JobDetailsActivity.this, MyApplicationsActivity.class);
                intent.putExtra("USER_EMAIL", userEmail);
                intent.putExtra("APPLICATION_SENT", true);
                startActivity(intent);
                finish();
            }, 1500);
        } else {
            Toast.makeText(this, "Erreur lors de l'envoi de la candidature", Toast.LENGTH_SHORT).show();
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
