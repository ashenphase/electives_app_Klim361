import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GuestDashboardView {
    private final Stage stage;

    private TableView<Elective> table;
    private Button refreshButton;
    private TextArea activityInfoArea;

    protected TableView<Teacher> teachersTable;

    private TableView<Student> studentsTable;

    public GuestDashboardView() {
        this.stage = new Stage();
        initUI();
    }

    private void initUI() {
        stage.setTitle("Панель Просмотра (Гость)");

        TabPane tabPane = new TabPane();

        // КУРСЫ И ЧАСЫ
        Tab coursesTab = new Tab("Распределение занятий");
        coursesTab.setClosable(false);

        table = new TableView<>();
        setupCoursesTable();

        refreshButton = new Button("Обновить список курсов");
        refreshButton.setMaxWidth(Double.MAX_VALUE);

        activityInfoArea = new TextArea();
        activityInfoArea.setPromptText("Выберите курс из таблицы, чтобы увидеть распределение часов...");
        activityInfoArea.setEditable(false);
        activityInfoArea.setPrefHeight(150);

        VBox coursesLayout = new VBox(10);
        coursesLayout.setPadding(new Insets(15));
        coursesLayout.getChildren().addAll(
                new Label("Список факультативов по семестрам:"),
                table,
                refreshButton,
                new Label("Распределение часов (Виды занятий) по выбранному курсу:"),
                activityInfoArea
        );
        coursesTab.setContent(coursesLayout);


        // ПРЕПОДАВАТЕЛИ
        Tab teachersTab = new Tab("Преподаватели");
        teachersTab.setClosable(false);

        teachersTable = new TableView<>();
        TableColumn<Teacher, Integer> tIdCol = new TableColumn<>("ID");
        tIdCol.setCellValueFactory(cellData -> cellData.getValue().teacherIdProperty().asObject());
        TableColumn<Teacher, String> tNameCol = new TableColumn<>("ФИО Преподавателя");
        tNameCol.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
        tNameCol.setPrefWidth(250);
        TableColumn<Teacher, String> tPosCol = new TableColumn<>("Должность");
        tPosCol.setCellValueFactory(cellData -> cellData.getValue().positionProperty());
        tPosCol.setPrefWidth(180);
        teachersTable.getColumns().addAll(tIdCol, tNameCol, tPosCol);
        teachersTable.setPrefWidth(650);

        VBox teachersLayout = new VBox(10);
        teachersLayout.setPadding(new Insets(15));
        teachersLayout.getChildren().addAll(
                new Label("Зарегистрированные преподаватели:"),
                teachersTable
        );
        teachersTab.setContent(teachersLayout);


        // СТУДЕНТЫ
        Tab studentsTab = new Tab("Записанные студенты");
        studentsTab.setClosable(false);

        studentsTable = new TableView<>();
        TableColumn<Student, Integer> sIdCol = new TableColumn<>("ID");
        sIdCol.setCellValueFactory(cellData -> cellData.getValue().studentIdProperty().asObject());
        TableColumn<Student, String> sNameCol = new TableColumn<>("ФИО Студента");
        sNameCol.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
        sNameCol.setPrefWidth(250);
        TableColumn<Student, String> sPhoneCol = new TableColumn<>("Телефон");
        sPhoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty());
        sPhoneCol.setPrefWidth(180);
        studentsTable.getColumns().addAll(sIdCol, sNameCol, sPhoneCol);
        studentsTable.setPrefWidth(650);

        VBox studentsLayout = new VBox(10);
        studentsLayout.setPadding(new Insets(15));
        studentsLayout.getChildren().addAll(
                new Label("Список студентов в системе:"),
                studentsTable
        );
        studentsTab.setContent(studentsLayout);

        tabPane.getTabs().addAll(coursesTab, teachersTab, studentsTab);

        Scene scene = new Scene(tabPane, 700, 600);
        stage.setScene(scene);
    }

    private void setupCoursesTable() {
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
    }

    public void show() { stage.show(); }
    public TableView<Elective> getTable() { return table; }
    public Button getRefreshButton() { return refreshButton; }
    public TextArea getActivityInfoArea() { return activityInfoArea; }
    public TableView<Teacher> getTeachersTable() { return teachersTable; }
    public TableView<Student> getStudentsTable() { return studentsTable; }
}
