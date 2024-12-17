package com.bichpormak.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import com.bichpormak.models.Teacher;

/**
 * Класс для панели отображения и управления таблицей учителей.
 */
public class TeacherPanel extends JPanel {
    private JTable teacherTable;
    private DefaultTableModel teacherTableModel;
    private TableRowSorter<DefaultTableModel> teacherSorter;

    public TeacherPanel(List<Teacher> teachers) {
        setLayout(new BorderLayout());

        String[] columns = {"ФИО учителя", "Предмет", "Классы"};
        teacherTableModel = new DefaultTableModel(columns, 0);
        for (Teacher teacher : teachers) {
            teacherTableModel.addRow(new Object[]{teacher.getName(), teacher.getSubject(), teacher.getClasses()});
        }

        teacherTable = new JTable(teacherTableModel);
        teacherTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(teacherTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Учителя"));

        teacherSorter = new TableRowSorter<>(teacherTableModel);
        teacherTable.setRowSorter(teacherSorter);

        add(scrollPane, BorderLayout.CENTER);
    }

    public JTable getTeacherTable() {
        return teacherTable;
    }

    public DefaultTableModel getTeacherTableModel() {
        return teacherTableModel;
    }

    public TableRowSorter<DefaultTableModel> getTeacherSorter() {
        return teacherSorter;
    }

    public void addTeacher(Teacher teacher) {
        teacherTableModel.addRow(new Object[]{teacher.getName(), teacher.getSubject(), teacher.getClasses()});
    }

    public void removeTeacher(int modelRow) {
        teacherTableModel.removeRow(modelRow);
    }
}
