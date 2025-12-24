package com.emsi.recrutement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private TextInputLayout tilEmail, tilPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialiser DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Initialiser les vues
        initViews();

        // Configurer les écouteurs
        setupListeners();

        // Pour tester : pré-remplir les champs
        etEmail.setText("candidat@test.ma");
        etPassword.setText("test123");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());

        tvRegister.setOnClickListener(v -> {
            // Ouvrir l'inscription candidat
            Intent intent = new Intent(LoginActivity.this, RegisterCandidateActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        // Réinitialiser les erreurs
        tilEmail.setError(null);
        tilPassword.setError(null);

        // Récupérer les valeurs
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation simple
        if (email.isEmpty()) {
            tilEmail.setError("L'email est obligatoire");
            return;
        }

        if (password.isEmpty()) {
            tilPassword.setError("Le mot de passe est obligatoire");
            return;
        }

        // Vérifier les identifiants
        boolean isValid = dbHelper.checkUser(email, password);

        if (isValid) {
            Toast.makeText(LoginActivity.this,
                    "Connexion réussie!",
                    Toast.LENGTH_SHORT).show();

            // Ici, plus besoin de redirection vers RecruteurActivity ou CandidatActivity
            // Tu peux rester sur cette activité ou ouvrir MainActivity si tu en as une
            // Ex :
            // Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            // startActivity(intent);
            // finish();

        } else {
            Toast.makeText(LoginActivity.this,
                    "Email ou mot de passe incorrect",
                    Toast.LENGTH_SHORT).show();
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
