package com.emsi.recrutement;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MyCVsActivity extends AppCompatActivity implements CVAdapter.OnCVActionListener {

    private RecyclerView rvCVList;
    private LinearLayout layoutEmptyState;
    private FloatingActionButton fabAddCV;

    private DatabaseHelper dbHelper;
    private CVAdapter adapter;
    private String userEmail;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cvs);

        // Get user email
        userEmail = getIntent().getStringExtra("USER_EMAIL");
        if (userEmail == null) {
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        User user = dbHelper.getUserByEmail(userEmail);
        if (user != null) {
            userId = user.getId();
        }

        setupToolbar();
        initViews();
        setupRecyclerView();
        setupFAB();
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
        rvCVList = findViewById(R.id.rvCVList);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        fabAddCV = findViewById(R.id.fabAddCV);
    }

    private void setupRecyclerView() {
        adapter = new CVAdapter(null, this);
        androidx.recyclerview.widget.GridLayoutManager gridLayoutManager = new androidx.recyclerview.widget.GridLayoutManager(
                this, 2);
        rvCVList.setLayoutManager(gridLayoutManager);
        rvCVList.setAdapter(adapter);
    }

    private void setupFAB() {
        fabAddCV.setOnClickListener(v -> {
            Intent intent = new Intent(this, UploadCVActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCVs();
    }

    private void loadCVs() {
        List<CV> cvList = dbHelper.getUserCVs(userId);

        if (cvList == null || cvList.isEmpty()) {
            rvCVList.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvCVList.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
            adapter.updateCVs(cvList);
        }
    }

    @Override
    public void onCVClick(CV cv) {
        // Open CV file
        try {
            Uri uri;
            String fileUriString = cv.getFileUri();

            // Check if it's a local file path (starts with /) or a content URI
            if (fileUriString.startsWith("/")) {
                java.io.File file = new java.io.File(fileUriString);
                uri = androidx.core.content.FileProvider.getUriForFile(
                        this,
                        getApplicationContext().getPackageName() + ".provider",
                        file);
            } else {
                uri = Uri.parse(fileUriString);
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);

            // Determine MIME type based on file extension
            String fileName = cv.getFileName().toLowerCase();
            String mimeType = "application/pdf";
            if (fileName.endsWith(".doc")) {
                mimeType = "application/msword";
            } else if (fileName.endsWith(".docx")) {
                mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            }

            intent.setDataAndType(uri, mimeType);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Try to open with chooser
            try {
                startActivity(Intent.createChooser(intent, "Ouvrir le CV"));
            } catch (android.content.ActivityNotFoundException e) {
                // If no app can handle it, prompt to install
                showInstallPDFReaderDialog();
            }
        } catch (SecurityException se) {
            android.util.Log.e("MyCVsActivity", "Permission error: " + se.getMessage(), se);
            new AlertDialog.Builder(this)
                    .setTitle("Fichier inaccessible")
                    .setMessage(
                            "Ce CV utilise un ancien format de lien qui n'est plus valide. Veuillez le supprimer et le réimporter.")
                    .setPositiveButton("OK", null)
                    .show();
        } catch (Exception e) {
            android.util.Log.e("MyCVsActivity", "Error opening CV: " + e.getMessage(), e);
            Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showInstallPDFReaderDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Application manquante")
                .setMessage("Aucune application trouvée pour ouvrir ce fichier. Voulez-vous installer un lecteur PDF ?")
                .setPositiveButton("Télécharger", (dialog, which) -> {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pdf reader")));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/search?q=pdf reader")));
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override
    public void onDeleteClick(CV cv) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer le CV")
                .setMessage("Voulez-vous vraiment supprimer \"" + cv.getTitre() + "\" ?")
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    boolean success = dbHelper.deleteCV(cv.getId());
                    if (success) {
                        Toast.makeText(this, "CV supprimé", Toast.LENGTH_SHORT).show();
                        loadCVs(); // Refresh list
                    } else {
                        Toast.makeText(this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
