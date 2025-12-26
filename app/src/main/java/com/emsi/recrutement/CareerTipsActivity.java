package com.emsi.recrutement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CareerTipsActivity extends AppCompatActivity {

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_career_tips);

        // Récupérer l'email utilisateur
        userEmail = getIntent().getStringExtra("USER_EMAIL");
        if (userEmail == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setupToolbar();
        loadTips();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Conseils Carrière");
            }
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadTips() {
        // Charger et afficher les conseils carrière
        // Pour l'instant, on affiche un message simple
        TextView tvTips = findViewById(R.id.tvTips);
        if (tvTips != null) {
            String tips = "💼 Conseils pour votre carrière :\n\n" +
                    "1. Mettez à jour régulièrement votre CV\n" +
                    "2. Personnalisez votre lettre de motivation pour chaque candidature\n" +
                    "3. Préparez-vous bien aux entretiens\n" +
                    "4. Développez vos compétences en continu\n" +
                    "5. Construisez votre réseau professionnel\n" +
                    "6. Restez positif et persévérant\n\n" +
                    "Bonne chance dans votre recherche d'emploi !";
            tvTips.setText(tips);
        }
    }
}

