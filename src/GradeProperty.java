import javafx.beans.property.*;

public class GradeProperty {
    private final IntegerProperty studentId;
    private final StringProperty studentName;
    private final IntegerProperty grade;
    private final StringProperty examDate;

    public GradeProperty(int studentId, String studentName, Integer grade, String examDate) {
        this.studentId = new SimpleIntegerProperty(studentId);
        this.studentName = new SimpleStringProperty(studentName);
        this.grade = new SimpleIntegerProperty(grade != null ? grade : 0);
        this.examDate = new SimpleStringProperty(examDate != null ? examDate : "-");
    }

    public IntegerProperty studentIdProperty() { return studentId; }
    public int getStudentId() { return studentId.get(); }

    public StringProperty studentNameProperty() { return studentName; }
    public String getStudentName() { return studentName.get(); }

    public IntegerProperty gradeProperty() { return grade; }
    public int getGrade() { return grade.get(); }

    public StringProperty examDateProperty() { return examDate; }
    public String getExamDate() { return examDate.get(); }
}