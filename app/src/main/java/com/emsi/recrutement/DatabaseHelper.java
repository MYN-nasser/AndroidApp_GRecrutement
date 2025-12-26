package com.emsi.recrutement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "recrutement.db";
    private static final int DATABASE_VERSION = 4;

    // Table utilisateurs
    public static final String TABLE_USERS = "utilisateurs";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_NOM = "nom";
    public static final String COLUMN_PRENOM = "prenom";
    public static final String COLUMN_TELEPHONE = "telephone";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_IMAGE_URI = "image_uri";

    // Table offres d'emploi
    public static final String TABLE_JOBS = "offres_emploi";
    public static final String COLUMN_JOB_ID = "job_id";
    public static final String COLUMN_JOB_TITLE = "titre";
    public static final String COLUMN_JOB_COMPANY = "entreprise";
    public static final String COLUMN_JOB_LOCATION = "lieu";
    public static final String COLUMN_JOB_SALARY = "salaire";
    public static final String COLUMN_JOB_DESCRIPTION = "description";
    public static final String COLUMN_JOB_REQUIREMENTS = "exigences";
    public static final String COLUMN_JOB_TYPE = "type_emploi"; // CDI, CDD, Stage, etc.

    // Table candidatures
    public static final String TABLE_APPLICATIONS = "candidatures";
    public static final String COLUMN_APPLICATION_ID = "application_id";
    public static final String COLUMN_APPLICATION_USER_ID = "user_id";
    public static final String COLUMN_APPLICATION_JOB_ID = "job_id";
    public static final String COLUMN_APPLICATION_STATUS = "statut"; // En attente, Acceptée, Refusée
    public static final String COLUMN_APPLICATION_DATE = "date_candidature";
    public static final String COLUMN_APPLICATION_INTERVIEW = "interview_scheduled"; // 0 or 1

    // Table saved jobs (offres enregistrées)
    public static final String TABLE_SAVED_JOBS = "saved_jobs";
    public static final String COLUMN_SAVED_ID = "saved_id";
    public static final String COLUMN_SAVED_USER_ID = "user_id";
    public static final String COLUMN_SAVED_JOB_ID = "job_id";
    public static final String COLUMN_SAVED_DATE = "saved_date";

    // Requête de création de table utilisateurs
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_EMAIL + " TEXT UNIQUE NOT NULL," +
            COLUMN_PASSWORD + " TEXT NOT NULL," +
            COLUMN_NOM + " TEXT NOT NULL," +
            COLUMN_PRENOM + " TEXT NOT NULL," +
            COLUMN_TELEPHONE + " TEXT," +
            COLUMN_TYPE + " TEXT NOT NULL," +
            COLUMN_IMAGE_URI + " TEXT" +
            ");";

    // Requête de création de table offres d'emploi
    private static final String CREATE_TABLE_JOBS = "CREATE TABLE " + TABLE_JOBS + "(" +
            COLUMN_JOB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_JOB_TITLE + " TEXT NOT NULL," +
            COLUMN_JOB_COMPANY + " TEXT NOT NULL," +
            COLUMN_JOB_LOCATION + " TEXT," +
            COLUMN_JOB_SALARY + " TEXT," +
            COLUMN_JOB_DESCRIPTION + " TEXT," +
            COLUMN_JOB_REQUIREMENTS + " TEXT," +
            COLUMN_JOB_TYPE + " TEXT" +
            ");";

    // Requête de création de table candidatures
    private static final String CREATE_TABLE_APPLICATIONS = "CREATE TABLE " + TABLE_APPLICATIONS + "(" +
            COLUMN_APPLICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_APPLICATION_USER_ID + " INTEGER NOT NULL," +
            COLUMN_APPLICATION_JOB_ID + " INTEGER NOT NULL," +
            COLUMN_APPLICATION_STATUS + " TEXT DEFAULT 'En attente'," +
            COLUMN_APPLICATION_DATE + " TEXT DEFAULT (datetime('now'))," +
            "FOREIGN KEY(" + COLUMN_APPLICATION_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")," +
            "FOREIGN KEY(" + COLUMN_APPLICATION_JOB_ID + ") REFERENCES " + TABLE_JOBS + "(" + COLUMN_JOB_ID + ")" +
            ");";

    // Requête de création de table saved jobs
    private static final String CREATE_TABLE_SAVED_JOBS = "CREATE TABLE " + TABLE_SAVED_JOBS + "(" +
            COLUMN_SAVED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_SAVED_USER_ID + " INTEGER NOT NULL," +
            COLUMN_SAVED_JOB_ID + " INTEGER NOT NULL," +
            COLUMN_SAVED_DATE + " TEXT DEFAULT (datetime('now'))," +
            "FOREIGN KEY(" + COLUMN_SAVED_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")," +
            "FOREIGN KEY(" + COLUMN_SAVED_JOB_ID + ") REFERENCES " + TABLE_JOBS + "(" + COLUMN_JOB_ID + ")," +
            "UNIQUE(" + COLUMN_SAVED_USER_ID + "," + COLUMN_SAVED_JOB_ID + ")" +
            ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_USERS);
            db.execSQL(CREATE_TABLE_JOBS);
            db.execSQL(CREATE_TABLE_APPLICATIONS);
            db.execSQL(CREATE_TABLE_SAVED_JOBS);
            Log.d("DatabaseHelper", "Tables créées avec succès");

            // Ajouter des utilisateurs de test
            addTestUsers(db);
            // Ajouter des offres d'emploi de test
            addTestJobs(db);

        } catch (Exception e) {
            Log.e("DatabaseHelper", "Erreur création table: " + e.getMessage());
        }
    }

    private void addTestUsers(SQLiteDatabase db) {
        // Utilisateur 1: Recruteur
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, "admin@emsi.ma");
        values.put(COLUMN_PASSWORD, "admin123");
        values.put(COLUMN_NOM, "Admin");
        values.put(COLUMN_PRENOM, "EMSI");
        values.put(COLUMN_TYPE, "recruteur");
        long id1 = db.insert(TABLE_USERS, null, values);
        Log.d("DatabaseHelper", "Admin inséré avec ID: " + id1);

        // Utilisateur 2: Candidat
        values.clear();
        values.put(COLUMN_EMAIL, "candidat@test.ma");
        values.put(COLUMN_PASSWORD, "test123");
        values.put(COLUMN_NOM, "Test");
        values.put(COLUMN_PRENOM, "Candidat");
        values.put(COLUMN_TYPE, "candidat");
        long id2 = db.insert(TABLE_USERS, null, values);
        Log.d("DatabaseHelper", "Candidat inséré avec ID: " + id2);

        // Utilisateur 3: Lauréat
        values.clear();
        values.put(COLUMN_EMAIL, "laureat@emsi.ma");
        values.put(COLUMN_PASSWORD, "laureat123");
        values.put(COLUMN_NOM, "Lauréat");
        values.put(COLUMN_PRENOM, "EMSI");
        values.put(COLUMN_TYPE, "candidat");
        long id3 = db.insert(TABLE_USERS, null, values);
        Log.d("DatabaseHelper", "Lauréat inséré avec ID: " + id3);
    }

    private void addTestJobs(SQLiteDatabase db) {
        // Offre 1
        ContentValues values = new ContentValues();
        values.put(COLUMN_JOB_TITLE, "Développeur Android");
        values.put(COLUMN_JOB_COMPANY, "TechCorp");
        values.put(COLUMN_JOB_LOCATION, "Casablanca");
        values.put(COLUMN_JOB_SALARY, "15 000 - 20 000 MAD");
        values.put(COLUMN_JOB_DESCRIPTION,
                "Nous recherchons un développeur Android expérimenté pour rejoindre notre équipe. Vous serez responsable du développement et de la maintenance d'applications mobiles.");
        values.put(COLUMN_JOB_REQUIREMENTS,
                "Bac+5 en informatique, 3 ans d'expérience minimum, maîtrise de Java/Kotlin");
        values.put(COLUMN_JOB_TYPE, "CDI");
        db.insert(TABLE_JOBS, null, values);

        // Offre 2
        values.clear();
        values.put(COLUMN_JOB_TITLE, "Ingénieur Full Stack");
        values.put(COLUMN_JOB_COMPANY, "Digital Solutions");
        values.put(COLUMN_JOB_LOCATION, "Rabat");
        values.put(COLUMN_JOB_SALARY, "18 000 - 25 000 MAD");
        values.put(COLUMN_JOB_DESCRIPTION,
                "Poste d'ingénieur full stack pour développer des solutions web et mobiles innovantes.");
        values.put(COLUMN_JOB_REQUIREMENTS, "Bac+5, maîtrise de React, Node.js, bases de données");
        values.put(COLUMN_JOB_TYPE, "CDI");
        db.insert(TABLE_JOBS, null, values);

        // Offre 3
        values.clear();
        values.put(COLUMN_JOB_TITLE, "Stagiaire Développement Web");
        values.put(COLUMN_JOB_COMPANY, "StartupTech");
        values.put(COLUMN_JOB_LOCATION, "Marrakech");
        values.put(COLUMN_JOB_SALARY, "3 000 - 5 000 MAD");
        values.put(COLUMN_JOB_DESCRIPTION,
                "Stage de 6 mois pour un étudiant en dernière année. Opportunité d'apprendre et de contribuer à des projets réels.");
        values.put(COLUMN_JOB_REQUIREMENTS, "Étudiant en dernière année, connaissances en HTML/CSS/JavaScript");
        values.put(COLUMN_JOB_TYPE, "Stage");
        db.insert(TABLE_JOBS, null, values);

        // Offre 4
        values.clear();
        values.put(COLUMN_JOB_TITLE, "Data Analyst");
        values.put(COLUMN_JOB_COMPANY, "DataCorp");
        values.put(COLUMN_JOB_LOCATION, "Casablanca");
        values.put(COLUMN_JOB_SALARY, "12 000 - 18 000 MAD");
        values.put(COLUMN_JOB_DESCRIPTION, "Analyser des données pour aider à la prise de décision stratégique.");
        values.put(COLUMN_JOB_REQUIREMENTS, "Bac+5, maîtrise de Python, SQL, Excel");
        values.put(COLUMN_JOB_TYPE, "CDD");
        db.insert(TABLE_JOBS, null, values);

        // Offre 5
        values.clear();
        values.put(COLUMN_JOB_TITLE, "Chef de Projet IT");
        values.put(COLUMN_JOB_COMPANY, "ProjetManager Inc");
        values.put(COLUMN_JOB_LOCATION, "Tanger");
        values.put(COLUMN_JOB_SALARY, "20 000 - 30 000 MAD");
        values.put(COLUMN_JOB_DESCRIPTION,
                "Gérer des projets informatiques de A à Z, coordonner les équipes techniques.");
        values.put(COLUMN_JOB_REQUIREMENTS, "Bac+5, 5 ans d'expérience, certification PMP un plus");
        values.put(COLUMN_JOB_TYPE, "CDI");
        db.insert(TABLE_JOBS, null, values);

        Log.d("DatabaseHelper", "Offres d'emploi de test ajoutées");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLICATIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOBS);
            db.execSQL(CREATE_TABLE_JOBS);
            db.execSQL(CREATE_TABLE_APPLICATIONS);
            addTestJobs(db);
        }
        if (oldVersion < 3) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_IMAGE_URI + " TEXT");
            } catch (Exception e) {
                Log.e("DatabaseHelper", "Erreur mise à jour V3: " + e.getMessage());
            }
        }
        if (oldVersion < 4) {
            try {
                // Create saved_jobs table
                db.execSQL(CREATE_TABLE_SAVED_JOBS);
                // Add interview_scheduled column to candidatures
                db.execSQL("ALTER TABLE " + TABLE_APPLICATIONS + " ADD COLUMN " + COLUMN_APPLICATION_INTERVIEW
                        + " INTEGER DEFAULT 0");
                Log.d("DatabaseHelper", "Mise à jour V4: saved_jobs table et interview_scheduled ajoutés");
            } catch (Exception e) {
                Log.e("DatabaseHelper", "Erreur mise à jour V4: " + e.getMessage());
            }
        }
    }

    // Méthode SIMPLIFIÉE pour vérifier login
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Pour debug
        Log.d("DatabaseHelper", "Vérification: " + email + " / " + password);

        try {
            String query = "SELECT * FROM " + TABLE_USERS +
                    " WHERE email = ? AND password = ?";
            Cursor cursor = db.rawQuery(query, new String[] { email, password });

            boolean exists = cursor.getCount() > 0;

            Log.d("DatabaseHelper", "Résultat: " + exists +
                    " (lignes trouvées: " + cursor.getCount() + ")");

            // Afficher tous les utilisateurs pour debug
            Cursor allUsers = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
            while (allUsers.moveToNext()) {
                Log.d("DatabaseHelper", "User: " +
                        allUsers.getString(allUsers.getColumnIndexOrThrow(COLUMN_EMAIL)) +
                        " / " +
                        allUsers.getString(allUsers.getColumnIndexOrThrow(COLUMN_PASSWORD)));
            }
            allUsers.close();

            cursor.close();
            db.close();
            return exists;

        } catch (Exception e) {
            Log.e("DatabaseHelper", "Erreur checkUser: " + e.getMessage());
            db.close();
            return false;
        }
    }

    // Méthode pour obtenir le type d'utilisateur
    public String getUserType(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String type = "candidat"; // valeur par défaut

        try {
            String query = "SELECT type FROM " + TABLE_USERS + " WHERE email = ?";
            Cursor cursor = db.rawQuery(query, new String[] { email });

            if (cursor.moveToFirst()) {
                int typeIndex = cursor.getColumnIndex("type");
                if (typeIndex != -1) {
                    type = cursor.getString(typeIndex);
                }
            }

            cursor.close();

        } catch (Exception e) {
            Log.e("DatabaseHelper", "Erreur getUserType: " + e.getMessage());
        }

        db.close();
        return type;
    }

    // Méthode pour vérifier si email existe
    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String query = "SELECT * FROM " + TABLE_USERS + " WHERE email = ?";
            Cursor cursor = db.rawQuery(query, new String[] { email });

            boolean exists = cursor.getCount() > 0;
            cursor.close();
            db.close();
            return exists;

        } catch (Exception e) {
            Log.e("DatabaseHelper", "Erreur checkEmailExists: " + e.getMessage());
            db.close();
            return false;
        }
    }

    // Méthode pour créer un nouvel utilisateur
    public long addUser(String email, String password, String nom, String prenom, String type) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_EMAIL, email);
            values.put(COLUMN_PASSWORD, password);
            values.put(COLUMN_NOM, nom);
            values.put(COLUMN_PRENOM, prenom);
            values.put(COLUMN_TYPE, type);

            long result = db.insert(TABLE_USERS, null, values);
            Log.d("DatabaseHelper", "Utilisateur ajouté avec ID: " + result);

            db.close();
            return result;

        } catch (Exception e) {
            Log.e("DatabaseHelper", "Erreur addUser: " + e.getMessage());
            db.close();
            return -1;
        }
    }

    // Méthode pour obtenir un utilisateur par email
    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        try {
            String query = "SELECT * FROM " + TABLE_USERS + " WHERE email = ?";
            Cursor cursor = db.rawQuery(query, new String[] { email });

            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String nom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOM));
                String prenom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRENOM));
                String telephone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEPHONE));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                String imageUri = null;
                int imageIndex = cursor.getColumnIndex(COLUMN_IMAGE_URI);
                if (imageIndex != -1) {
                    imageUri = cursor.getString(imageIndex);
                }

                user = new User(id, email, nom, prenom, telephone, type, imageUri);
            }

            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Erreur getUserByEmail: " + e.getMessage());
        }

        db.close();
        return user;
    }

    // Méthode pour mettre à jour les informations utilisateur (Profil complet)
    public boolean updateUserProfile(String email, String nom, String prenom, String telephone, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NOM, nom);
            values.put(COLUMN_PRENOM, prenom);
            if (telephone != null) {
                values.put(COLUMN_TELEPHONE, telephone);
            }
            if (imageUri != null) {
                values.put(COLUMN_IMAGE_URI, imageUri);
            }

            int rowsAffected = db.update(TABLE_USERS, values, COLUMN_EMAIL + " = ?", new String[] { email });
            db.close();
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Erreur updateUserProfile: " + e.getMessage());
            db.close();
            return false;
        }
    }

    // Méthode pour vérifier le mot de passe actuel
    public boolean checkPassword(String email, String password) {
        return checkUser(email, password);
    }

    // Méthode pour changer le mot de passe
    public boolean updatePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PASSWORD, newPassword);

            int rowsAffected = db.update(TABLE_USERS, values, COLUMN_EMAIL + " = ?", new String[] { email });
            db.close();
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Erreur updatePassword: " + e.getMessage());
            db.close();
            return false;
        }
    }

    // Méthode pour obtenir toutes les offres d'emploi
    public java.util.List<JobOffer> getAllJobs() {
        java.util.List<JobOffer> jobs = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String query = "SELECT * FROM " + TABLE_JOBS + " ORDER BY " + COLUMN_JOB_ID + " DESC";
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_JOB_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_TITLE));
                String company = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_COMPANY));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_LOCATION));
                String salary = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_SALARY));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_DESCRIPTION));
                String requirements = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_REQUIREMENTS));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_TYPE));

                jobs.add(new JobOffer(id, title, company, location, salary, description, requirements, type));
            }

            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Erreur getAllJobs: " + e.getMessage());
        }

        db.close();
        return jobs;
    }

    // Méthode pour rechercher des offres
    public java.util.List<JobOffer> searchJobs(String searchQuery) {
        java.util.List<JobOffer> jobs = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String query = "SELECT * FROM " + TABLE_JOBS +
                    " WHERE " + COLUMN_JOB_TITLE + " LIKE ? OR " +
                    COLUMN_JOB_COMPANY + " LIKE ? OR " +
                    COLUMN_JOB_DESCRIPTION + " LIKE ?" +
                    " ORDER BY " + COLUMN_JOB_ID + " DESC";
            String searchPattern = "%" + searchQuery + "%";
            Cursor cursor = db.rawQuery(query, new String[] { searchPattern, searchPattern, searchPattern });

            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_JOB_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_TITLE));
                String company = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_COMPANY));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_LOCATION));
                String salary = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_SALARY));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_DESCRIPTION));
                String requirements = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_REQUIREMENTS));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_TYPE));

                jobs.add(new JobOffer(id, title, company, location, salary, description, requirements, type));
            }

            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Erreur searchJobs: " + e.getMessage());
        }

        db.close();
        return jobs;
    }

    // Méthode pour obtenir une offre par ID
    public JobOffer getJobById(int jobId) {
        SQLiteDatabase db = this.getReadableDatabase();
        JobOffer job = null;

        try {
            String query = "SELECT * FROM " + TABLE_JOBS + " WHERE " + COLUMN_JOB_ID + " = ?";
            Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(jobId) });

            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_JOB_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_TITLE));
                String company = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_COMPANY));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_LOCATION));
                String salary = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_SALARY));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_DESCRIPTION));
                String requirements = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_REQUIREMENTS));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_TYPE));

                job = new JobOffer(id, title, company, location, salary, description, requirements, type);
            }

            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Erreur getJobById: " + e.getMessage());
        }

        db.close();
        return job;
    }

    // Méthode pour ajouter une candidature
    public long addApplication(int userId, int jobId) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            // Vérifier si la candidature existe déjà
            String checkQuery = "SELECT * FROM " + TABLE_APPLICATIONS +
                    " WHERE " + COLUMN_APPLICATION_USER_ID + " = ? AND " +
                    COLUMN_APPLICATION_JOB_ID + " = ?";
            Cursor cursor = db.rawQuery(checkQuery, new String[] { String.valueOf(userId), String.valueOf(jobId) });

            if (cursor.getCount() > 0) {
                cursor.close();
                db.close();
                return -1; // Candidature déjà existante
            }
            cursor.close();

            ContentValues values = new ContentValues();
            values.put(COLUMN_APPLICATION_USER_ID, userId);
            values.put(COLUMN_APPLICATION_JOB_ID, jobId);
            values.put(COLUMN_APPLICATION_STATUS, "En attente");

            long result = db.insert(TABLE_APPLICATIONS, null, values);
            Log.d("DatabaseHelper", "Candidature ajoutée avec ID: " + result);

            db.close();
            return result;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Erreur addApplication: " + e.getMessage());
            db.close();
            return -1;
        }
    }

    // Méthode pour obtenir les candidatures d'un utilisateur
    public java.util.List<ApplicationInfo> getUserApplications(int userId) {
        java.util.List<ApplicationInfo> applications = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String query = "SELECT a.*, j." + COLUMN_JOB_TITLE + ", j." + COLUMN_JOB_COMPANY +
                    " FROM " + TABLE_APPLICATIONS + " a" +
                    " INNER JOIN " + TABLE_JOBS + " j ON a." + COLUMN_APPLICATION_JOB_ID + " = j." + COLUMN_JOB_ID +
                    " WHERE a." + COLUMN_APPLICATION_USER_ID + " = ?" +
                    " ORDER BY a." + COLUMN_APPLICATION_DATE + " DESC";
            Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(userId) });

            while (cursor.moveToNext()) {
                int appId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_APPLICATION_ID));
                int jobId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_APPLICATION_JOB_ID));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPLICATION_STATUS));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPLICATION_DATE));
                String jobTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_TITLE));
                String company = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_COMPANY));

                applications.add(new ApplicationInfo(appId, userId, jobId, jobTitle, company, status, date));
            }

            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Erreur getUserApplications: " + e.getMessage());
        }

        db.close();
        return applications;
    }

    // Méthode pour vérifier si un utilisateur a déjà postulé
    public boolean hasApplied(int userId, int jobId) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean hasApplied = false;

        try {
            String query = "SELECT * FROM " + TABLE_APPLICATIONS +
                    " WHERE " + COLUMN_APPLICATION_USER_ID + " = ? AND " +
                    COLUMN_APPLICATION_JOB_ID + " = ?";
            Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(userId), String.valueOf(jobId) });

            hasApplied = cursor.getCount() > 0;
            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Erreur hasApplied: " + e.getMessage());
        }

        db.close();
        return hasApplied;
    }

    // Classe interne pour les informations de candidature
    public static class ApplicationInfo {
        private int applicationId;
        private int userId;
        private int jobId;
        private String jobTitle;
        private String company;
        private String status;
        private String date;

        public ApplicationInfo(int applicationId, int userId, int jobId, String jobTitle,
                String company, String status, String date) {
            this.applicationId = applicationId;
            this.userId = userId;
            this.jobId = jobId;
            this.jobTitle = jobTitle;
            this.company = company;
            this.status = status;
            this.date = date;
        }

        public int getApplicationId() {
            return applicationId;
        }

        public int getUserId() {
            return userId;
        }

        public int getJobId() {
            return jobId;
        }

        public String getJobTitle() {
            return jobTitle;
        }

        public String getCompany() {
            return company;
        }

        public String getStatus() {
            return status;
        }

        public String getDate() {
            return date;
        }
    }

    // ==================== STATISTICS METHODS ====================

    // Get count of user's applications
    public int getUserApplicationsCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_APPLICATIONS +
                    " WHERE " + COLUMN_APPLICATION_USER_ID + " = ?",
                    new String[] { String.valueOf(userId) });
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Erreur getUserApplicationsCount: " + e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return count;
    }

    // Get count of user's interviews (applications with interview_scheduled = 1)
    public int getUserInterviewsCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_APPLICATIONS +
                    " WHERE " + COLUMN_APPLICATION_USER_ID + " = ? AND " +
                    COLUMN_APPLICATION_INTERVIEW + " = 1",
                    new String[] { String.valueOf(userId) });
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Erreur getUserInterviewsCount: " + e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return count;
    }

    // Get count of user's saved jobs
    public int getUserSavedJobsCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_SAVED_JOBS +
                    " WHERE " + COLUMN_SAVED_USER_ID + " = ?",
                    new String[] { String.valueOf(userId) });
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Erreur getUserSavedJobsCount: " + e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return count;
    }

    // ==================== SAVED JOBS MANAGEMENT ====================

    // Save a job for a user
    public boolean saveJob(int userId, int jobId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_SAVED_USER_ID, userId);
            values.put(COLUMN_SAVED_JOB_ID, jobId);
            long result = db.insert(TABLE_SAVED_JOBS, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Erreur saveJob: " + e.getMessage());
            return false;
        }
    }

    // Unsave a job for a user
    public boolean unsaveJob(int userId, int jobId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int result = db.delete(TABLE_SAVED_JOBS,
                    COLUMN_SAVED_USER_ID + " = ? AND " + COLUMN_SAVED_JOB_ID + " = ?",
                    new String[] { String.valueOf(userId), String.valueOf(jobId) });
            return result > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Erreur unsaveJob: " + e.getMessage());
            return false;
        }
    }

    // Check if a job is saved by a user
    public boolean isJobSaved(int userId, int jobId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_SAVED_JOBS +
                    " WHERE " + COLUMN_SAVED_USER_ID + " = ? AND " + COLUMN_SAVED_JOB_ID + " = ?",
                    new String[] { String.valueOf(userId), String.valueOf(jobId) });
            if (cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Erreur isJobSaved: " + e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return false;
    }
}