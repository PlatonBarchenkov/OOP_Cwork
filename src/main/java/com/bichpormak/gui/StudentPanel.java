package com.bichpormak.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import com.bichpormak.models.Student;

/**
 * Класс для панели отображения и управления таблицей учеников.
 */
public class StudentPanel extends JPanel {
    private JTable studentTable;
    private DefaultTableModel studentTableModel;
    private TableRowSorter<DefaultTableModel> studentSorter;

    public StudentPanel(List<Student> students) {
        setLayout(new BorderLayout());

        String[] columns = {"ФИО ученика", "Класс", "Успеваемость"};
        studentTableModel = new DefaultTableModel(columns, 0);
        for (Student student : students) {
            studentTableModel.addRow(new Object[]{student.getName(), student.getStudentClass(), student.getPerformance()});
        }

        studentTable = new JTable(studentTableModel);
        studentTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Ученики"));

        studentSorter = new TableRowSorter<>(studentTableModel);
        studentTable.setRowSorter(studentSorter);

        add(scrollPane, BorderLayout.CENTER);
    }

    public JTable getStudentTable() {
        return studentTable;
    }

    public DefaultTableModel getStudentTableModel() {
        return studentTableModel;
    }

    public TableRowSorter<DefaultTableModel> getStudentSorter() {
        return studentSorter;
    }

    public void addStudent(Student student) {
        studentTableModel.addRow(new Object[]{student.getName(), student.getStudentClass(), student.getPerformance()});
    }

    public void removeStudent(int modelRow) {
        studentTableModel.removeRow(modelRow);
    }
}
