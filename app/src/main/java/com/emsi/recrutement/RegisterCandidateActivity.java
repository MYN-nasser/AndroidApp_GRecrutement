package com.emsi.recrutement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class RegisterCandidateActivity extends AppCompatActivity {

    // Champs texte
    private TextInputEditText etNom, etPrenom, etEmail, etTelephone,
            etPromo, etPassword, etConfirmPassword;

    // Layouts pour erreurs (TextInputLayout permet d'afficher le message rouge en dessous)
    private TextInputLayout tilNom, tilPrenom, tilEmail, tilTelephone,
            tilPromo, tilPassword, tilConfirmPassword;

    // Boutons et textes
    private Button btnUploadCv, btnRegister;
    private TextView tvCvStatus, tvLoginLink;

    // Base de données
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_candidate);

        // Initialiser la base de données
        dbHelper = new DatabaseHelper(this);

        // Initialiser toutes les vues
        initViews();

        // Configurer les écouteurs de clics
        setupListeners();

        // Gestion des barres système (pour le design bord à bord)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        etNom = findViewById(R.id.et_nom);
        etPrenom = findViewById(R.id.et_prenom);
        etEmail = findViewById(R.id.et_email);
        etTelephone = findViewById(R.id.et_telephone);
        etPromo = findViewById(R.id.et_promo);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        tilNom = findViewById(R.id.til_nom);
        tilPrenom = findViewById(R.id.til_prenom);
        tilEmail = findViewById(R.id.til_email);
        tilTelephone = findViewById(R.id.til_telephone);
        tilPromo = findViewById(R.id.til_promo);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);

        btnUploadCv = findViewById(R.id.btn_upload_cv);
        btnRegister = findViewById(R.id.btn_register);
        tvCvStatus = findViewById(R.id.tv_cv_status);
        tvLoginLink = findViewById(R.id.tv_login_link);
    }

    private void setupListeners() {
        // Bouton d'inscription
        btnRegister.setOnClickListener(v -> registerCandidate());

        // Lien vers la page de connexion
        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterCandidateActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Bouton Upload (à implémenter plus tard)
        btnUploadCv.setOnClickListener(v -> {
            Toast.makeText(this, "Fonctionnalité upload à venir", Toast.LENGTH_SHORT).show();
        });
    }

    private void registerCandidate() {
        // 1. Réinitialiser les erreurs visuelles
        clearAllErrors();

        // 2. Récupérer les valeurs saisies
        String nom = etNom.getText().toString().trim();
        String prenom = etPrenom.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telephone = etTelephone.getText().toString().trim();
        String promo = etPromo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        boolean hasError = false;

        // --- VALIDATIONS ---

        if (nom.isEmpty()) {
            tilNom.setError("Le nom est obligatoire");
            hasError = true;
        }

        if (prenom.isEmpty()) {
            tilPrenom.setError("Le prénom est obligatoire");
            hasError = true;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email invalide");
            hasError = true;
        }

        if (promo.isEmpty()) {
            tilPromo.setError("Année de promotion obligatoire");
            hasError = true;
        }

        if (password.length() < 6) {
            tilPassword.setError("Minimum 6 caractères");
            hasError = true;
        }

        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Les mots de passe ne correspondent pas");
            hasError = true;
        }

        // 3. Si aucune erreur, on procède à l'enregistrement
        if (!hasError) {
            // Ajouter à la base de données
            long result = dbHelper.addUser(email, password, nom, prenom, "candidat");

            if (result != -1) {
                // SUCCÈS : Redirection vers le Dashboard
                Toast.makeText(this, "Bienvenue " + prenom + " !", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(RegisterCandidateActivity.this, DashboardCandidateActivity.class);
                intent.putExtra("USER_EMAIL", email);
                startActivity(intent);

                // On ferme cette page pour ne pas pouvoir revenir au formulaire avec le bouton "Retour"
                finish();
            } else {
                Toast.makeText(this, "Erreur lors de la création du compte", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void clearAllErrors() {
        tilNom.setError(null);
        tilPrenom.setError(null);
        tilEmail.setError(null);
        tilTelephone.setError(null);
        tilPromo.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}