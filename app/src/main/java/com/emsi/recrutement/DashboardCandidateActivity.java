package com.emsi.recrutement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;

public class DashboardCandidateActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String userEmail;
    private User currentUser;
    private RecyclerView rvRecentJobs;
    private JobOfferAdapter recentJobsAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_candidate);

        // Initialiser la base de données
        dbHelper = new DatabaseHelper(this);

        // Récupérer l'email depuis l'intent
        userEmail = getIntent().getStringExtra("USER_EMAIL");
        if (userEmail == null) {
            // Si pas d'email, rediriger vers login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Charger les données utilisateur
        loadUserData();

        setupNavigation();
        setupToolbar();
        setupDrawer();
        setupRecentJobs();
    }

    private void setupToolbar() {
        // Burger Menu Icon
        View ivBurgerMenu = findViewById(R.id.ivBurgerMenu);
        if (ivBurgerMenu != null) {
            ivBurgerMenu.setOnClickListener(v -> {
                if (drawerLayout != null) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        // Notification Icon
        View ivNotification = findViewById(R.id.ivNotification);
        if (ivNotification != null) {
            ivNotification.setOnClickListener(
                    v -> Toast.makeText(this, "Pas de nouvelles notifications", Toast.LENGTH_SHORT).show());
        }

        // Profile Icon
        View ivProfile = findViewById(R.id.ivProfile);
        if (ivProfile != null) {
            ivProfile.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProfileCandidateActivity.class);
                intent.putExtra("USER_EMAIL", userEmail);
                startActivity(intent);
            });
        }

        // Load profile image
        loadProfileImage();
    }

    private void loadProfileImage() {
        CircleImageView ivProfile = findViewById(R.id.ivProfile);
        if (ivProfile != null && currentUser != null) {
            String imageUri = currentUser.getImageUri();
            if (imageUri != null && !imageUri.isEmpty()) {
                try {
                    ivProfile.setImageURI(Uri.parse(imageUri));
                } catch (Exception e) {
                    ivProfile.setImageResource(R.drawable.ic_profile_placeholder);
                }
            } else {
                ivProfile.setImageResource(R.drawable.ic_profile_placeholder);
            }
        }
    }

    private void loadUserData() {
        currentUser = dbHelper.getUserByEmail(userEmail);
        if (currentUser != null) {
            TextView tvWelcome = findViewById(R.id.tvWelcome);
            if (tvWelcome != null) {
                tvWelcome.setText("Bonjour, " + currentUser.getPrenom() + " " + currentUser.getNom());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh user data and profile image when returning from profile screen
        loadUserData();
        loadProfileImage();
    }

    private void setupNavigation() {
        // Search Jobs - attacher le listener au LinearLayout interne
        View searchJobsLayout = findViewById(R.id.layoutSearchJobs);
        if (searchJobsLayout != null) {
            searchJobsLayout.setOnClickListener(v -> {
                Intent intent = new Intent(this, JobSearchActivity.class);
                intent.putExtra("USER_EMAIL", userEmail);
                startActivity(intent);
            });
        }

        // Upload CV - attacher le listener au LinearLayout interne
        View uploadCvLayout = findViewById(R.id.layoutUploadCV);
        if (uploadCvLayout != null) {
            uploadCvLayout.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProfileCandidateActivity.class);
                intent.putExtra("USER_EMAIL", userEmail);
                startActivity(intent);
            });
        }

        // My Applications - attacher le listener au LinearLayout interne
        View myApplicationsLayout = findViewById(R.id.layoutMyApplications);
        if (myApplicationsLayout != null) {
            myApplicationsLayout.setOnClickListener(v -> {
                Intent intent = new Intent(this, MyApplicationsActivity.class);
                intent.putExtra("USER_EMAIL", userEmail);
                startActivity(intent);
            });
        }

        // Career Tips - attacher le listener au LinearLayout interne
        View careerTipsLayout = findViewById(R.id.layoutCareerTips);
        if (careerTipsLayout != null) {
            careerTipsLayout.setOnClickListener(v -> {
                Intent intent = new Intent(this, CareerTipsActivity.class);
                intent.putExtra("USER_EMAIL", userEmail);
                startActivity(intent);
            });
        }

        // View All recent jobs
        TextView tvViewAll = findViewById(R.id.tvViewAll);
        if (tvViewAll != null) {
            tvViewAll.setOnClickListener(v -> {
                Intent intent = new Intent(this, JobSearchActivity.class);
                intent.putExtra("USER_EMAIL", userEmail);
                startActivity(intent);
            });
        }

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    // Déjà sur le dashboard
                    return true;
                } else if (itemId == R.id.nav_search) {
                    Intent intent = new Intent(this, JobSearchActivity.class);
                    intent.putExtra("USER_EMAIL", userEmail);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_applications) {
                    Intent intent = new Intent(this, MyApplicationsActivity.class);
                    intent.putExtra("USER_EMAIL", userEmail);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(this, ProfileCandidateActivity.class);
                    intent.putExtra("USER_EMAIL", userEmail);
                    startActivity(intent);
                    return true;
                }
                return false;
            });
        }

        // Logout button
        Button btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                // Nettoyer les préférences partagées si nécessaire
                SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                prefs.edit().clear().apply();

                // Rediriger vers LoginActivity
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    private void setupRecentJobs() {
        rvRecentJobs = findViewById(R.id.rvRecentJobs);
        if (rvRecentJobs != null) {
            // Configuration du RecyclerView vertical (l'une au-dessus de l'autre)
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            rvRecentJobs.setLayoutManager(layoutManager);

            // Charger les offres récentes (limiter à 5 pour l'affichage vertical)
            List<JobOffer> recentJobs = dbHelper.getAllJobs();
            if (recentJobs.size() > 5) {
                recentJobs = recentJobs.subList(0, 5);
            }

            // Créer et configurer l'adapter avec JobOfferAdapter (pour les cartes
            // verticales)
            recentJobsAdapter = new JobOfferAdapter(recentJobs, new JobOfferAdapter.OnJobClickListener() {
                @Override
                public void onJobClick(JobOffer job) {
                    Intent intent = new Intent(DashboardCandidateActivity.this, JobDetailsActivity.class);
                    intent.putExtra("JOB_ID", job.getId());
                    intent.putExtra("USER_EMAIL", userEmail);
                    startActivity(intent);
                }

                @Override
                public void onSaveJobClick(JobOffer job) {
                    // Fonctionnalité de sauvegarde à implémenter si nécessaire
                }
            });
            rvRecentJobs.setAdapter(recentJobsAdapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    private void setupDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        if (navigationView != null) {
            // Update header with user data
            View headerView = navigationView.getHeaderView(0);
            if (headerView != null && currentUser != null) {
                TextView navHeaderName = headerView.findViewById(R.id.nav_header_name);
                TextView navHeaderEmail = headerView.findViewById(R.id.nav_header_email);
                CircleImageView navHeaderImage = headerView.findViewById(R.id.nav_header_profile_image);

                if (navHeaderName != null) {
                    navHeaderName.setText(currentUser.getFullName());
                }
                if (navHeaderEmail != null) {
                    navHeaderEmail.setText(currentUser.getEmail());
                }
                if (navHeaderImage != null && currentUser.getImageUri() != null
                        && !currentUser.getImageUri().isEmpty()) {
                    try {
                        navHeaderImage.setImageURI(Uri.parse(currentUser.getImageUri()));
                    } catch (Exception e) {
                        navHeaderImage.setImageResource(R.drawable.ic_profile_placeholder);
                    }
                }
            }

            // Handle menu item clicks
            navigationView.setNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(this, ProfileCandidateActivity.class);
                    intent.putExtra("USER_EMAIL", userEmail);
                    startActivity(intent);
                } else if (itemId == R.id.nav_favorites) {
                    Toast.makeText(this, "Favoris - Bientôt disponible", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.nav_applications) {
                    Intent intent = new Intent(this, MyApplicationsActivity.class);
                    intent.putExtra("USER_EMAIL", userEmail);
                    startActivity(intent);
                } else if (itemId == R.id.nav_cv) {
                    Toast.makeText(this, "Mes CV - Bientôt disponible", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.nav_calendar) {
                    Toast.makeText(this, "Calendrier - Bientôt disponible", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.nav_career) {
                    Intent intent = new Intent(this, CareerTipsActivity.class);
                    intent.putExtra("USER_EMAIL", userEmail);
                    startActivity(intent);
                } else if (itemId == R.id.nav_logout) {
                    logout();
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        }
    }

    private void logout() {
        // Clear any saved session data
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        prefs.edit().clear().apply();

        // Redirect to login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
