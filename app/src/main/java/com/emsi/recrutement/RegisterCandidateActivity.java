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

public class RegisterCandidateActivity extends AppCompatActivity {

    // Champs texte
    private TextInputEditText etNom, etPrenom, etEmail, etTelephone,
            etPromo, etPassword, etConfirmPassword;

    // Layouts pour erreurs
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

        // Configurer les écouteurs
        setupListeners();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        // Champs texte
        etNom = findViewById(R.id.et_nom);
        etPrenom = findViewById(R.id.et_prenom);
        etEmail = findViewById(R.id.et_email);
        etTelephone = findViewById(R.id.et_telephone);
        etPromo = findViewById(R.id.et_promo);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        // Layouts d'erreur
        tilNom = findViewById(R.id.til_nom);
        tilPrenom = findViewById(R.id.til_prenom);
        tilEmail = findViewById(R.id.til_email);
        tilTelephone = findViewById(R.id.til_telephone);
        tilPromo = findViewById(R.id.til_promo);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);

        // Boutons et textes
        btnUploadCv = findViewById(R.id.btn_upload_cv);
        btnRegister = findViewById(R.id.btn_register);
        tvCvStatus = findViewById(R.id.tv_cv_status);
        tvLoginLink = findViewById(R.id.tv_login_link);
    }

    private void setupListeners() {
        // Bouton Upload CV
        btnUploadCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implémenter l'upload de fichiers
                Toast.makeText(RegisterCandidateActivity.this,
                        "Fonctionnalité upload à implémenter",
                        Toast.LENGTH_SHORT).show();
                tvCvStatus.setText("⚠️ Aucun fichier sélectionné");
            }
        });

        // Bouton d'inscription
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerCandidate();
            }
        });

        // Lien vers connexion
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterCandidateActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void registerCandidate() {
        // Réinitialiser toutes les erreurs
        clearAllErrors();

        // Récupérer les valeurs
        String nom = etNom.getText().toString().trim();
        String prenom = etPrenom.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telephone = etTelephone.getText().toString().trim();
        String promo = etPromo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        boolean hasError = false;

        // Validation du nom
        if (nom.isEmpty()) {
            tilNom.setError("Le nom est obligatoire");
            hasError = true;
        } else if (nom.length() < 2) {
            tilNom.setError("Nom trop court");
            hasError = true;
        }

        // Validation du prénom
        if (prenom.isEmpty()) {
            tilPrenom.setError("Le prénom est obligatoire");
            hasError = true;
        } else if (prenom.length() < 2) {
            tilPrenom.setError("Prénom trop court");
            hasError = true;
        }

        // Validation de l'email
        if (email.isEmpty()) {
            tilEmail.setError("L'email est obligatoire");
            hasError = true;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Format d'email invalide");
            hasError = true;
        } else if (!email.toLowerCase().contains("emsi") && !email.toLowerCase().contains("esi")) {
            tilEmail.setError("Veuillez utiliser votre email EMSI");
            hasError = true;
        } else if (dbHelper.checkEmailExists(email)) {
            tilEmail.setError("Cet email est déjà utilisé");
            hasError = true;
        }

        // Validation de l'année de promotion
        if (promo.isEmpty()) {
            tilPromo.setError("L'année de promotion est obligatoire");
            hasError = true;
        } else if (promo.length() != 4) {
            tilPromo.setError("Format invalide (ex: 2024)");
            hasError = true;
        } else {
            try {
                int annee = Integer.parseInt(promo);
                int anneeCourante = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
                if (annee < 2000 || annee > anneeCourante + 2) {
                    tilPromo.setError("Année invalide (2000-" + (anneeCourante + 2) + ")");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                tilPromo.setError("Doit être un nombre");
                hasError = true;
            }
        }

        // Validation du mot de passe
        if (password.isEmpty()) {
            tilPassword.setError("Le mot de passe est obligatoire");
            hasError = true;
        } else if (password.length() < 6) {
            tilPassword.setError("Minimum 6 caractères");
            hasError = true;
        }

        // Validation de la confirmation
        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError("Confirmez le mot de passe");
            hasError = true;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Les mots de passe ne correspondent pas");
            hasError = true;
        }

        // Si aucune erreur, créer le compte
        if (!hasError) {
            // Si téléphone est vide, mettre null
            String tel = telephone.isEmpty() ? "" : telephone;

            // Créer l'utilisateur dans la base
            long result = dbHelper.addUser(email, password, nom, prenom, "candidat");

            if (result != -1) {
                // Succès
                Toast.makeText(this,
                        "✅ Compte créé avec succès!\n\n" +
                                "Nom: " + prenom + " " + nom + "\n" +
                                "Email: " + email + "\n" +
                                "Promotion: " + promo,
                        Toast.LENGTH_LONG).show();

                // Rediriger vers la page de connexion
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("EMAIL", email);
                startActivity(intent);
                finish();

            } else {
                // Erreur lors de la création
                Toast.makeText(this,
                        "❌ Erreur lors de la création du compte",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Afficher un message général d'erreur
            Toast.makeText(this,
                    "Veuillez corriger les erreurs dans le formulaire",
                    Toast.LENGTH_SHORT).show();
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