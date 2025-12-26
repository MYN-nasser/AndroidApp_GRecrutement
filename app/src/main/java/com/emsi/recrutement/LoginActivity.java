package com.emsi.recrutement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private TextInputLayout tilEmail, tilPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialiser la base de données
        dbHelper = new DatabaseHelper(this);

        // Initialiser les vues (widgets de l'interface)
        initViews();

        // Configurer les écouteurs de clics (pour les boutons)
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email_login);
        etPassword = findViewById(R.id.et_password_login);
        tilEmail = findViewById(R.id.til_email_login);
        tilPassword = findViewById(R.id.til_password_login);
        btnLogin = findViewById(R.id.btn_login);
        tvRegisterLink = findViewById(R.id.tv_register_link);
    }

    private void setupListeners() {
        // Clic sur le bouton principal "Se Connecter"
        btnLogin.setOnClickListener(v -> loginUser());

        // Clic sur le lien "Pas encore de compte ? Inscrivez-vous"
        tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterCandidateActivity.class);
            startActivity(intent);
            finish(); // On ferme la page de login pour ne pas y revenir
        });
    }

    private void loginUser() {
        // Réinitialiser les messages d'erreur précédents
        tilEmail.setError(null);
        tilPassword.setError(null);

        // Récupérer le texte saisi par l'utilisateur
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Vérifier que les champs ne sont pas vides
        if (email.isEmpty()) {
            tilEmail.setError("L'email ne peut pas être vide");
            return; // Arrêter la fonction ici
        }
        if (password.isEmpty()) {
            tilPassword.setError("Le mot de passe ne peut pas être vide");
            return; // Arrêter la fonction ici
        }

        // --- C'EST LA PARTIE LA PLUS IMPORTANTE ---
        // Vérifier si l'utilisateur existe dans la base de données
        boolean isValid = dbHelper.checkUser(email, password);

        if (isValid) {
            // SUCCÈS : L'utilisateur est valide
            Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show();

            // 1. Créer une intention pour aller vers le tableau de bord
            Intent intent = new Intent(LoginActivity.this, DashboardCandidateActivity.class);
            // Passer l'email pour récupérer les données utilisateur
            intent.putExtra("USER_EMAIL", email);

            // 2. Démarrer la nouvelle activité (la redirection)
            startActivity(intent);

            // 3. Fermer l'activité de login pour empêcher l'utilisateur d'y revenir avec le bouton "Retour"
            finish();

        } else {
            // ÉCHEC : Identifiants incorrects
            Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_LONG).show();
            // Optionnel : afficher une erreur visuelle
            tilPassword.setError(" "); // Met un espace pour afficher l'erreur sans message textuel
        }
    }

    // Bonne pratique : fermer la connexion à la base de données quand l'activité est détruite
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}