package models;

public class Course {
    private int id;
    private String title;
    private String description;
    private final int createdBy;
    private final boolean isActive;

    public Course(int id, String title, String description, int createdBy, boolean isActive) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (createdBy <= 0) {
            throw new IllegalArgumentException("Invalid creator ID");
        }
        this.id = id;
        this.title = title.trim();
        this.description = description;
        this.createdBy = createdBy;
        this.isActive = isActive;
    }

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

    public boolean isActive() {
        return isActive;
    }
}
