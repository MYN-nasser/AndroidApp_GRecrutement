package com.emsi.recrutement;

public class JobOffer {
    private int id;
    private String title;
    private String company;
    private String location;
    private String salary;
    private String description;
    private String requirements;
    private String type;
    private String logo;

    public JobOffer(int id, String title, String company, String location, String salary,
            String description, String requirements, String type, String logo) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.location = location;
        this.salary = salary;
        this.description = description;
        this.requirements = requirements;
        this.type = type;
        this.logo = logo;
    }

    // Constructor without logo for backward compatibility
    public JobOffer(int id, String title, String company, String location, String salary,
            String description, String requirements, String type) {
        this(id, title, company, location, salary, description, requirements, type, null);
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public String getLocation() {
        return location;
    }

    public String getSalary() {
        return salary;
    }

    public String getDescription() {
        return description;
    }

    public String getRequirements() {
        return requirements;
    }

    public String getType() {
        return type;
    }

    public String getLogo() {
        return logo;
    }
}
