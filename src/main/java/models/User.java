package models;

import java.time.LocalDateTime;

public abstract class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String  role;
    private final LocalDateTime createdAt;
    private boolean isActive;

    public User(int id, String username, String password, String email, String fullName, String role, boolean isActive) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public String getFullName() {return fullName;}
    public void setFullName(String fullName) {this.fullName = fullName;}
    public String getRole() {return role;}
    public boolean isActive() {return isActive;}
    public void setActive(boolean active) {isActive = active;}

    public abstract void showMenu();

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    public void setUsername(String username) {
    }

    public void setPassword(String password) {
    }
}
