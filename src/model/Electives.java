package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

    public ObservableList<Elective> getTargetList() {
        return electiveList;
    }
}