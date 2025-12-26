package com.emsi.recrutement;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileCandidateActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private User currentUser;
    private String userEmail;

    // UI Components
    private TextInputEditText etName, etFirstName, etPhone, etEmail;
    private TextView tvHeaderName, tvHeaderEmail;
    private CircleImageView ivProfile;
    private Button btnSaveProfile, btnChangePassword;

    // Image Picker
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private String selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_candidate);

        // Récupérer l'email utilisateur
        userEmail = getIntent().getStringExtra("USER_EMAIL");
        if (userEmail == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialiser la base de données
        dbHelper = new DatabaseHelper(this);

        // Initialiser le picker d'image
        initImagePicker();

        // Initialiser les vues
        initViews();

        // Charger les données
        loadUserData();

        // Configurer les actions
        setupListeners();
        setupToolbar();
    }

    private void initImagePicker() {
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                // Persist permission for specific URI
                try {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                selectedImageUri = uri.toString();
                ivProfile.setImageURI(uri);
                Toast.makeText(this, "Photo sélectionnée", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("PhotoPicker", "Pas de média sélectionné");
            }
        });
    }

    private void initViews() {
        // Inputs
        etName = findViewById(R.id.etName);
        etFirstName = findViewById(R.id.etFirstName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);

        // Header
        tvHeaderName = findViewById(R.id.tvHeaderName);
        tvHeaderEmail = findViewById(R.id.tvHeaderEmail);
        ivProfile = findViewById(R.id.ivProfile);

        // Buttons
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
    }

    private void loadUserData() {
        currentUser = dbHelper.getUserByEmail(userEmail);

        if (currentUser == null) {
            Toast.makeText(this, "Erreur: utilisateur introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set Texts
        etName.setText(currentUser.getNom());
        etFirstName.setText(currentUser.getPrenom());
        etPhone.setText(currentUser.getTelephone());
        etEmail.setText(currentUser.getEmail());

        tvHeaderName.setText(currentUser.getFullName());
        tvHeaderEmail.setText(currentUser.getEmail());

        // Load Image
        if (currentUser.getImageUri() != null && !currentUser.getImageUri().isEmpty()) {
            try {
                ivProfile.setImageURI(Uri.parse(currentUser.getImageUri()));
                selectedImageUri = currentUser.getImageUri();
            } catch (Exception e) {
                ivProfile.setImageResource(R.drawable.ic_profile_placeholder);
            }
        }
    }

    private void setupListeners() {
        // Image Upload
        if (findViewById(R.id.ivCamera) != null) {
            findViewById(R.id.ivCamera).setOnClickListener(v -> launchImagePicker());
        }
        ivProfile.setOnClickListener(v -> launchImagePicker());

        // Save Profile
        btnSaveProfile.setOnClickListener(v -> saveProfile());

        // Change Password
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
    }

    private void launchImagePicker() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void saveProfile() {
        String nom = etName.getText().toString().trim();
        String prenom = etFirstName.getText().toString().trim();
        String telephone = etPhone.getText().toString().trim();

        if (nom.isEmpty() || prenom.isEmpty()) {
            Toast.makeText(this, "Nom et Prénom sont obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dbHelper.updateUserProfile(userEmail, nom, prenom, telephone, selectedImageUri);

        if (success) {
            Toast.makeText(this, "Profil mis à jour avec succès !", Toast.LENGTH_SHORT).show();
            // Refresh local data to update header
            loadUserData();
        } else {
            Toast.makeText(this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
        }
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier le mot de passe");

        // Custom Layout for Dialog
        View view = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        EditText etOldPass = view.findViewById(R.id.etOldPassword);
        EditText etNewPass = view.findViewById(R.id.etNewPassword);
        EditText etConfirmPass = view.findViewById(R.id.etConfirmPassword);

        builder.setView(view);

        builder.setPositiveButton("Modifier", (dialog, which) -> {
            // This listener will be overridden to prevent auto-close on error
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Override Positive Button
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String oldPass = etOldPass.getText().toString();
            String newPass = etNewPass.getText().toString();
            String confirmPass = etConfirmPass.getText().toString();

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Tous les champs sont requis", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!dbHelper.checkPassword(userEmail, oldPass)) {
                etOldPass.setError("Mot de passe actuel incorrect");
                return;
            }

            if (!newPass.equals(confirmPass)) {
                etConfirmPass.setError("Les mots de passe ne correspondent pas");
                return;
            }

            if (newPass.length() < 6) {
                etNewPass.setError("Le mot de passe doit contenir au moins 6 caractères");
                return;
            }

            if (dbHelper.updatePassword(userEmail, newPass)) {
                Toast.makeText(this, "Mot de passe modifié avec succès", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Erreur lors de la modification", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Mon Profil");
            }
            toolbar.setNavigationOnClickListener(v -> finish());
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
