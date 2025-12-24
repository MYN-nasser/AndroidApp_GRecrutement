package com.yourapp.emsirecrutement;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_PDF_REQUEST = 1;

    private TextInputEditText etEmail, etPromo;
    private TextInputLayout tilEmail, tilPromo;
    private Button btnUploadCV, btnRegister;
    private TextView tvCVStatus;

    private Uri cvUri = null;
    private boolean isUploading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();
    }

    private void initViews() {
        // TextInputLayouts
        tilEmail = findViewById(R.id.til_email);
        tilPromo = findViewById(R.id.til_promo);

        // EditTexts
        etEmail = findViewById(R.id.et_email);
        etPromo = findViewById(R.id.et_promo);

        // Buttons
        btnUploadCV = findViewById(R.id.btn_upload_cv);
        btnRegister = findViewById(R.id.btn_register);

        // TextViews
        tvCVStatus = findViewById(R.id.tv_cv_status);

        // Set default promo year to current year
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        etPromo.setText(String.valueOf(currentYear));
    }

    private void setupClickListeners() {
        // Upload CV button
        btnUploadCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        // Register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegistration();
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Sélectionnez votre CV"),
                    PICK_PDF_REQUEST
            );
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this,
                    "Veuillez installer un gestionnaire de fichiers",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                cvUri = data.getData();

                // Get file name
                String fileName = getFileName(cvUri);

                // Update UI
                btnUploadCV.setText("CV: " + (fileName.length() > 20 ?
                        fileName.substring(0, 20) + "..." : fileName));
                tvCVStatus.setText("✅ CV téléchargé: " + fileName);
                tvCVStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

                Toast.makeText(this, "CV téléchargé avec succès", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(
                    uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void attemptRegistration() {
        // Reset errors
        tilEmail.setError(null);
        tilPromo.setError(null);

        // Get values
        String email = etEmail.getText().toString().trim();
        String promoYear = etPromo.getText().toString().trim();

        boolean hasError = false;

        // Validate email
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("L'email est requis");
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Format d'email invalide");
            hasError = true;
        } else if (!email.toLowerCase().contains("@emsi")) {
            tilEmail.setError("Veuillez utiliser un email EMSI");
            hasError = true;
        }

        // Validate promotion year
        if (TextUtils.isEmpty(promoYear)) {
            tilPromo.setError("L'année de promotion est requise");
            hasError = true;
        } else {
            try {
                int year = Integer.parseInt(promoYear);
                int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

                if (year < 2000 || year > currentYear + 2) {
                    tilPromo.setError("Année invalide (2000-" + (currentYear + 2) + ")");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                tilPromo.setError("Année invalide");
                hasError = true;
            }
        }

        // Check CV upload
        if (cvUri == null) {
            Toast.makeText(this,
                    "Veuillez télécharger votre CV",
                    Toast.LENGTH_SHORT).show();
            hasError = true;
        }

        if (hasError) {
            return;
        }

        // All validations passed - proceed with registration
        registerUser(email, promoYear);
    }

    private void registerUser(String email, String promoYear) {
        // Show loading
        btnRegister.setEnabled(false);
        btnRegister.setText("Création en cours...");

        // Simulate registration process (replace with Firebase/API call)
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Simulate successful registration
                btnRegister.setEnabled(true);
                btnRegister.setText("Créer mon Compte Candidat");

                // Show success message
                Toast.makeText(RegisterActivity.this,
                        "Compte créé avec succès!",
                        Toast.LENGTH_SHORT).show();

                // Navigate to dashboard with user data
                Intent intent = new Intent(RegisterActivity.this, DashboardCandidateActivity.class);
                intent.putExtra("USERNAME", email.substring(0, email.indexOf("@")));
                intent.putExtra("EMAIL", email);
                intent.putExtra("PROMO_YEAR", promoYear);
                intent.putExtra("NEW_USER", true);

                // Clear activity stack
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }, 2000); // 2 second delay for simulation
    }

    // Helper method to show error
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Handle back button press
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Optional: Add fade animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    // Optional: Add menu for additional options
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_login) {
            // Navigate to login
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_help) {
            Toast.makeText(this, "Aide - Contacter l'administration EMSI", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}