import controller.AuthController;
import javafx.application.Application;
import javafx.stage.Stage;
import model.DBConnection;
import model.Users;
import view.AuthView;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Users model = new Users();                 // Model
        AuthView view = new AuthView(primaryStage); // View
        new AuthController(model, view);           // Controller

        view.show();
    }

    @Override
    public void stop() {
        DBConnection.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
