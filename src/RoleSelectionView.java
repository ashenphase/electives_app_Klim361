import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RoleSelectionView {
    private final Stage stage;
    private final VBox layout;

    public RoleSelectionView() {
        stage = new Stage();
        stage.setTitle("Выбор роли для входа");

        layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setPrefWidth(300);

        Label titleLabel = new Label("Выберите, в качестве кого войти:");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        layout.getChildren().add(titleLabel);

        Scene scene = new Scene(layout);
        stage.setScene(scene);
    }

    public Button createRoleButton(String roleName) {
        String russianTitle = roleName;
        if ("admin".equals(roleName)) russianTitle = "Администратор";
        if ("teacher".equals(roleName)) russianTitle = "Преподаватель";
        if ("guest".equals(roleName)) russianTitle = "Гость";

        Button btn = new Button(russianTitle);
        btn.setPrefWidth(200);
        btn.setPrefHeight(40);
        layout.getChildren().add(btn);
        return btn;
    }

    public void show() { stage.show(); }
    public void close() { stage.close(); }
}