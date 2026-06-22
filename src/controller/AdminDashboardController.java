package controller;

import model.Elective;
import model.Electives;
import view.AdminDashboardView;

public class AdminDashboardController {
    private final Electives model;
    private final AdminDashboardView view;

    public AdminDashboardController(Electives model, AdminDashboardView view) {
        this.model = model;
        this.view = view;

        this.view.getTable().setItems(this.model.getTargetList());
        this.view.getRefreshButton().setOnAction(e -> this.model.loadFromDb());

        this.view.getTable().getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                int courseId = newSelection.getSemesterCourseId();

                String activityData = model.loadActivitiesForCourse(courseId);
                view.getActivityInfoArea().setText(activityData);

                view.getTeacherBox().getItems().clear();
                java.util.List<model.TeacherProperty> availableTeachers = model.getAvailableTeachersForElective(newSelection.getTitle());
                view.getTeacherBox().getItems().addAll(availableTeachers);
            }
        });

        // кнопка "Добавить часы"
        this.view.getAddActivityButton().setOnAction(e -> {
            Elective selectedCourse = this.view.getTable().getSelectionModel().getSelectedItem();
            String selectedType = this.view.getActivityTypeBox().getValue();
            String hoursText = this.view.getHoursField().getText();
            model.TeacherProperty selectedTeacher = this.view.getTeacherBox().getValue();

            if (selectedCourse == null) {
                this.view.getActivityInfoArea().setText("Ошибка: Сначала выберите курс в таблице!");
                return;
            }
            if (selectedType == null || hoursText.isEmpty()) {
                this.view.getActivityInfoArea().setText("Ошибка: Выберите вид занятия и введите часы!");
                return;
            }

            try {
                int hours = Integer.parseInt(hoursText);
                if (hours <= 0) {
                    this.view.getActivityInfoArea().setText("Ошибка: Количество часов должно быть больше 0!");
                    return;
                }

                Integer teacherId = (selectedTeacher != null) ? selectedTeacher.getTeacherId() : null;

                boolean success = model.saveActivityHours(selectedCourse.getSemesterCourseId(), selectedType, hours, teacherId);

                if (success) {
                    String updatedData = model.loadActivitiesForCourse(selectedCourse.getSemesterCourseId());
                    this.view.getActivityInfoArea().setText(updatedData);
                    this.view.getHoursField().clear();
                    this.view.getTeacherBox().setValue(null);
                } else {
                    this.view.getActivityInfoArea().setText("Не удалось сохранить изменения. Проверьте связи преподавателя.");
                }

            } catch (NumberFormatException ex) {
                this.view.getActivityInfoArea().setText("Ошибка: Введите корректное число часов!");
            }
        });

        this.model.loadFromDb();

        // ЛОГИКА ВКЛАДКИ ПРЕПОДАВАТЕЛИ

        Runnable refreshTeachers = () -> {
            view.getTeachersTable().setItems(model.loadTeachersFromDb());
        };

        // кервичная загрузка преподов и факультативов в ComboBox
        refreshTeachers.run();
        view.getAllElectivesBox().getItems().addAll(model.getAllElectiveTitles());

        // кнопка добавить преподавателя
        view.getAddTeacherBtn().setOnAction(e -> {
            String ln = view.getTLastNameField().getText();
            String fn = view.getTFirstNameField().getText();
            String pat = view.getTPatronymicField().getText();
            String pos = view.getTPositionField().getText();

            if (ln.isEmpty() || fn.isEmpty() || pos.isEmpty()) {
                view.getTeacherStatusLabel().setText("Заполните Фамилию, Имя и Должность!");
                return;
            }

            if (model.addTeacher(ln, fn, pat, pos)) {
                view.getTeacherStatusLabel().setText("Преподаватель успешно добавлен!");
                refreshTeachers.run();
                view.getTLastNameField().clear(); view.getTFirstNameField().clear();
                view.getTPatronymicField().clear(); view.getTPositionField().clear();
            } else {
                view.getTeacherStatusLabel().setText("Ошибка добавления в БД.");
            }
        });

        // кнопка удалить преподавателя
        view.getDeleteTeacherBtn().setOnAction(e -> {
            model.Teacher selected = view.getTeachersTable().getSelectionModel().getSelectedItem();
            if (selected == null) {
                view.getTeacherStatusLabel().setText("Сначала выберите преподавателя в таблице!");
                return;
            }

            if (model.deleteTeacher(selected.getTeacherId())) {
                view.getTeacherStatusLabel().setText("Преподаватель удален!");
                refreshTeachers.run();
            } else {
                view.getTeacherStatusLabel().setText("Не удалось удалить (возможно, он ведет занятия).");
            }
        });

        // кнопка назначить на факультатив
        view.getAssignBtn().setOnAction(e -> {
            model.Teacher selectedTeacher = view.getTeachersTable().getSelectionModel().getSelectedItem();
            String selectedElective = view.getAllElectivesBox().getValue();

            if (selectedTeacher == null || selectedElective == null) {
                view.getTeacherStatusLabel().setText("Выберите и преподавателя, и факультатив!");
                return;
            }

            if (model.assignTeacherToElective(selectedTeacher.getTeacherId(), selectedElective)) {
                view.getTeacherStatusLabel().setText("Преподаватель успешно закреплен за курсом!");

                Elective currentCourse = view.getTable().getSelectionModel().getSelectedItem();
                if (currentCourse != null && currentCourse.getTitle().equals(selectedElective)) {
                    String updatedData = model.loadActivitiesForCourse(currentCourse.getSemesterCourseId());
                    view.getActivityInfoArea().setText(updatedData);

                    view.getTeacherBox().getItems().clear();
                    view.getTeacherBox().getItems().addAll(model.getAvailableTeachersForElective(currentCourse.getTitle()));
                }

            } else {
                view.getTeacherStatusLabel().setText("Ошибка связи (возможно, уже назначен).");
            }
        });

        // ЛОГИКА ВКЛАДКИ СТУДЕНТЫ

        Runnable refreshStudents = () -> {
            view.getStudentsTable().setItems(model.loadStudentsFromDb());
        };

        // загрузка данных на вкладку
        refreshStudents.run();
        view.getSemCoursesBox().getItems().addAll(model.getSemesterCoursesFormList());

        // кнопка добавить студента
        view.getAddStudentBtn().setOnAction(e -> {
            String ln = view.getSLastNameField().getText();
            String fn = view.getSFirstNameField().getText();
            String pat = view.getSPatronymicField().getText();
            String phone = view.getSPhoneField().getText();
            String addr = view.getSAddressField().getText();

            if (ln.isEmpty() || fn.isEmpty() || phone.isEmpty()) {
                view.getStudentStatusLabel().setText("Заполните Фамилию, Имя и Телефон!");
                return;
            }

            if (model.addStudent(ln, fn, pat, phone, addr)) {
                view.getStudentStatusLabel().setText("Студент успешно внесен в базу!");
                refreshStudents.run();
                view.getSLastNameField().clear(); view.getSFirstNameField().clear();
                view.getSPatronymicField().clear(); view.getSPhoneField().clear();
                view.getSAddressField().clear();
            } else {
                view.getStudentStatusLabel().setText("Ошибка. Возможно, такой номер телефона уже есть.");
            }
        });

        // кнопка удалить студента
        view.getDeleteStudentBtn().setOnAction(e -> {
            model.Student selected = view.getStudentsTable().getSelectionModel().getSelectedItem();
            if (selected == null) {
                view.getStudentStatusLabel().setText("Выберите студента в таблице!");
                return;
            }

            if (model.deleteStudent(selected.getStudentId())) {
                view.getStudentStatusLabel().setText("Студент удален из справочника.");
                refreshStudents.run();
            } else {
                view.getStudentStatusLabel().setText("Не удалось удалить (студент уже записан на курсы).");
            }
        });

        // кнопка записать на курс
        view.getEnrollBtn().setOnAction(e -> {
            model.Student selectedStudent = view.getStudentsTable().getSelectionModel().getSelectedItem();
            String selectedCourseRow = view.getSemCoursesBox().getValue();

            if (selectedStudent == null || selectedCourseRow == null) {
                view.getStudentStatusLabel().setText("Выберите студента и семестровый курс!");
                return;
            }

            try {
                int semesterCourseId = Integer.parseInt(selectedCourseRow.split(" - ")[0]);

                if (model.enrollStudentToCourse(selectedStudent.getStudentId(), semesterCourseId)) {
                    view.getStudentStatusLabel().setText("Студент успешно записан на этот семестровый курс!");
                } else {
                    view.getStudentStatusLabel().setText("Ошибка записи (возможно, он уже записан).");
                }
            } catch (Exception ex) {
                view.getStudentStatusLabel().setText("Ошибка обработки данных.");
                ex.printStackTrace();
            }
        });
    }


}