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

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh save button state when returning to this screen
        android.widget.ImageButton btnSaveJob = findViewById(R.id.btnSaveJob);
        if (btnSaveJob != null && currentUser != null) {
            boolean isSaved = dbHelper.isJobSaved(currentUser.getId(), jobId);
            updateSaveButtonState(btnSaveJob, isSaved);
        }
    }

    private void loadJobData() {
        TextView tvJobTitle = findViewById(R.id.tvJobTitle);
        TextView tvCompanyName = findViewById(R.id.tvCompanyName);
        TextView tvLocation = findViewById(R.id.tvLocation);
        TextView tvSalary = findViewById(R.id.tvSalary);
        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvRequirements = findViewById(R.id.tvRequirements);
        TextView tvContractType = findViewById(R.id.tvContractType);
        TextView tvExperience = findViewById(R.id.tvExperience);
        // ImageView ivCompanyLogo = findViewById(R.id.ivCompanyLogo); // To be
        // implemented with Glide/Picasso if URL exists

        if (tvJobTitle != null)
            tvJobTitle.setText(currentJob.getTitle());
        if (tvCompanyName != null)
            tvCompanyName.setText(currentJob.getCompany());
        if (tvLocation != null)
            tvLocation.setText(currentJob.getLocation());

        if (tvSalary != null) {
            String salary = currentJob.getSalary();
            tvSalary.setText((salary != null && !salary.isEmpty()) ? salary : "Salaire non spécifié");
        }

        if (tvDescription != null) {
            String desc = currentJob.getDescription();
            tvDescription.setText((desc != null && !desc.isEmpty()) ? desc : "Aucune description disponible.");
        }

        if (tvRequirements != null) {
            String reqs = currentJob.getRequirements();
            tvRequirements.setText((reqs != null && !reqs.isEmpty()) ? reqs : "Aucune exigence spécifiée.");
        }

        if (tvContractType != null) {
            String type = currentJob.getType();
            tvContractType.setText((type != null && !type.isEmpty()) ? type : "CDI");
        }

        // Placeholder for Experience as it's not in DB yet
        if (tvExperience != null) {
            tvExperience.setText("+3 ans");
        }
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
        android.widget.ImageButton btnSaveJob = findViewById(R.id.btnSaveJob);
        com.google.android.material.button.MaterialButton btnApplyNow = findViewById(R.id.btnApplyNow);

        // Check if job is already saved
        boolean isSaved = dbHelper.isJobSaved(currentUser.getId(), jobId);
        updateSaveButtonState(btnSaveJob, isSaved);

        if (btnSaveJob != null) {
            btnSaveJob.setOnClickListener(v -> {
                boolean currentlySaved = dbHelper.isJobSaved(currentUser.getId(), jobId);
                if (currentlySaved) {
                    // Unsave the job
                    dbHelper.unsaveJob(currentUser.getId(), jobId);
                    updateSaveButtonState(btnSaveJob, false);
                    Toast.makeText(this, "Offre retirée des favoris", Toast.LENGTH_SHORT).show();
                } else {
                    // Save the job
                    dbHelper.saveJob(currentUser.getId(), jobId);
                    updateSaveButtonState(btnSaveJob, true);
                    Toast.makeText(this, "Offre ajoutée aux favoris", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnApplyNow != null) {
            if (hasApplied) {
                btnApplyNow.setText("Déjà postulé");
                btnApplyNow.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50)); // Green
                btnApplyNow.setEnabled(true); // Keep enabled to show state
                btnApplyNow.setOnClickListener(null); // Remove click action
            } else {
                btnApplyNow.setText("Postuler");
                btnApplyNow.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                        getResources().getColor(R.color.primary_color)));
                btnApplyNow.setOnClickListener(v -> applyForJob());
            }
        }
    }

    private void updateSaveButtonState(android.widget.ImageButton button, boolean isSaved) {
        if (button != null) {
            if (isSaved) {
                // Yellow icon for saved
                button.setImageResource(R.drawable.ic_bookmark_filled);
                button.setColorFilter(0xFFFFC107);
            } else {
                // Gray icon for unsaved
                button.setImageResource(R.drawable.ic_bookmark_border);
                button.setColorFilter(0xFF9E9E9E);
            }
        }
    }

    private void applyForJob() {
        long result = dbHelper.addApplication(currentUser.getId(), jobId);

        if (result != -1) {
            Toast.makeText(this, "Candidature envoyée avec succès!", Toast.LENGTH_SHORT).show();
            hasApplied = true;

            // Mettre à jour le bouton
            com.google.android.material.button.MaterialButton btnApplyNow = findViewById(R.id.btnApplyNow);
            if (btnApplyNow != null) {
                btnApplyNow.setText("Déjà postulé");
                btnApplyNow.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50)); // Green
                btnApplyNow.setEnabled(true);
                btnApplyNow.setOnClickListener(null);
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
