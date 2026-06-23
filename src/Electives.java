import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.Connection;

import java.util.ArrayList;
import java.util.List;

public class Electives {
    private final Connection connection;
    private final ObservableList<Elective> electiveList;

    public Electives() {
        this.connection = DBConnection.getInstance().getConnection();
        this.electiveList = FXCollections.observableArrayList();
    }

    public void loadFromDb() {
        String query = "SELECT sc.semester_course_id, e.title AS elective_title, d.title AS dept_title, " +
                "sc.semester_number, sc.academic_year " +
                "FROM semester_courses sc " +
                "INNER JOIN electives e ON sc.elective_id = e.elective_id " +
                "INNER JOIN departments d ON e.department_id = d.department_id";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            electiveList.clear();
            while (rs.next()) {
                electiveList.add(new Elective(
                        rs.getInt("semester_course_id"),
                        rs.getString("elective_title"),
                        rs.getString("dept_title"),
                        rs.getInt("semester_number"),
                        rs.getString("academic_year")
                ));
            }
            System.out.println("Семестровые курсы загружены: " + electiveList.size());
        } catch (Exception e) {
            System.err.println("Ошибка загрузки курсов: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // получить информацию о часах для конкретного курса
    public String loadActivitiesForCourse(int semesterCourseId) {
        String query = "SELECT act.hours, act_t.title AS type_title, t.last_name, t.first_name, t.patronymic " +
                "FROM activities act " +
                "INNER JOIN activity_types act_t ON act.activity_type_id = act_t.activity_type_id " +
                "LEFT JOIN teachers t ON act.teacher_id = t.teacher_id " +
                "WHERE act.semester_course_id = ?";

        StringBuilder result = new StringBuilder();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, semesterCourseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String lastName = rs.getString("last_name");
                    String firstName = rs.getString("first_name");
                    String patronymic = rs.getString("patronymic");

                    String fullTeacherName = "не назначен";
                    if (lastName != null) {
                        fullTeacherName = lastName + " " + firstName + " " + (patronymic != null ? patronymic : "");
                        fullTeacherName = fullTeacherName.trim();
                    }

                    result.append("- ")
                            .append(rs.getString("type_title"))
                            .append(": ")
                            .append(rs.getInt("hours"))
                            .append(" ч. (Преподаватель: ")
                            .append(fullTeacherName)
                            .append(")\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при загрузке часов.";
        }

        if (result.length() == 0) return "Для данного курса часы занятий еще не распределены.";
        return result.toString();
    }

    // добавить или обновить часы для вида занятия
    public boolean saveActivityHours(int semesterCourseId, String typeTitle, int hours, Integer teacherId) {
        String typeQuery = "SELECT activity_type_id FROM activity_types WHERE title = ?";
        String saveQuery = "INSERT INTO activities (hours, semester_course_id, activity_type_id, teacher_id) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE hours = ?, teacher_id = ?";

        try {
            int typeId = -1;
            try (PreparedStatement pstmt = connection.prepareStatement(typeQuery)) {
                pstmt.setString(1, typeTitle);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) typeId = rs.getInt("activity_type_id");
                }
            }

            if (typeId == -1) return false;

            try (PreparedStatement pstmt = connection.prepareStatement(saveQuery)) {
                pstmt.setInt(1, hours);
                pstmt.setInt(2, semesterCourseId);
                pstmt.setInt(3, typeId);

                if (teacherId != null) pstmt.setInt(4, teacherId);
                else pstmt.setNull(4, java.sql.Types.INTEGER);

                pstmt.setInt(5, hours);

                if (teacherId != null) pstmt.setInt(6, teacherId);
                else pstmt.setNull(6, java.sql.Types.INTEGER);

                pstmt.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // метод для загрузки доступных преподавателей по названию факультатива
    public List<TeacherProperty> getAvailableTeachersForElective(String electiveTitle) {
        List<TeacherProperty> teachers = new ArrayList<>();
        String query = "SELECT t.teacher_id, t.last_name, t.first_name, t.patronymic " +
                "FROM teachers t " +
                "INNER JOIN electives_teachers et ON t.teacher_id = et.teacher_id " +
                "INNER JOIN electives e ON et.elective_id = e.elective_id " +
                "WHERE e.title = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, electiveTitle);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    teachers.add(new TeacherProperty(
                            rs.getInt("teacher_id"),
                            rs.getString("last_name"),
                            rs.getString("first_name"),
                            rs.getString("patronymic")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return teachers;
    }

    // загрузка всех преподавателей для таблицы
    public ObservableList<Teacher> loadTeachersFromDb() {
        ObservableList<Teacher> teachers = FXCollections.observableArrayList();
        String query = "SELECT teacher_id, last_name, first_name, patronymic, position FROM teachers";
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                teachers.add(new Teacher(
                        rs.getInt("teacher_id"),
                        rs.getString("last_name"),
                        rs.getString("first_name"),
                        rs.getString("patronymic"),
                        rs.getString("position")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return teachers;
    }

    // добавление нового преподавателя
    public boolean addTeacher(String lastName, String firstName, String patronymic, String position) {
        String query = "INSERT INTO teachers (last_name, first_name, patronymic, position, user_id) VALUES (?, ?, ?, ?, NULL)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, lastName);
            pstmt.setString(2, firstName);
            pstmt.setString(3, patronymic.isEmpty() ? null : patronymic);
            pstmt.setString(4, position);
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // удаление преподавателя
    public boolean deleteTeacher(int teacherId) {
        String query = "DELETE FROM teachers WHERE teacher_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, teacherId);
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // получение списка факультативов (из таблицы electives)
    public List<String> getAllElectiveTitles() {
        List<String> titles = new ArrayList<>();
        String query = "SELECT title FROM electives";
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                titles.add(rs.getString("title"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return titles;
    }

    // назначение преподавателя на факультатив (electives_teachers)
    public boolean assignTeacherToElective(int teacherId, String electiveTitle) {
        String findIdQuery = "SELECT elective_id FROM electives WHERE title = ?";
        String insertQuery = "INSERT INTO electives_teachers (elective_id, teacher_id) VALUES (?, ?)";
        try {
            int electiveId = -1;
            try (PreparedStatement pstmt = connection.prepareStatement(findIdQuery)) {
                pstmt.setString(1, electiveTitle);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) electiveId = rs.getInt("elective_id");
                }
            }
            if (electiveId == -1) return false;

            try (PreparedStatement pstmtInsert = connection.prepareStatement(insertQuery)) {
                pstmtInsert.setInt(1, electiveId);
                pstmtInsert.setInt(2, teacherId);
                pstmtInsert.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // загрузка всех студентов
    public ObservableList<Student> loadStudentsFromDb() {
        ObservableList<Student> students = FXCollections.observableArrayList();
        String query = "SELECT student_id, last_name, first_name, patronymic, phone_number, address FROM students";
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                students.add(new Student(
                        rs.getInt("student_id"),
                        rs.getString("last_name"),
                        rs.getString("first_name"),
                        rs.getString("patronymic"),
                        rs.getString("phone_number"),
                        rs.getString("address")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

    // добавление нового студента в справочник
    public boolean addStudent(String lastName, String firstName, String patronymic, String phone, String address) {
        String query = "INSERT INTO students (last_name, first_name, patronymic, phone_number, address) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, lastName);
            pstmt.setString(2, firstName);
            pstmt.setString(3, patronymic.isEmpty() ? null : patronymic);
            pstmt.setString(4, phone);
            pstmt.setString(5, address.isEmpty() ? null : address);
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // удаление студента
    public boolean deleteStudent(int studentId) {
        String query = "DELETE FROM students WHERE student_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ObservableList<String> getSemesterCoursesFormList() {
        ObservableList<String> list = FXCollections.observableArrayList();
        // ИСПРАВЛЕНО: Достаем semester_number из базы данных
        String query = "SELECT sc.semester_course_id, e.title, sc.academic_year, sc.semester_number " +
                "FROM semester_courses sc " +
                "JOIN electives e ON sc.elective_id = e.elective_id";
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("semester_course_id");
                String title = rs.getString("title");
                String year = rs.getString("academic_year");
                int sem = rs.getInt("semester_number");

                // ИСПРАВЛЕНО: Красиво склеиваем строку, добавляя семестр
                list.add(id + " - " + title + " (Семестр " + sem + ") (" + year + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean enrollStudentToCourse(int studentId, int semesterCourseId) {
        String query = "INSERT INTO students_semester_courses (student_id, semester_course_id, grade, exam_date) VALUES (?, ?, NULL, NULL)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, semesterCourseId);
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ObservableList<UserProperty> loadUsersFromDb() {
        ObservableList<UserProperty> users = FXCollections.observableArrayList();

        String query = "SELECT u.user_id, u.login, GROUP_CONCAT(r.role_name SEPARATOR ', ') AS roles_list " +
                "FROM users u " +
                "LEFT JOIN users_roles ur ON u.user_id = ur.user_id " +
                "LEFT JOIN roles r ON ur.role_id = r.role_id " +
                "GROUP BY u.user_id, u.login";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String roles = rs.getString("roles_list");
                if (roles == null || roles.isEmpty()) {
                    roles = "guest"; // если ролей нет
                }

                users.add(new UserProperty(
                        rs.getInt("user_id"),
                        rs.getString("login"),
                        roles,
                        0
                ));
            }
        } catch (Exception e) {
            System.err.println("ОШИБКА ПРИ СБОРЕ МНОЖЕСТВЕННЫХ РОЛЕЙ");
            e.printStackTrace();
        }
        return users;
    }

    public List<TeacherProperty> getFreeTeachers() {
        List<TeacherProperty> list = new ArrayList<>();
        String query = "SELECT teacher_id, last_name, first_name, patronymic FROM teachers WHERE user_id IS NULL";
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new TeacherProperty(
                        rs.getInt("teacher_id"),
                        rs.getString("last_name"),
                        rs.getString("first_name"),
                        rs.getString("patronymic")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addRoleToUser(int userId, String roleName, Integer teacherId) {
        String findRoleId = "SELECT role_id FROM roles WHERE role_name = ?";
        String insertNewRole = "INSERT IGNORE INTO users_roles (user_id, role_id) VALUES (?, ?)";
        String updateTeacher = "UPDATE teachers SET user_id = ? WHERE teacher_id = ?";

        try {
            connection.setAutoCommit(false);

            int roleId = -1;
            try (PreparedStatement pstmt = connection.prepareStatement(findRoleId)) {
                pstmt.setString(1, roleName);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) roleId = rs.getInt("role_id");
                }
            }

            if (roleId != -1) {
                try (PreparedStatement pstmt = connection.prepareStatement(insertNewRole)) {
                    pstmt.setInt(1, userId);
                    pstmt.setInt(2, roleId);
                    pstmt.executeUpdate();
                }
            }

            if (teacherId != null && "teacher".equals(roleName)) {
                try (PreparedStatement pstmt = connection.prepareStatement(updateTeacher)) {
                    pstmt.setInt(1, userId);
                    pstmt.setInt(2, teacherId);
                    pstmt.executeUpdate();
                }
            }

            connection.commit();
            connection.setAutoCommit(true);
            return true;
        } catch (Exception e) {
            try { connection.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        String clearTeacherLink = "UPDATE teachers SET user_id = NULL WHERE user_id = ?";
        String deleteQuery = "DELETE FROM users WHERE user_id = ?";
        try {
            try (PreparedStatement pstmt = connection.prepareStatement(clearTeacherLink)) {
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public int getTeacherIdByUserId(int userId) {
        String query = "SELECT teacher_id FROM teachers WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("teacher_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // если не препод
    }

    public ObservableList<Elective> loadTeacherCourses(int teacherId) {
        ObservableList<Elective> list = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT sc.semester_course_id, e.title, d.title AS dept_title, sc.semester_number, sc.academic_year " +
                "FROM semester_courses sc " +
                "JOIN electives e ON sc.elective_id = e.elective_id " +
                "JOIN departments d ON e.department_id = d.department_id " +
                "JOIN activities a ON sc.semester_course_id = a.semester_course_id " +
                "WHERE a.teacher_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, teacherId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Elective(
                            rs.getInt("semester_course_id"),
                            rs.getString("title"),
                            rs.getString("dept_title"),
                            rs.getInt("semester_number"),
                            rs.getString("academic_year")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public String loadTeacherActivities(int courseId, int teacherId) {
        StringBuilder sb = new StringBuilder();
        String query = "SELECT at.title, a.hours FROM activities a " +
                "JOIN activity_types at ON a.activity_type_id = at.activity_type_id " +
                "WHERE a.semester_course_id = ? AND a.teacher_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, courseId);
            pstmt.setInt(2, teacherId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sb.append("• ").append(rs.getString("title"))
                            .append(": ").append(rs.getInt("hours")).append(" ч.\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.length() == 0 ? "На данном курсе у вас нет назначенных часов." : sb.toString();
    }

    public ObservableList<GradeProperty> loadStudentsForGrading(int courseId) {
        ObservableList<GradeProperty> list = FXCollections.observableArrayList();
        String query = "SELECT s.student_id, CONCAT(s.last_name, ' ', s.first_name, ' ', IFNULL(s.patronymic, '')) AS fio, " +
                "ssc.grade, ssc.exam_date " +
                "FROM students_semester_courses ssc " +
                "JOIN students s ON ssc.student_id = s.student_id " +
                "WHERE ssc.semester_course_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Date d = rs.getDate("exam_date");
                    list.add(new GradeProperty(
                            rs.getInt("student_id"),
                            rs.getString("fio"),
                            rs.getObject("grade") != null ? rs.getInt("grade") : null,
                            d != null ? d.toString() : null
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean saveStudentGrade(int studentId, int courseId, int grade, String dateStr) {
        String updateGrade = "UPDATE students_semester_courses SET grade = ?, exam_date = ? WHERE student_id = ? AND semester_course_id = ?";

        String syncFinalGrade =
                "INSERT INTO electives_students (elective_id, student_id, final_grade) " +
                        "SELECT sc.elective_id, ?, ? FROM semester_courses sc WHERE sc.semester_course_id = ? " +
                        "ON DUPLICATE KEY UPDATE final_grade = VALUES(final_grade)";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(updateGrade)) {
                pstmt.setInt(1, grade);
                pstmt.setDate(2, dateStr != null ? Date.valueOf(dateStr) : null);
                pstmt.setInt(3, studentId);
                pstmt.setInt(4, courseId);
                pstmt.executeUpdate();
            }

            String findBestGrade =
                    "SELECT ssc.grade FROM students_semester_courses ssc " +
                            "JOIN semester_courses sc ON ssc.semester_course_id = sc.semester_course_id " +
                            "WHERE ssc.student_id = ? AND sc.elective_id = (SELECT elective_id FROM semester_courses WHERE semester_course_id = ?) " +
                            "ORDER BY sc.semester_number DESC";

            int finalGradeValue = grade; // По умолчанию ставим текущую
            try (PreparedStatement pstmt = connection.prepareStatement(findBestGrade)) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, courseId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && rs.getObject("grade") != null) {
                        finalGradeValue = rs.getInt("grade");
                    }
                }
            }

            try (PreparedStatement pstmt = connection.prepareStatement(syncFinalGrade)) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, finalGradeValue);
                pstmt.setInt(3, courseId);
                pstmt.executeUpdate();
            }

            connection.commit();
            connection.setAutoCommit(true);
            return true;
        } catch (Exception e) {
            try { connection.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
            return false;
        }
    }

    public boolean unenrollStudentFromCourse(int studentId, int semesterCourseId) {
        String query = "DELETE FROM students_semester_courses WHERE student_id = ? AND semester_course_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, semesterCourseId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Если строка удалена — всё прошло успешно
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ObservableList<Elective> getTargetList() {
        return electiveList;
    }
}