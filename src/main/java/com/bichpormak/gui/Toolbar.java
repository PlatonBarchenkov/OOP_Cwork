package com.bichpormak.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Класс для панели инструментов с кнопками действий.
 */
public class Toolbar extends JToolBar {
    private JButton addTeacherButton;
    private JButton addStudentButton;
    private JButton deleteTeacherButton;
    private JButton deleteStudentButton;
    private JButton generateReportButton;
    private JButton loadButton;
    private JButton saveButton;

    public Toolbar() {
        super("Панель инструментов");

        addTeacherButton = new JButton("Добавить учителя");
        addStudentButton = new JButton("Добавить ученика");
        deleteTeacherButton = new JButton("Удалить учителя");
        deleteStudentButton = new JButton("Удалить ученика");
        generateReportButton = new JButton("Создать отчёт");
        loadButton = new JButton("Загрузить данные");
        saveButton = new JButton("Сохранить данные");

        add(addTeacherButton);
        add(addStudentButton);
        add(deleteTeacherButton);
        add(deleteStudentButton);
        add(generateReportButton);
        add(Box.createHorizontalGlue());
        add(loadButton);
        add(saveButton);
    }

    // Методы для добавления слушателей к кнопкам
    public void addAddTeacherListener(ActionListener listener) {
        addTeacherButton.addActionListener(listener);
    }

    public void addAddStudentListener(ActionListener listener) {
        addStudentButton.addActionListener(listener);
    }

    public void addDeleteTeacherListener(ActionListener listener) {
        deleteTeacherButton.addActionListener(listener);
    }

    public void addDeleteStudentListener(ActionListener listener) {
        deleteStudentButton.addActionListener(listener);
    }

    public void addGenerateReportListener(ActionListener listener) {
        generateReportButton.addActionListener(listener);
    }

    public void addLoadListener(ActionListener listener) {
        loadButton.addActionListener(listener);
    }

    public void addSaveListener(ActionListener listener) {
        saveButton.addActionListener(listener);
    }

    // Методы для управления состоянием кнопок
    public void setAddTeacherEnabled(boolean enabled) {
        addTeacherButton.setEnabled(enabled);
    }

    public void setAddStudentEnabled(boolean enabled) {
        addStudentButton.setEnabled(enabled);
    }

    public void setDeleteTeacherEnabled(boolean enabled) {
        deleteTeacherButton.setEnabled(enabled);
    }

    public void setDeleteStudentEnabled(boolean enabled) {
        deleteStudentButton.setEnabled(enabled);
    }

    public void setGenerateReportEnabled(boolean enabled) {
        generateReportButton.setEnabled(enabled);
    }

    public void setLoadEnabled(boolean enabled) {
        loadButton.setEnabled(enabled);
    }

    public void setSaveEnabled(boolean enabled) {
        saveButton.setEnabled(enabled);
    }
}
