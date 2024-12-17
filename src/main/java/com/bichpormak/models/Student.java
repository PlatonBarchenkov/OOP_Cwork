package com.bichpormak.models;

/**
 * Класс, представляющий ученика.
 */
public class Student {
    private String name;
    private String studentClass;
    private String performance;

    public Student(String name, String studentClass, String performance) {
        this.name = name;
        this.studentClass = studentClass;
        this.performance = performance;
    }

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
    }
}
