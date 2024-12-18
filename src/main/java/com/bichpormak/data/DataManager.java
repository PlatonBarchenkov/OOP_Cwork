package com.bichpormak.data;

import com.bichpormak.models.Teacher;
import com.bichpormak.models.Student;
import com.bichpormak.exceptions.DataLoadException;
import com.bichpormak.exceptions.DataSaveException;

import java.sql.*;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Класс для управления данными с использованием SQLite.
 */
public class DataManager {

    private static final String DB_URL = "jdbc:sqlite:school.db";
    private JFrame frame;

    public DataManager(JFrame frame) {
        this.frame = frame;
        initializeDatabase();
    }

    /**
     * Инициализирует базу данных, создавая необходимые таблицы, если они не существуют.
     */
    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                String createTeachersTable = "CREATE TABLE IF NOT EXISTS teachers ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "name TEXT NOT NULL,"
                        + "subject TEXT NOT NULL,"
                        + "classes TEXT NOT NULL"
                        + ");";

                String createStudentsTable = "CREATE TABLE IF NOT EXISTS students ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "name TEXT NOT NULL,"
                        + "class TEXT NOT NULL,"
                        + "performance TEXT NOT NULL"
                        + ");";

                Statement stmt = conn.createStatement();
                stmt.execute(createTeachersTable);
                stmt.execute(createStudentsTable);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Ошибка инициализации базы данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Загружает данные учителей из базы данных.
     *
     * @param teachers Список для добавления учителей.
     * @throws DataLoadException Если возникает ошибка при загрузке данных.
     */
    public void loadTeachers(List<Teacher> teachers) throws DataLoadException {
        String query = "SELECT name, subject, classes FROM teachers";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            teachers.clear();
            while (rs.next()) {
                String name = rs.getString("name");
                String subject = rs.getString("subject");
                String classes = rs.getString("classes");
                Teacher teacher = new Teacher(name, subject, classes);
                teachers.add(teacher);
            }

        } catch (SQLException e) {
            throw new DataLoadException("Ошибка при загрузке учителей: " + e.getMessage());
        }
    }

    /**
     * Загружает данные учеников из базы данных.
     *
     * @param students Список для добавления учеников.
     * @throws DataLoadException Если возникает ошибка при загрузке данных.
     */
    public void loadStudents(List<Student> students) throws DataLoadException {
        String query = "SELECT name, class, performance FROM students";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            students.clear();
            while (rs.next()) {
                String name = rs.getString("name");
                String studentClass = rs.getString("class");
                String performance = rs.getString("performance");
                Student student = new Student(name, studentClass, performance);
                students.add(student);
            }

        } catch (SQLException e) {
            throw new DataLoadException("Ошибка при загрузке учеников: " + e.getMessage());
        }
    }

    /**
     * Сохраняет список учителей в базу данных.
     *
     * @param teachers Список учителей для сохранения.
     * @throws DataSaveException Если возникает ошибка при сохранении данных.
     */
    public void saveTeachers(List<Teacher> teachers) throws DataSaveException {
        String deleteAll = "DELETE FROM teachers";
        String insert = "INSERT INTO teachers(name, subject, classes) VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(deleteAll);
            }

            try (PreparedStatement pstmt = conn.prepareStatement(insert)) {
                for (Teacher teacher : teachers) {
                    pstmt.setString(1, teacher.getName());
                    pstmt.setString(2, teacher.getSubject());
                    pstmt.setString(3, teacher.getClasses());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            throw new DataSaveException("Ошибка при сохранении учителей: " + e.getMessage());
        }
    }

    /**
     * Сохраняет список учеников в базу данных.
     *
     * @param students Список учеников для сохранения.
     * @throws DataSaveException Если возникает ошибка при сохранении данных.
     */
    public void saveStudents(List<Student> students) throws DataSaveException {
        String deleteAll = "DELETE FROM students";
        String insert = "INSERT INTO students(name, class, performance) VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(deleteAll);
            }

            try (PreparedStatement pstmt = conn.prepareStatement(insert)) {
                for (Student student : students) {
                    pstmt.setString(1, student.getName());
                    pstmt.setString(2, student.getStudentClass());
                    pstmt.setString(3, student.getPerformance());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            throw new DataSaveException("Ошибка при сохранении учеников: " + e.getMessage());
        }
    }
}
