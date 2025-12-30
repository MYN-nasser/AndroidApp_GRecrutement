package com.emsi.recrutement;

public class CV {
    private int id;
    private int userId;
    private String titre;
    private String domaine;
    private String fileUri;
    private String fileName;
    private String uploadDate;
    private String preview; // Preview of CV content

    public CV(int id, int userId, String titre, String domaine, String fileUri, String fileName, String uploadDate) {
        this.id = id;
        this.userId = userId;
        this.titre = titre;
        this.domaine = domaine;
        this.fileUri = fileUri;
        this.fileName = fileName;
        this.uploadDate = uploadDate;
        this.preview = ""; // Default empty preview
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getTitre() {
        return titre;
    }

    public String getDomaine() {
        return domaine;
    }

    public String getFileUri() {
        return fileUri;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public String getPreview() {
        return preview;
    }

    // Setters (if needed)
    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setDomaine(String domaine) {
        this.domaine = domaine;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }
}
