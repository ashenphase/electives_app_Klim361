import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AuthView {
    private final Stage stage;
    private TextField loginField;
    private TextField emailField;
    private PasswordField passwordField;
    private Button loginButton;
    private Button registerButton;
    private Label statusLabel;


    public AuthView(Stage stage) {
        this.stage = stage;
        initUI();
    }

    private void initUI() {
        stage.setTitle("Авторизация и Регистрация");

        loginField = new TextField();
        loginField.setPromptText("Логин");

        emailField = new TextField();
        emailField.setPromptText("Email (только для регистрации)");

        passwordField = new PasswordField();
        passwordField.setPromptText("Пароль");

        loginButton = new Button("Войти");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        registerButton = new Button("Зарегистрироваться");
        registerButton.setMaxWidth(Double.MAX_VALUE);

        statusLabel = new Label();
        statusLabel.setWrapText(true);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(
                new Label("Вход / Регистрация"),
                loginField, emailField, passwordField,
                loginButton, registerButton, statusLabel
        );

        Scene scene = new Scene(layout, 320, 320);
        stage.setScene(scene);
    }

    public void show() { stage.show(); }
    public String getLogin() { return loginField.getText(); }
    public String getEmail() { return emailField.getText(); }
    public String getPassword() { return passwordField.getText(); }
    public Button getLoginButton() { return loginButton; }
    public Button getRegisterButton() { return registerButton; }
    public void setStatus(String text) { statusLabel.setText(text); }
}