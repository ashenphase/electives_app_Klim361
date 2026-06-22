package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        // Добавили teacher_id в INSERT и в ON DUPLICATE KEY UPDATE
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

    // получение списка ВСЕХ чистых факультативов (из таблицы electives) для выпадающего списка
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

    // получение списка запусков курсов для выпадающего списка записи (ID + Название)
    public List<String> getSemesterCoursesFormList() {
        List<String> list = new ArrayList<>();
        String query = "SELECT sc.semester_course_id, e.title, sc.academic_year " +
                "FROM semester_courses sc " +
                "INNER JOIN electives e ON sc.elective_id = e.elective_id";
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getInt("semester_course_id") + " - " + rs.getString("title") + " (" + rs.getString("academic_year") + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // запись студента на семестровый курс (students_semester_courses)
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

    public ObservableList<Elective> getTargetList() {
        return electiveList;
    }
}