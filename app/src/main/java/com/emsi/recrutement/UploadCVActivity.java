package com.emsi.recrutement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UploadCVActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_FILE = 100;

    private TextInputEditText etTitre;
    private TextInputEditText etDomaine;
    private MaterialButton btnSelectFile;
    private MaterialButton btnSave;
    private LinearLayout layoutSelectedFile;
    private TextView tvSelectedFileName;

    private DatabaseHelper dbHelper;
    private String userEmail;
    private int userId;
    private Uri selectedFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_cv);

        // Get user email from intent
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
        setupListeners();
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
        etTitre = findViewById(R.id.etTitre);
        etDomaine = findViewById(R.id.etDomaine);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnSave = findViewById(R.id.btnSave);
        layoutSelectedFile = findViewById(R.id.layoutSelectedFile);
        tvSelectedFileName = findViewById(R.id.tvSelectedFileName);
    }

    private void setupListeners() {
        btnSelectFile.setOnClickListener(v -> openFilePicker());
        btnSave.setOnClickListener(v -> saveCV());
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimeTypes = {
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        };
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Sélectionner un fichier"), REQUEST_CODE_PICK_FILE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Aucune application de fichiers trouvée", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                selectedFileUri = data.getData();

                // Get file name
                String fileName = getFileName(selectedFileUri);

                // Show selected file
                tvSelectedFileName.setText(fileName);
                layoutSelectedFile.setVisibility(View.VISIBLE);

                // Enable save button
                btnSave.setEnabled(true);

                // Take persistable permission
                try {
                    getContentResolver().takePersistableUriPermission(
                            selectedFileUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (Exception e) {
                    // Permission not available, file will still work for this session
                }
            }
        }
    }

    private String getFileName(Uri uri) {
        String fileName = "document.pdf";
        android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex);
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return fileName;
    }

    private String saveFileToInternalStorage(Uri sourceUri, String fileName) {
        try {
            java.io.InputStream inputStream = getContentResolver().openInputStream(sourceUri);
            if (inputStream == null)
                return null;

            java.io.File directory = new java.io.File(getFilesDir(), "cvs");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            java.io.File destinationFile = new java.io.File(directory, fileName);
            java.io.FileOutputStream outputStream = new java.io.FileOutputStream(destinationFile);

            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return destinationFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveCV() {
        String titre = etTitre.getText() != null ? etTitre.getText().toString().trim() : "";
        String domaine = etDomaine.getText() != null ? etDomaine.getText().toString().trim() : "";

        // Validation
        if (TextUtils.isEmpty(titre)) {
            etTitre.setError("Le titre est requis");
            etTitre.requestFocus();
            return;
        }

        if (selectedFileUri == null) {
            Toast.makeText(this, "Veuillez sélectionner un fichier", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if userId is valid
        if (userId == 0) {
            Toast.makeText(this, "Erreur: utilisateur non identifié", Toast.LENGTH_SHORT).show();
            android.util.Log.e("UploadCVActivity", "userId is 0, userEmail: " + userEmail);
            return;
        }

        // Save file locally
        String fileName = getFileName(selectedFileUri);
        String savedPath = saveFileToInternalStorage(selectedFileUri, fileName);

        if (savedPath == null) {
            Toast.makeText(this, "Erreur lors de la copie du fichier", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current date
        String uploadDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        android.util.Log.d("UploadCVActivity",
                "Saving CV - userId: " + userId + ", titre: " + titre + ", domaine: " + domaine);

        // Save to database
        boolean success = dbHelper.addCV(
                userId,
                titre,
                domaine,
                savedPath, // Save local path
                fileName,
                uploadDate);

        if (success) {
            Toast.makeText(this, "CV enregistré avec succès ✓", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Erreur lors de l'enregistrement. Vérifiez les logs.", Toast.LENGTH_LONG).show();
            android.util.Log.e("UploadCVActivity", "Failed to save CV");
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
