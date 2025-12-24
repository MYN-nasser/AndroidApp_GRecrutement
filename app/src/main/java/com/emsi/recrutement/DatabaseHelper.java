package com.emsi.recrutement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "recrutement.db";
    private static final int DATABASE_VERSION = 1;

    // Table utilisateurs
    public static final String TABLE_USERS = "utilisateurs";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_NOM = "nom";
    public static final String COLUMN_PRENOM = "prenom";
    public static final String COLUMN_TELEPHONE = "telephone";
    public static final String COLUMN_TYPE = "type";

    // Requête de création de table
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_EMAIL + " TEXT UNIQUE NOT NULL," +
                    COLUMN_PASSWORD + " TEXT NOT NULL," +
                    COLUMN_NOM + " TEXT NOT NULL," +
                    COLUMN_PRENOM + " TEXT NOT NULL," +
                    COLUMN_TELEPHONE + " TEXT," +
                    COLUMN_TYPE + " TEXT NOT NULL" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_USERS);
            Log.d("DatabaseHelper", "Table créée avec succès");

            // Ajouter des utilisateurs de test
            addTestUsers(db);

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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Méthode SIMPLIFIÉE pour vérifier login
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Pour debug
        Log.d("DatabaseHelper", "Vérification: " + email + " / " + password);

        try {
            String query = "SELECT * FROM " + TABLE_USERS +
                    " WHERE email = ? AND password = ?";
            Cursor cursor = db.rawQuery(query, new String[]{email, password});

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
            Cursor cursor = db.rawQuery(query, new String[]{email});

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
            Cursor cursor = db.rawQuery(query, new String[]{email});

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
}