package models;

public class Course {
    private int id;
    private String title;
    private String description;
    private int createdBy;
    private boolean isActive;


    public Course(int id, String title, String description, int createdBy, boolean isActive) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.isActive = isActive;
    }

    // Конструктор без id (для создания нового курса)
    public Course(String title, String description, int createdBy, boolean isActive) {
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
