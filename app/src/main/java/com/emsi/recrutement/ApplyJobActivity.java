package com.emsi.recrutement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ApplyJobActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int jobId;
    private String userEmail;
    private User currentUser;
    private JobOffer currentJob;
    private CVSelectionAdapter cvAdapter;
    private EditText etCoverLetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_job);

        // Get data from intent
        jobId = getIntent().getIntExtra("JOB_ID", -1);
        userEmail = getIntent().getStringExtra("USER_EMAIL");

        if (jobId == -1 || userEmail == null) {
            Toast.makeText(this, "Erreur: données manquantes", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize database
        dbHelper = new DatabaseHelper(this);
        currentUser = dbHelper.getUserByEmail(userEmail);
        currentJob = dbHelper.getJobById(jobId);

        if (currentUser == null || currentJob == null) {
            Toast.makeText(this, "Erreur: données introuvables", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupToolbar();
        setupViews();
        loadCVs();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupViews() {
        TextView tvJobTitle = findViewById(R.id.tvJobTitle);
        TextView tvCompanyName = findViewById(R.id.tvCompanyName);
        etCoverLetter = findViewById(R.id.etCoverLetter);
        MaterialButton btnSubmit = findViewById(R.id.btnSubmit);

        tvJobTitle.setText(currentJob.getTitle());
        tvCompanyName.setText(currentJob.getCompany());

        btnSubmit.setOnClickListener(v -> submitApplication());
    }

    private void loadCVs() {
        RecyclerView rvCVs = findViewById(R.id.rvCVs);
        List<CV> cvList = dbHelper.getUserCVs(currentUser.getId());

        if (cvList.isEmpty()) {
            Toast.makeText(this, "Vous devez d'abord ajouter un CV", Toast.LENGTH_LONG).show();
            // Redirect to MyCVsActivity
            Intent intent = new Intent(this, MyCVsActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
            finish();
            return;
        }

        cvAdapter = new CVSelectionAdapter(cvList, (cv, position) -> {
            // CV selected
        });

        rvCVs.setLayoutManager(new LinearLayoutManager(this));
        rvCVs.setAdapter(cvAdapter);
    }

    private void submitApplication() {
        CV selectedCV = cvAdapter.getSelectedCV();
        if (selectedCV == null) {
            Toast.makeText(this, "Veuillez sélectionner un CV", Toast.LENGTH_SHORT).show();
            return;
        }

        String coverLetter = etCoverLetter.getText().toString().trim();

        long result = dbHelper.addApplicationWithDetails(
                currentUser.getId(),
                jobId,
                selectedCV.getId(),
                coverLetter);

        if (result != -1) {
            Toast.makeText(this, "Candidature envoyée avec succès!", Toast.LENGTH_SHORT).show();

            // Navigate to MyApplicationsActivity
            Intent intent = new Intent(this, MyApplicationsActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            intent.putExtra("APPLICATION_SENT", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Vous avez déjà postulé à cette offre", Toast.LENGTH_SHORT).show();
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
