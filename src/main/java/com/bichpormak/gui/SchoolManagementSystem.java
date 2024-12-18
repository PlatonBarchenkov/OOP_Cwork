package com.bichpormak.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.bichpormak.data.DataManager;
import com.bichpormak.exceptions.*;
import com.bichpormak.models.Teacher;
import com.bichpormak.models.Student;
import com.bichpormak.report.ReportManager;
import com.bichpormak.threads.GenerateReportThread;
import com.bichpormak.threads.LoadDataThread;
import com.bichpormak.threads.SaveDataThread;

/**
 * Главный класс приложения для управления школой.
 */
public class SchoolManagementSystem {
    private JFrame frame;
    private JTabbedPane tabbedPane;
    private Toolbar toolbar;
    private FilterPanel filterPanel;
    private TeacherPanel teacherPanel;
    private StudentPanel studentPanel;
    private DataManager dataManager;
    private ReportManager reportManager;

    private List<Teacher> teachers;
    private List<Student> students;

    // CountDownLatch для синхронизации потоков
    private final CountDownLatch loadLatch = new CountDownLatch(1);
    private final CountDownLatch saveLatch = new CountDownLatch(1);

    public SchoolManagementSystem() {
        // Инициализация данных
        teachers = new ArrayList<>();
        students = new ArrayList<>();

        // Создание главного окна
        frame = new JFrame("Система Управления Школой");
        frame.setSize(1000, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Инициализация менеджеров
        dataManager = new DataManager(frame);
        reportManager = new ReportManager(frame);

        // Создание панели инструментов
        toolbar = new Toolbar();
        frame.add(toolbar, BorderLayout.NORTH);

        // Создание вкладок с таблицами
        tabbedPane = new JTabbedPane();
        teacherPanel = new TeacherPanel(teachers);
        studentPanel = new StudentPanel(students);
        tabbedPane.addTab("Учителя", teacherPanel);
        tabbedPane.addTab("Ученики", studentPanel);
        frame.add(tabbedPane, BorderLayout.CENTER);

        // Создание панели фильтрации
        filterPanel = new FilterPanel();
        frame.add(filterPanel, BorderLayout.SOUTH);

        // Обновление критериев поиска при смене вкладки
        tabbedPane.addChangeListener(e -> updateSearchCriteria());

        updateSearchCriteria();

        // Добавление слушателей к кнопкам
        addListeners();

        // Установка начального состояния кнопок
        setInitialButtonStates();

        // Отображение окна
        frame.setVisible(true);
    }

    /**
     * Метод для установки начального состояния кнопок.
     * Только кнопка "Загрузить данные" активна.
     */
    private void setInitialButtonStates() {
        toolbar.setAddTeacherEnabled(false);
        toolbar.setAddStudentEnabled(false);
        toolbar.setDeleteTeacherEnabled(false);
        toolbar.setDeleteStudentEnabled(false);
        toolbar.setSaveEnabled(false);
        toolbar.setGenerateReportEnabled(false);
    }

    /**
     * Метод для добавления слушателей к кнопкам.
     */
    private void addListeners() {
        // Слушатель для кнопки "Поиск"
        filterPanel.addSearchListener(e -> {
            String criterion = filterPanel.getSelectedCriterion();
            String value = filterPanel.getSearchValue();
            searchTable(criterion, value);
        });

        // Слушатель для кнопки "Сбросить"
        filterPanel.addResetListener(e -> resetTable());

        // Слушатель для кнопки "Добавить учителя"
        toolbar.addAddTeacherListener(e -> addTeacherDialog());

        // Слушатель для кнопки "Удалить учителя"
        toolbar.addDeleteTeacherListener(e -> deleteSelectedTeachers());

        // Слушатель для кнопки "Добавить ученика"
        toolbar.addAddStudentListener(e -> addStudentDialog());

        // Слушатель для кнопки "Удалить ученика"
        toolbar.addDeleteStudentListener(e -> deleteSelectedStudents());

        // Слушатель для кнопки "Загрузить данные"
        toolbar.addLoadListener(e -> {
            // Отключаем все кнопки кроме загрузки
            setButtonStatesDuringLoad(true);
            // Запускаем поток загрузки данных
            Thread loadThread = new LoadDataThread(this, loadLatch);
            loadThread.start();
        });

        // Слушатель для кнопки "Сохранить данные"
        toolbar.addSaveListener(e -> {
            // Отключаем кнопку создания отчёта до завершения сохранения
            toolbar.setGenerateReportEnabled(false);
            // Запускаем поток сохранения данных
            Thread saveThread = new SaveDataThread(this, loadLatch, saveLatch);
            saveThread.start();
        });

        // Слушатель для кнопки "Создать отчёт"
        toolbar.addGenerateReportListener(e -> {
            // Запускаем поток генерации отчёта
            Thread reportThread = new GenerateReportThread(this, saveLatch);
            reportThread.start();
        });
    }

    /**
     * Обновляет критерии поиска в зависимости от выбранной вкладки.
     */
    private void updateSearchCriteria() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex == 0) { // Учителя
            filterPanel.setSearchCriteria(new String[]{
                    "ФИО учителя", "Предмет", "Классы"
            });
        } else if (selectedIndex == 1) { // Ученики
            filterPanel.setSearchCriteria(new String[]{
                    "ФИО ученика", "Класс ученика", "Успеваемость"
            });
        }
    }

    /**
     * Метод для фильтрации данных в таблице на основе критерия и значения поиска.
     *
     * @param criterion Критерий поиска.
     * @param value     Значение для поиска.
     */
    public void searchTable(String criterion, String value) {
        if (value.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Поле поиска не может быть пустым.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int selectedIndex = tabbedPane.getSelectedIndex();

        if (selectedIndex == 0) { // Учителя
            int columnIndex = -1;
            switch (criterion) {
                case "ФИО учителя":
                    columnIndex = 0;
                    break;
                case "Предмет":
                    columnIndex = 1;
                    break;
                case "Классы":
                    columnIndex = 2;
                    break;
            }

            if (columnIndex != -1) {
                teacherPanel.getTeacherSorter().setRowFilter(RowFilter.regexFilter("(?i)" + value, columnIndex));
            }
        } else if (selectedIndex == 1) { // Ученики
            int columnIndex = -1;
            switch (criterion) {
                case "ФИО ученика":
                    columnIndex = 0;
                    break;
                case "Класс ученика":
                    columnIndex = 1;
                    break;
                case "Успеваемость":
                    columnIndex = 2;
                    break;
            }

            if (columnIndex != -1) {
                studentPanel.getStudentSorter().setRowFilter(RowFilter.regexFilter("(?i)" + value, columnIndex));
            }
        }
    }

    /**
     * Метод для сброса фильтров и восстановления исходных данных.
     */
    public void resetTable() {
        // Сброс фильтра для учителей
        teacherPanel.getTeacherSorter().setRowFilter(null);
        // Сброс фильтра для учеников
        studentPanel.getStudentSorter().setRowFilter(null);
        // Очистка поля поиска
        filterPanel.resetFields();
    }

    /**
     * Метод для отображения диалогового окна добавления учителя.
     */
    private void addTeacherDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2));
        JTextField nameField = new JTextField(20);
        JTextField subjectField = new JTextField(20);
        JTextField classesField = new JTextField(20);

        panel.add(new JLabel("ФИО учителя: "));
        panel.add(nameField);
        panel.add(new JLabel("Предмет: "));
        panel.add(subjectField);
        panel.add(new JLabel("Классы: "));
        panel.add(classesField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Добавить учителя", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                String subject = subjectField.getText();
                String classes = classesField.getText();

                validateFields(name, subject, classes);

                // Создание и добавление учителя
                Teacher newTeacher = new Teacher(name, subject, classes);
                teachers.add(newTeacher);
                teacherPanel.addTeacher(newTeacher);

                JOptionPane.showMessageDialog(frame, "Учитель добавлен!", "Добавление", JOptionPane.INFORMATION_MESSAGE);
                toolbar.setSaveEnabled(true); // Включаем кнопку сохранения
            } catch (InvalidInputException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Метод для отображения диалогового окна добавления ученика.
     */
    private void addStudentDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2));
        JTextField nameField = new JTextField(20);
        JTextField classField = new JTextField(20);
        JTextField performanceField = new JTextField(20);

        panel.add(new JLabel("ФИО ученика: "));
        panel.add(nameField);
        panel.add(new JLabel("Класс: "));
        panel.add(classField);
        panel.add(new JLabel("Успеваемость: "));
        panel.add(performanceField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Добавить ученика", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                String studentClass = classField.getText();
                String performance = performanceField.getText();

                validateFields(name, studentClass, performance);

                // Создание и добавление ученика
                Student newStudent = new Student(name, studentClass, performance);
                students.add(newStudent);
                studentPanel.addStudent(newStudent);

                JOptionPane.showMessageDialog(frame, "Ученик добавлен!", "Добавление", JOptionPane.INFORMATION_MESSAGE);
                toolbar.setSaveEnabled(true); // Включаем кнопку сохранения
            } catch (InvalidInputException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Метод для проверки корректности введенных данных.
     *
     * @param field1 Первое поле (ФИО).
     * @param field2 Второе поле (Предмет/Класс).
     * @param field3 Третье поле (Классы/Успеваемость).
     * @throws InvalidInputException Если хотя бы одно из полей пустое.
     */
    private void validateFields(String field1, String field2, String field3) throws InvalidInputException {
        if (field1.isEmpty() || field2.isEmpty() || field3.isEmpty()) {
            throw new InvalidInputException("Все поля должны быть заполнены!");
        }
        // Дополнительная валидация может быть добавлена здесь, например, проверка формата классов
    }

    /**
     * Метод для удаления выбранных учителей с подтверждением.
     */
    private void deleteSelectedTeachers() {
        JTable table = teacherPanel.getTeacherTable();
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите учителей для удаления.", "Ошибка удаления", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Получение имен выбранных учителей
        StringBuilder names = new StringBuilder();
        for (int row : selectedRows) {
            int modelRow = table.convertRowIndexToModel(row);
            String teacherName = (String) teacherPanel.getTeacherTableModel().getValueAt(modelRow, 0);
            names.append(teacherName).append("\n");
        }

        // Определяем тексты кнопок на русском
        String[] options = {"Да", "Нет"};

        // Показываем диалог подтверждения с русскими кнопками
        int confirm = JOptionPane.showOptionDialog(
                frame,
                "Вы уверены, что хотите удалить следующих учителей?\n" + names.toString(),
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[0]
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Сортировка выбранных строк по убыванию индексов
            int[] sortedRows = selectedRows.clone();
            java.util.Arrays.sort(sortedRows);
            for (int i = sortedRows.length - 1; i >= 0; i--) {
                int modelRow = table.convertRowIndexToModel(sortedRows[i]);
                teacherPanel.removeTeacher(modelRow);
                teachers.remove(modelRow);
            }
            JOptionPane.showMessageDialog(frame, "Учителя удалены.", "Удаление", JOptionPane.INFORMATION_MESSAGE);
            toolbar.setSaveEnabled(true); // Включаем кнопку сохранения
        }
    }

    /**
     * Метод для удаления выбранных учеников с подтверждением.
     */
    private void deleteSelectedStudents() {
        JTable table = studentPanel.getStudentTable();
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Пожалуйста, выберите учеников для удаления.",
                    "Ошибка удаления",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Получение имен выбранных учеников
        StringBuilder names = new StringBuilder();
        for (int row : selectedRows) {
            int modelRow = table.convertRowIndexToModel(row);
            String studentName = (String) studentPanel.getStudentTableModel().getValueAt(modelRow, 0);
            names.append(studentName).append("\n");
        }

        // Определяем тексты кнопок на русском
        String[] options = {"Да", "Нет"};

        // Показываем диалог подтверждения с русскими кнопками
        int confirm = JOptionPane.showOptionDialog(
                frame,
                "Вы уверены, что хотите удалить следующих учеников?\n" + names.toString(),
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[0]
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Сортировка выбранных строк по убыванию индексов
            int[] sortedRows = selectedRows.clone();
            java.util.Arrays.sort(sortedRows);
            for (int i = sortedRows.length - 1; i >= 0; i--) {
                int modelRow = table.convertRowIndexToModel(sortedRows[i]);
                studentPanel.removeStudent(modelRow);
                students.remove(modelRow);
            }
            JOptionPane.showMessageDialog(
                    frame,
                    "Ученики удалены.",
                    "Удаление",
                    JOptionPane.INFORMATION_MESSAGE
            );
            toolbar.setSaveEnabled(true); // Включаем кнопку сохранения
        }
    }

    /**
     * Метод для загрузки данных из базы данных.
     *
     * @throws DataLoadException Если возникает ошибка при загрузке данных.
     */
    public void loadDataFromDatabase() throws DataLoadException {
        dataManager.loadTeachers(teachers);
        dataManager.loadStudents(students);

        // Обновление таблиц
        teacherPanel.getTeacherTableModel().setRowCount(0);
        for (Teacher teacher : teachers) {
            teacherPanel.addTeacher(teacher);
        }

        studentPanel.getStudentTableModel().setRowCount(0);
        for (Student student : students) {
            studentPanel.addStudent(student);
        }

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(frame, "Данные успешно загружены из базы данных.", "Успех", JOptionPane.INFORMATION_MESSAGE);
            // Включаем остальные кнопки после загрузки
            setButtonStatesAfterLoad(true);
        });
    }

    /**
     * Метод для сохранения данных в базу данных.
     *
     * @throws DataSaveException Если возникает ошибка при сохранении данных.
     */
    public void saveDataToDatabase() throws DataSaveException {
        dataManager.saveTeachers(teachers);
        dataManager.saveStudents(students);

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(frame, "Данные успешно сохранены в базу данных.", "Успех", JOptionPane.INFORMATION_MESSAGE);
            // Включаем кнопку создания отчёта после сохранения
            toolbar.setGenerateReportEnabled(true);
        });
    }

    /**
     * Метод для генерации отчёта.
     */
    public void generateReport() {
        reportManager.generateReport(students);
    }

    /**
     * Метод для управления состоянием кнопок во время загрузки данных.
     *
     * @param isLoading true, если данные загружаются; false иначе.
     */
    public void setButtonStatesDuringLoad(boolean isLoading) {
        toolbar.setAddTeacherEnabled(!isLoading && loadLatch.getCount() == 0);
        toolbar.setAddStudentEnabled(!isLoading && loadLatch.getCount() == 0);
        toolbar.setDeleteTeacherEnabled(!isLoading && loadLatch.getCount() == 0);
        toolbar.setDeleteStudentEnabled(!isLoading && loadLatch.getCount() == 0);
        toolbar.setSaveEnabled(!isLoading && loadLatch.getCount() == 0);
        toolbar.setGenerateReportEnabled(false);
        toolbar.setLoadEnabled(!isLoading); // Разрешаем повторную загрузку
    }

    /**
     * Метод для управления состоянием кнопок после загрузки данных.
     *
     * @param isLoaded true, если данные успешно загружены; false иначе.
     */
    public void setButtonStatesAfterLoad(boolean isLoaded) {
        if (isLoaded) {
            toolbar.setAddTeacherEnabled(true);
            toolbar.setAddStudentEnabled(true);
            toolbar.setDeleteTeacherEnabled(true);
            toolbar.setDeleteStudentEnabled(true);
            toolbar.setSaveEnabled(true);
        } else {
            toolbar.setAddTeacherEnabled(false);
            toolbar.setAddStudentEnabled(false);
            toolbar.setDeleteTeacherEnabled(false);
            toolbar.setDeleteStudentEnabled(false);
            toolbar.setSaveEnabled(false);
        }
        // Кнопка создания отчёта остаётся отключённой до сохранения
        toolbar.setGenerateReportEnabled(false);
    }

    /**
     * Точка входа в программу. Запуск приложения.
     *
     * @param args Аргументы командной строки (не используются).
     */
    public static void main(String[] args) {
        // Запуск интерфейса в потоке обработки событий Swing
        SwingUtilities.invokeLater(() -> new SchoolManagementSystem());
    }
}
