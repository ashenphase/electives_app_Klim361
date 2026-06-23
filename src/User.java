import java.util.ArrayList;
import java.util.List;

public class User {
    private int userId;
    private String login;
    private String email;
    private String passwordHash;
    private List<String> roles = new ArrayList<>();

    public User(int userId, String login, String email, String passwordHash) {
        this.userId = userId;
        this.login = login;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void addRole(String role) {
        if (role != null && !this.roles.contains(role)) {
            this.roles.add(role);
        }
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}