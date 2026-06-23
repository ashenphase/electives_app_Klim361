import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminDashboardView {
    private final Stage stage;

    private TableView<Elective> table;
    private Button refreshButton;

    private TextArea activityInfoArea;
    private ComboBox<String> activityTypeBox;
    private TextField hoursField;
    private Button addActivityButton;
    private ComboBox<TeacherProperty> teacherBox;


    protected TableView<Teacher> teachersTable;
    private TextField tLastNameField, tFirstNameField, tPatronymicField, tPositionField;
    private Button addTeacherBtn, deleteTeacherBtn, assignBtn;
    private ComboBox<String> allElectivesBox;
    private Label teacherStatusLabel;


    private TableView<Student> studentsTable;
    private TextField sLastNameField, sFirstNameField, sPatronymicField, sPhoneField, sAddressField;
    private Button addStudentBtn, deleteStudentBtn, enrollBtn;
    private ComboBox<String> semCoursesBox;
    private Label studentStatusLabel;
    private Button unenrollBtn;


    private TableView<UserProperty> usersTable;
    private ComboBox<String> rolesComboBox;
    private Button changeRoleBtn;
    private Button deleteUserBtn;
    private Label userStatusLabel;
    private ComboBox<TeacherProperty> freeTeachersComboBox;

    public AdminDashboardView() {
        this.stage = new Stage();
        initUI();
    }

    private void initUI() {
        stage.setTitle("Панель Администратора");

        TabPane tabPane = new TabPane();

        // КУРСЫ И ЧАСЫ
        Tab coursesTab = new Tab("Распределение занятий");
        coursesTab.setClosable(false);

        table = new TableView<>();
        setupCoursesTable();

        refreshButton = new Button("Обновить список курсов");
        refreshButton.setMaxWidth(Double.MAX_VALUE);

        // блок управления часами
        activityInfoArea = new TextArea();
        activityInfoArea.setPromptText("Выберите курс из таблицы, чтобы увидеть распределение часов...");
        activityInfoArea.setEditable(false);
        activityInfoArea.setPrefHeight(100);

        activityTypeBox = new ComboBox<>();
        activityTypeBox.getItems().addAll("Лекция", "Практическое занятие", "Лабораторная работа");
        activityTypeBox.setPromptText("Вид занятия");

        hoursField = new TextField();
        hoursField.setPromptText("Кол-во часов");
        hoursField.setPrefWidth(100);

        addActivityButton = new Button("Задать часы и преподавателя");

        teacherBox = new ComboBox<>();
        teacherBox.setPromptText("Преподаватель");
        teacherBox.setPrefWidth(180);

        HBox activityForm = new HBox(10);
        activityForm.getChildren().addAll(activityTypeBox, hoursField, teacherBox, addActivityButton);

        VBox coursesLayout = new VBox(10);
        coursesLayout.setPadding(new Insets(15));
        coursesLayout.getChildren().addAll(
                new Label("Список факультативов по семестрам:"),
                table, refreshButton,
                new Label("Распределение часов (Виды занятий) по выбранному курсу:"),
                activityInfoArea,
                activityForm
        );
        coursesTab.setContent(coursesLayout);


        // ПРЕПОДАВАТЕЛИ
        Tab teachersTab = new Tab("Преподаватели");
        teachersTab.setClosable(false);

        // таблица
        teachersTable = new TableView<>();
        TableColumn<Teacher, Integer> tIdCol = new TableColumn<>("ID");
        tIdCol.setCellValueFactory(cellData -> cellData.getValue().teacherIdProperty().asObject());
        TableColumn<Teacher, String> tNameCol = new TableColumn<>("ФИО Преподавателя");
        tNameCol.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
        tNameCol.setPrefWidth(200);
        TableColumn<Teacher, String> tPosCol = new TableColumn<>("Должность");
        tPosCol.setCellValueFactory(cellData -> cellData.getValue().positionProperty());
        tPosCol.setPrefWidth(120);
        teachersTable.getColumns().addAll(tIdCol, tNameCol, tPosCol);

        // форма управления
        VBox teacherForm = new VBox(10);
        teacherForm.setPadding(new Insets(10));
        teacherForm.setPrefWidth(300);

        tLastNameField = new TextField(); tLastNameField.setPromptText("Фамилия");
        tFirstNameField = new TextField(); tFirstNameField.setPromptText("Имя");
        tPatronymicField = new TextField(); tPatronymicField.setPromptText("Отчество (если есть)");
        tPositionField = new TextField(); tPositionField.setPromptText("Должность");

        addTeacherBtn = new Button("Добавить преподавателя");
        addTeacherBtn.setMaxWidth(Double.MAX_VALUE);
        deleteTeacherBtn = new Button("Удалить выбранного");
        deleteTeacherBtn.setMaxWidth(Double.MAX_VALUE);

        allElectivesBox = new ComboBox<>();
        allElectivesBox.setPromptText("Выберите факультатив");
        allElectivesBox.setMaxWidth(Double.MAX_VALUE);

        assignBtn = new Button("Назначить на факультатив");
        assignBtn.setMaxWidth(Double.MAX_VALUE);

        teacherStatusLabel = new Label();
        teacherStatusLabel.setWrapText(true);

        teacherForm.getChildren().addAll(
                new Label("Новый преподаватель"),
                tLastNameField, tFirstNameField, tPatronymicField, tPositionField, addTeacherBtn,
                new Label("Управление связями"),
                deleteTeacherBtn, new Separator(),
                new Label("Назначить выбранного на курс:"), allElectivesBox, assignBtn,
                teacherStatusLabel
        );

        HBox teachersLayout = new HBox(15);
        teachersLayout.setPadding(new Insets(15));
        teachersLayout.getChildren().addAll(teachersTable, teacherForm);
        teachersTab.setContent(teachersLayout);



        Tab studentsTab = new Tab("Студенты");
        studentsTab.setClosable(false);

        // таблица студентов
        studentsTable = new TableView<>();
        TableColumn<Student, Integer> sIdCol = new TableColumn<>("ID");
        sIdCol.setCellValueFactory(cellData -> cellData.getValue().studentIdProperty().asObject());
        TableColumn<Student, String> sNameCol = new TableColumn<>("ФИО Студента");
        sNameCol.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
        sNameCol.setPrefWidth(200);
        TableColumn<Student, String> sPhoneCol = new TableColumn<>("Телефон");
        sPhoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty());
        sPhoneCol.setPrefWidth(120);
        studentsTable.getColumns().addAll(sIdCol, sNameCol, sPhoneCol);

        // форма управления
        VBox studentForm = new VBox(10);
        studentForm.setPadding(new Insets(10));
        studentForm.setPrefWidth(300);

        sLastNameField = new TextField(); sLastNameField.setPromptText("Фамилия");
        sFirstNameField = new TextField(); sFirstNameField.setPromptText("Имя");
        sPatronymicField = new TextField(); sPatronymicField.setPromptText("Отчество");
        sPhoneField = new TextField(); sPhoneField.setPromptText("Телефон");
        sAddressField = new TextField(); sAddressField.setPromptText("Адрес проживания");

        addStudentBtn = new Button("Добавить студента");
        addStudentBtn.setMaxWidth(Double.MAX_VALUE);
        deleteStudentBtn = new Button("Удалить выбранного");
        deleteStudentBtn.setMaxWidth(Double.MAX_VALUE);

        semCoursesBox = new ComboBox<>();
        semCoursesBox.setPromptText("Выберите семестровый курс");
        semCoursesBox.setMaxWidth(Double.MAX_VALUE);

        enrollBtn = new Button("Записать на курс");
        enrollBtn.setMaxWidth(Double.MAX_VALUE);

        unenrollBtn = new Button("Отписать от курса");
        unenrollBtn.setMaxWidth(Double.MAX_VALUE);
        unenrollBtn.setStyle("-fx-text-fill: darkred;");

        studentStatusLabel = new Label();
        studentStatusLabel.setWrapText(true);

        studentForm.getChildren().addAll(
                new Label("Новый студент"),
                sLastNameField, sFirstNameField, sPatronymicField, sPhoneField, sAddressField, addStudentBtn,
                new Label("Управление записью"),
                deleteStudentBtn, new Separator(),
                new Label("Записать на факультатив:"), semCoursesBox, enrollBtn,
                unenrollBtn,
                studentStatusLabel
        );

        HBox studentsLayout = new HBox(15);
        studentsLayout.setPadding(new Insets(15));
        studentsLayout.getChildren().addAll(studentsTable, studentForm);
        studentsTab.setContent(studentsLayout);

        Tab rolesTab = new Tab("Управление ролями");
        rolesTab.setClosable(false);

        usersTable = new TableView<>();
        TableColumn<UserProperty, Integer> uIdCol = new TableColumn<>("ID");
        uIdCol.setCellValueFactory(cellData -> cellData.getValue().userIdProperty().asObject());
        TableColumn<UserProperty, String> uLoginCol = new TableColumn<>("Логин / Аккаунт");
        uLoginCol.setCellValueFactory(cellData -> cellData.getValue().loginProperty());
        uLoginCol.setPrefWidth(180);
        TableColumn<UserProperty, String> uRoleCol = new TableColumn<>("Текущая роль");
        uRoleCol.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        uRoleCol.setPrefWidth(120);
        usersTable.getColumns().addAll(uIdCol, uLoginCol, uRoleCol);

        VBox rolesForm = new VBox(10);
        rolesForm.setPadding(new Insets(10));
        rolesForm.setPrefWidth(280);

        rolesComboBox = new ComboBox<>();
        rolesComboBox.getItems().addAll("admin", "teacher", "guest");
        rolesComboBox.setPromptText("Выберите роль");
        rolesComboBox.setMaxWidth(Double.MAX_VALUE);

        freeTeachersComboBox = new ComboBox<>();
        freeTeachersComboBox.setPromptText("Привязать к преподавателю");
        freeTeachersComboBox.setMaxWidth(Double.MAX_VALUE);
        freeTeachersComboBox.setVisible(false);

        changeRoleBtn = new Button("Сохранить изменения");
        changeRoleBtn.setMaxWidth(Double.MAX_VALUE);

        deleteUserBtn = new Button("Удалить аккаунт");
        deleteUserBtn.setMaxWidth(Double.MAX_VALUE);
        deleteUserBtn.setStyle("-fx-text-fill: red;");

        userStatusLabel = new Label();
        userStatusLabel.setWrapText(true);

        rolesForm.getChildren().addAll(
                new Label("Изменение прав"),
                rolesComboBox,
                freeTeachersComboBox,
                changeRoleBtn,
                new Separator(),

                deleteUserBtn,
                userStatusLabel
        );

        HBox rolesLayout = new HBox(15);
        rolesLayout.setPadding(new Insets(15));
        rolesLayout.getChildren().addAll(usersTable, rolesForm);
        rolesTab.setContent(rolesLayout);

        tabPane.getTabs().addAll(coursesTab, teachersTab, studentsTab, rolesTab);

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
    public ComboBox<String> getActivityTypeBox() { return activityTypeBox; }
    public TextField getHoursField() { return hoursField; }
    public Button getAddActivityButton() { return addActivityButton; }
    public ComboBox<TeacherProperty> getTeacherBox() { return teacherBox; }

    public TableView<Teacher> getTeachersTable() { return teachersTable; }
    public TextField getTLastNameField() { return tLastNameField; }
    public TextField getTFirstNameField() { return tFirstNameField; }
    public TextField getTPatronymicField() { return tPatronymicField; }
    public TextField getTPositionField() { return tPositionField; }
    public Button getAddTeacherBtn() { return addTeacherBtn; }
    public Button getDeleteTeacherBtn() { return deleteTeacherBtn; }
    public ComboBox<String> getAllElectivesBox() { return allElectivesBox; }
    public Button getAssignBtn() { return assignBtn; }
    public Label getTeacherStatusLabel() { return teacherStatusLabel; }

    public TableView<Student> getStudentsTable() { return studentsTable; }
    public TextField getSLastNameField() { return sLastNameField; }
    public TextField getSFirstNameField() { return sFirstNameField; }
    public TextField getSPatronymicField() { return sPatronymicField; }
    public TextField getSPhoneField() { return sPhoneField; }
    public TextField getSAddressField() { return sAddressField; }
    public Button getAddStudentBtn() { return addStudentBtn; }
    public Button getDeleteStudentBtn() { return deleteStudentBtn; }
    public ComboBox<String> getSemCoursesBox() { return semCoursesBox; }
    public Button getEnrollBtn() { return enrollBtn; }
    public Label getStudentStatusLabel() { return studentStatusLabel; }
    public Button getUnenrollBtn() { return unenrollBtn; }

    public TableView<UserProperty> getUsersTable() { return usersTable; }
    public ComboBox<String> getRolesComboBox() { return rolesComboBox; }
    public ComboBox<TeacherProperty> getFreeTeachersComboBox() { return freeTeachersComboBox; }
    public Button getChangeRoleBtn() { return changeRoleBtn; }
    public Button getDeleteUserBtn() { return deleteUserBtn; }
    public Label getUserStatusLabel() { return userStatusLabel; }
}