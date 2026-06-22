package model;

import javafx.beans.property.*;

public class Teacher {
    private final IntegerProperty teacherId;
    private final StringProperty lastName;
    private final StringProperty firstName;
    private final StringProperty patronymic;
    private final StringProperty position;
    private final StringProperty fullName;

    public Teacher(int teacherId, String lastName, String firstName, String patronymic, String position) {
        this.teacherId = new SimpleIntegerProperty(teacherId);
        this.lastName = new SimpleStringProperty(lastName);
        this.firstName = new SimpleStringProperty(firstName);
        this.patronymic = new SimpleStringProperty(patronymic);
        this.position = new SimpleStringProperty(position);
        this.fullName = new SimpleStringProperty(lastName + " " + firstName + " " + (patronymic != null ? patronymic : ""));
    }

    public IntegerProperty teacherIdProperty() { return teacherId; }
    public StringProperty positionProperty() { return position; }
    public StringProperty fullNameProperty() { return fullName; }

    public int getTeacherId() { return teacherId.get(); }
    public String getPosition() { return position.get(); }
    public String getFullName() { return fullName.get(); }
}