package model;

public class TeacherProperty {
    private final int teacherId;
    private final String fullName;

    public TeacherProperty(int teacherId, String lastName, String firstName, String patronymic) {
        this.teacherId = teacherId;
        this.fullName = lastName + " " + firstName + " " + (patronymic != null ? patronymic : "");
    }

    public int getTeacherId() { return teacherId; }

    @Override
    public String toString() { return fullName.trim(); }
}