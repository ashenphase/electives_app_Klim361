package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Elective;

public class AdminDashboardView {
    private final Stage stage;
    private TableView<Elective> table;
    private Button refreshButton;

    public AdminDashboardView() {
        this.stage = new Stage();
        initUI();
    }

    private void initUI() {
        stage.setTitle("Панель Администратора — Учебные курсы");

        table = new TableView<>();

        TableColumn<Elective, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> cellData.getValue().semesterCourseIdProperty().asObject());

        TableColumn<Elective, String> titleCol = new TableColumn<>("Факультатив");
        titleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        titleCol.setPrefWidth(180);

        TableColumn<Elective, String> deptCol = new TableColumn<>("Кафедра");
        deptCol.setCellValueFactory(cellData -> cellData.getValue().departmentTitleProperty());
        deptCol.setPrefWidth(150);

        TableColumn<Elective, Integer> semCol = new TableColumn<>("Семестр");
        semCol.setCellValueFactory(cellData -> cellData.getValue().semesterNumberProperty().asObject());

        TableColumn<Elective, String> yearCol = new TableColumn<>("Учебный год");
        yearCol.setCellValueFactory(cellData -> cellData.getValue().academicYearProperty());
        yearCol.setPrefWidth(100);

        table.getColumns().addAll(idCol, titleCol, deptCol, semCol, yearCol);

        refreshButton = new Button("Обновить данные");
        refreshButton.setMaxWidth(Double.MAX_VALUE);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.getChildren().addAll(new Label("Список факультативов по семестрам:"), table, refreshButton);

        Scene scene = new Scene(layout, 600, 400);
        stage.setScene(scene);
    }

    public void show() { stage.show(); }
    public TableView<Elective> getTable() { return table; }
    public Button getRefreshButton() { return refreshButton; }
}