import javafx.beans.property.*;

public class UserProperty {
    private final IntegerProperty userId;
    private final StringProperty login;
    private final StringProperty role;
    private final IntegerProperty personId; // Будет хранить teacher_id

    public UserProperty(int userId, String login, String role, int personId) {
        this.userId = new SimpleIntegerProperty(userId);
        this.login = new SimpleStringProperty(login);
        this.role = new SimpleStringProperty(role);
        this.personId = new SimpleIntegerProperty(personId);
    }

    public IntegerProperty userIdProperty() { return userId; }
    public StringProperty loginProperty() { return login; }
    public StringProperty roleProperty() { return role; }
    public IntegerProperty personIdProperty() { return personId; }

    public int getUserId() { return userId.get(); }
    public String getLogin() { return login.get(); }
    public String getRole() { return role.get(); }
    public int getPersonId() { return personId.get(); }
}