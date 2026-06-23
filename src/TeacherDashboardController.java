import java.time.LocalDate;

public class TeacherDashboardController {
    private final Electives model;
    private final TeacherDashboardView view;
    private final int teacherId;

    public TeacherDashboardController(Electives model, TeacherDashboardView view, int userId) {
        this.model = model;
        this.view = view;

        this.teacherId = this.model.getTeacherIdByUserId(userId);

        // обновление списков
        Runnable refreshData = () -> {
            var courses = this.model.loadTeacherCourses(teacherId);
            this.view.getMyCoursesTable().setItems(courses);
            this.view.getCourseSelectorBox().getItems().clear();
            this.view.getCourseSelectorBox().getItems().addAll(courses);
        };

        // клик по таблице "Нагрузка"
        this.view.getMyCoursesTable().getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                String info = model.loadTeacherActivities(newSel.getSemesterCourseId(), teacherId);
                view.getMyActivityInfoArea().setText(info);
            }
        });

        // выбор курса в журнале оценок
        this.view.getCourseSelectorBox().getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                view.getGradingTable().setItems(model.loadStudentsForGrading(newSel.getSemesterCourseId()));
            }
        });

        // клик на строку студента
        this.view.getGradingTable().getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                if (newSel.getGrade() >= 2) view.getGradeComboBox().setValue(newSel.getGrade());
                else view.getGradeComboBox().setValue(null);

                if (!"-".equals(newSel.getExamDate())) {
                    view.getExamDatePicker().setValue(LocalDate.parse(newSel.getExamDate()));
                } else {
                    view.getExamDatePicker().setValue(null);
                }
            }
        });

        // кнопка сохранения оценки
        this.view.getSaveGradeBtn().setOnAction(e -> {
            Elective selectedCourse = view.getCourseSelectorBox().getValue();
            GradeProperty selectedStudent = view.getGradingTable().getSelectionModel().getSelectedItem();
            Integer grade = view.getGradeComboBox().getValue();
            LocalDate date = view.getExamDatePicker().getValue();

            if (selectedCourse == null || selectedStudent == null || grade == null) {
                view.getStatusLabel().setText("Ошибка: Выберите курс, студента и оценку!");
                return;
            }

            String dateStr = (date != null) ? date.toString() : null;

            if (model.saveStudentGrade(selectedStudent.getStudentId(), selectedCourse.getSemesterCourseId(), grade, dateStr)) {
                view.getStatusLabel().setText("Оценка успешно выставлена!");
                view.getGradingTable().setItems(model.loadStudentsForGrading(selectedCourse.getSemesterCourseId()));
            } else {
                view.getStatusLabel().setText("Ошибка сохранения оценки в БД.");
            }
        });

        this.view.getRefreshCoursesBtn().setOnAction(e -> refreshData.run());

        refreshData.run();
    }
}