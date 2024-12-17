package com.bichpormak.models;

/**
 * Класс, представляющий учителя.
 */
public class Teacher {
    private String name;
    private String subject;
    private String classes;

    public Teacher(String name, String subject, String classes) {
        this.name = name;
        this.subject = subject;
        this.classes = classes;
    }

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }
}
