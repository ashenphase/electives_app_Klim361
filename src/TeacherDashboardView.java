import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TeacherDashboardView {
    private final Stage stage;

    // нагрузка
    private TableView<Elective> myCoursesTable;
    private Button refreshCoursesBtn;
    private TextArea myActivityInfoArea;

    // журнал
    private ComboBox<Elective> courseSelectorBox;
    private TableView<GradeProperty> gradingTable;
    private ComboBox<Integer> gradeComboBox;
    private DatePicker examDatePicker;
    private Button saveGradeBtn;
    private Label statusLabel;

    public TeacherDashboardView() {
        this.stage = new Stage();
        initUI();
    }

    private void initUI() {
        stage.setTitle("Панель Преподавателя");

        TabPane tabPane = new TabPane();

        // НАГРУЗКА
        Tab loadTab = new Tab("Нагрузка");
        loadTab.setClosable(false);

        myCoursesTable = new TableView<>();
        setupCoursesTable(myCoursesTable);

        refreshCoursesBtn = new Button("Обновить список курсов");
        refreshCoursesBtn.setMaxWidth(Double.MAX_VALUE);

        myActivityInfoArea = new TextArea();
        myActivityInfoArea.setPromptText("Выберите курс, чтобы увидеть часы занятий");
        myActivityInfoArea.setEditable(false);
        myActivityInfoArea.setPrefHeight(150);

        VBox loadLayout = new VBox(10);
        loadLayout.setPadding(new Insets(15));
        loadLayout.getChildren().addAll(
                new Label("Семестровые курсы:"),
                myCoursesTable, refreshCoursesBtn,
                new Label("Виды занятий и часы по курсу:"),
                myActivityInfoArea
        );
        loadTab.setContent(loadLayout);


        // ЖУРНАЛ
        Tab gradingTab = new Tab("Журнал оценок");
        gradingTab.setClosable(false);

        courseSelectorBox = new ComboBox<>();
        courseSelectorBox.setPromptText("Выберите курс для выставления оценок");
        courseSelectorBox.setMaxWidth(Double.MAX_VALUE);

        gradingTable = new TableView<>();
        setupGradingTable();

        // выставление оценок
        VBox gradeForm = new VBox(10);
        gradeForm.setPadding(new Insets(10));
        gradeForm.setPrefWidth(250);

        gradeComboBox = new ComboBox<>();
        gradeComboBox.getItems().addAll(2, 3, 4, 5);
        gradeComboBox.setPromptText("Оценка");
        gradeComboBox.setMaxWidth(Double.MAX_VALUE);

        examDatePicker = new DatePicker();
        examDatePicker.setPromptText("Дата экзамена");
        examDatePicker.setMaxWidth(Double.MAX_VALUE);

        saveGradeBtn = new Button("Выставить оценку");
        saveGradeBtn.setMaxWidth(Double.MAX_VALUE);

        statusLabel = new Label();
        statusLabel.setWrapText(true);

        gradeForm.getChildren().addAll(
                new Label("Управление оценкой"),
                new Label("Оценка:"), gradeComboBox,
                new Label("Дата экзамена:"), examDatePicker,
                saveGradeBtn,
                statusLabel
        );

        HBox gradingLayout = new HBox(15);
        gradingLayout.setPadding(new Insets(15));

        VBox tableContainer = new VBox(10);
        tableContainer.getChildren().addAll(new Label("Выберите курс:"), courseSelectorBox, gradingTable);
        HBox.setHgrow(tableContainer, javafx.scene.layout.Priority.ALWAYS);

        gradingLayout.getChildren().addAll(tableContainer, gradeForm);
        gradingTab.setContent(gradingLayout);

        tabPane.getTabs().addAll(loadTab, gradingTab);

        Scene scene = new Scene(tabPane, 750, 600);
        stage.setScene(scene);
    }

    private void setupCoursesTable(TableView<Elective> table) {
        TableColumn<Elective, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> cellData.getValue().semesterCourseIdProperty().asObject());

        TableColumn<Elective, String> titleCol = new TableColumn<>("Факультатив");
        titleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        titleCol.setPrefWidth(180);

        TableColumn<Elective, String> deptCol = new TableColumn<>("Кафедра");
        deptCol.setCellValueFactory(cellData -> cellData.getValue().departmentTitleProperty());
        deptCol.setPrefWidth(120);

        TableColumn<Elective, Integer> semCol = new TableColumn<>("Семестр");
        semCol.setCellValueFactory(cellData -> cellData.getValue().semesterNumberProperty().asObject());

        TableColumn<Elective, String> yearCol = new TableColumn<>("Учебный год");
        yearCol.setCellValueFactory(cellData -> cellData.getValue().academicYearProperty());
        yearCol.setPrefWidth(100);

        table.getColumns().addAll(idCol, titleCol, deptCol, semCol, yearCol);
    }

    private void setupGradingTable() {
        TableColumn<GradeProperty, Integer> idCol = new TableColumn<>("ID Студента");
        idCol.setCellValueFactory(cellData -> cellData.getValue().studentIdProperty().asObject());

        TableColumn<GradeProperty, String> nameCol = new TableColumn<>("ФИО Студента");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().studentNameProperty());
        nameCol.setPrefWidth(220);

        TableColumn<GradeProperty, Integer> gradeCol = new TableColumn<>("Оценка");
        gradeCol.setCellValueFactory(cellData -> cellData.getValue().gradeProperty().asObject());
        gradeCol.setCellFactory(column -> new TableCell<GradeProperty, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item == 0) {
                    setText("-");
                } else {
                    setText(item.toString());
                }
            }
        });

        TableColumn<GradeProperty, String> dateCol = new TableColumn<>("Дата экзамена");
        dateCol.setCellValueFactory(cellData -> cellData.getValue().examDateProperty());
        dateCol.setPrefWidth(120);

        gradingTable.getColumns().addAll(idCol, nameCol, gradeCol, dateCol);
    }

    public void show() { stage.show(); }
    public TableView<Elective> getMyCoursesTable() { return myCoursesTable; }
    public Button getRefreshCoursesBtn() { return refreshCoursesBtn; }
    public TextArea getMyActivityInfoArea() { return myActivityInfoArea; }
    public ComboBox<Elective> getCourseSelectorBox() { return courseSelectorBox; }
    public TableView<GradeProperty> getGradingTable() { return gradingTable; }
    public ComboBox<Integer> getGradeComboBox() { return gradeComboBox; }
    public DatePicker getExamDatePicker() { return examDatePicker; }
    public Button getSaveGradeBtn() { return saveGradeBtn; }
    public Label getStatusLabel() { return statusLabel; }
}