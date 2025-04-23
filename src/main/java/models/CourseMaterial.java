package models;

public class CourseMaterial {
    private int id;
    private int courseId;
    private String title;
    private final String content;
    private final String materialType;

    public CourseMaterial(int id, int courseId, String title, String content, String materialType) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.content = content;
        this.materialType = materialType;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public String getMaterialType() { return materialType; }
}
