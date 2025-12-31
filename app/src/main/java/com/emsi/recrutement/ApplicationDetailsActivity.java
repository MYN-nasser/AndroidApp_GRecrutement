package com.emsi.recrutement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.io.File;

public class ApplicationDetailsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int applicationId;
    private DatabaseHelper.ApplicationInfo application;
    private User currentUser;
    private JobOffer currentJob;
    private CV currentCV;

    private ImageView ivProfileImage;
    private TextView tvUserName, tvUserEmail, tvUserPhone;
    private TextView tvJobTitle, tvCompanyName, tvApplicationDate;
    private TextView tvCVName, tvCoverLetter;
    private MaterialButton btnViewCV;
    private MaterialCardView cardCoverLetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_details);

        // Get application ID from intent
        applicationId = getIntent().getIntExtra("APPLICATION_ID", -1);
        if (applicationId == -1) {
            Toast.makeText(this, "Erreur: candidature introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        setupToolbar();
        initViews();
        loadApplicationData();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        tvJobTitle = findViewById(R.id.tvJobTitle);
        tvCompanyName = findViewById(R.id.tvCompanyName);
        tvApplicationDate = findViewById(R.id.tvApplicationDate);
        tvCVName = findViewById(R.id.tvCVName);
        tvCoverLetter = findViewById(R.id.tvCoverLetter);
        btnViewCV = findViewById(R.id.btnViewCV);
        cardCoverLetter = findViewById(R.id.cardCoverLetter);
    }

    private void loadApplicationData() {
        // Get application details
        application = getApplicationById(applicationId);
        if (application == null) {
            Toast.makeText(this, "Erreur: candidature introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load user data
        currentUser = dbHelper.getUserById(application.getUserId());
        if (currentUser != null) {
            displayUserInfo();
        }

        // Load job data
        currentJob = dbHelper.getJobById(application.getJobId());
        if (currentJob != null) {
            displayJobInfo();
        }

        // Load CV and cover letter
        loadCVAndCoverLetter();
    }

    private DatabaseHelper.ApplicationInfo getApplicationById(int appId) {
        // Query database directly for this application
        return dbHelper.getApplicationById(appId);
    }

    private void displayUserInfo() {
        tvUserName.setText(currentUser.getPrenom() + " " + currentUser.getNom());
        tvUserEmail.setText(currentUser.getEmail());
        tvUserPhone.setText(currentUser.getTelephone() != null ? currentUser.getTelephone() : "Non renseigné");

        // TODO: Load profile image if available
        // For now, use placeholder
    }

    private void displayJobInfo() {
        tvJobTitle.setText(currentJob.getTitle());
        tvCompanyName.setText(currentJob.getCompany());

        // Format date
        String dateStr = application.getDate();
        if (dateStr != null && dateStr.contains(" ")) {
            dateStr = dateStr.split(" ")[0];
        }
        tvApplicationDate.setText("Postulé le: " + dateStr);
    }

    private void loadCVAndCoverLetter() {
        // Get CV ID and cover letter from database
        int cvId = getApplicationCVId(applicationId);
        String coverLetter = getApplicationCoverLetter(applicationId);

        if (cvId != -1) {
            currentCV = dbHelper.getCVById(cvId);
            if (currentCV != null) {
                tvCVName.setText(currentCV.getFileName());
                btnViewCV.setOnClickListener(v -> openCV());
            }
        } else {
            tvCVName.setText("Aucun CV joint");
            btnViewCV.setEnabled(false);
        }

        // Display cover letter
        if (coverLetter != null && !coverLetter.trim().isEmpty()) {
            tvCoverLetter.setText(coverLetter);
        } else {
            tvCoverLetter.setText("Aucune lettre de motivation jointe");
            tvCoverLetter.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    private int getApplicationCVId(int appId) {
        // Query database for CV ID
        android.database.Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT " + DatabaseHelper.COLUMN_APPLICATION_CV_ID + " FROM " + DatabaseHelper.TABLE_APPLICATIONS +
                        " WHERE " + DatabaseHelper.COLUMN_APPLICATION_ID + " = ?",
                new String[] { String.valueOf(appId) });
        int cvId = -1;
        if (cursor.moveToFirst()) {
            int colIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_APPLICATION_CV_ID);
            if (colIndex != -1) {
                cvId = cursor.getInt(colIndex);
            }
        }
        cursor.close();
        return cvId;
    }

    private String getApplicationCoverLetter(int appId) {
        // Query database for cover letter
        android.database.Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT " + DatabaseHelper.COLUMN_APPLICATION_COVER_LETTER + " FROM "
                        + DatabaseHelper.TABLE_APPLICATIONS +
                        " WHERE " + DatabaseHelper.COLUMN_APPLICATION_ID + " = ?",
                new String[] { String.valueOf(appId) });
        String coverLetter = null;
        if (cursor.moveToFirst()) {
            int colIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_APPLICATION_COVER_LETTER);
            if (colIndex != -1) {
                coverLetter = cursor.getString(colIndex);
            }
        }
        cursor.close();
        return coverLetter;
    }

    private void openCV() {
        if (currentCV == null) {
            Toast.makeText(this, "CV introuvable", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File cvFile = new File(currentCV.getFileUri());
            if (!cvFile.exists()) {
                Toast.makeText(this, "Fichier CV introuvable", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri cvUri = FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".provider",
                    cvFile);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(cvUri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors de l'ouverture du CV", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
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
