package model;

public class User {
    private int userId;
    private String login;
    private String email;
    private String passwordHash;

    public User(int userId, String login, String email, String passwordHash) {
        this.userId = userId;
        this.login = login;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // геттеры и сеттеры
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}