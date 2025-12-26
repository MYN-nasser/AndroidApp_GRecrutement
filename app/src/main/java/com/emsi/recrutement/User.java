package com.emsi.recrutement;

public class User {
    private int id;
    private String email;
    private String nom;
    private String prenom;
    private String telephone;
    private String type;
    private String imageUri;

    // Constructeur normal
    public User(int id, String email, String nom, String prenom, String telephone, String type) {
        this(id, email, nom, prenom, telephone, type, null);
    }

    // Constructeur complet
    public User(int id, String email, String nom, String prenom, String telephone, String type, String imageUri) {
        this.id = id;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.type = type;
        this.imageUri = imageUri;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getType() {
        return type;
    }

    public String getImageUri() {
        return imageUri;
    }

    // Pour obtenir le nom complet
    public String getFullName() {
        return prenom + " " + nom;
    }
}