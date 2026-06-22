package model;

import javafx.beans.property.*;

public class Student {
    private final IntegerProperty studentId;
    private final StringProperty lastName;
    private final StringProperty firstName;
    private final StringProperty patronymic;
    private final StringProperty phoneNumber;
    private final StringProperty address;
    private final StringProperty fullName;

    public Student(int studentId, String lastName, String firstName, String patronymic, String phoneNumber, String address) {
        this.studentId = new SimpleIntegerProperty(studentId);
        this.lastName = new SimpleStringProperty(lastName);
        this.firstName = new SimpleStringProperty(firstName);
        this.patronymic = new SimpleStringProperty(patronymic);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.address = new SimpleStringProperty(address);
        this.fullName = new SimpleStringProperty(lastName + " " + firstName + " " + (patronymic != null ? patronymic : ""));
    }

    public IntegerProperty studentIdProperty() { return studentId; }
    public StringProperty fullNameProperty() { return fullName; }
    public StringProperty phoneNumberProperty() { return phoneNumber; }

    public int getStudentId() { return studentId.get(); }
    public String getFullName() { return fullName.get(); }
    public String getPhoneNumber() { return phoneNumber.get(); }
}