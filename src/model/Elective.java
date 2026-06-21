package model;

import javafx.beans.property.*;

public class Elective {
    private final IntegerProperty semesterCourseId;
    private final StringProperty title;
    private final StringProperty departmentTitle;
    private final IntegerProperty semesterNumber;
    private final StringProperty academicYear;

    public Elective(int semesterCourseId, String title, String departmentTitle, int semesterNumber, String academicYear) {
        this.semesterCourseId = new SimpleIntegerProperty(semesterCourseId);
        this.title = new SimpleStringProperty(title);
        this.departmentTitle = new SimpleStringProperty(departmentTitle);
        this.semesterNumber = new SimpleIntegerProperty(semesterNumber);
        this.academicYear = new SimpleStringProperty(academicYear);
    }

    public IntegerProperty semesterCourseIdProperty() { return semesterCourseId; }
    public StringProperty titleProperty() { return title; }
    public StringProperty departmentTitleProperty() { return departmentTitle; }
    public IntegerProperty semesterNumberProperty() { return semesterNumber; }
    public StringProperty academicYearProperty() { return academicYear; }

    public int getSemesterCourseId() { return semesterCourseId.get(); }
    public String getTitle() { return title.get(); }
    public String getDepartmentTitle() { return departmentTitle.get(); }
    public int getSemesterNumber() { return semesterNumber.get(); }
    public String getAcademicYear() { return academicYear.get(); }
}