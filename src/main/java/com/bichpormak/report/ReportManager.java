package com.bichpormak.report;

import com.bichpormak.models.Student;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Класс для генерации отчётов с использованием JasperReports.
 */
public class ReportManager {
    private JFrame frame;

    public ReportManager(JFrame frame) {
        this.frame = frame;
    }

    /**
     * Генерация отчёта.
     *
     * @param students Список учеников для отчёта.
     */
    public void generateReport(List<Student> students) {
        try {
            // Путь к шаблону отчёта
            String reportPath = "lab_08.jrxml"; // Убедитесь, что путь правильный и файл существует
            File reportFile = new File(reportPath);
            if (!reportFile.exists()) {
                JOptionPane.showMessageDialog(frame, "Файл шаблона отчёта не найден: " + reportPath, "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Компиляция шаблона отчёта
            JasperReport jasperReport = JasperCompileManager.compileReport(reportPath);

            // Создание источника данных из списка учеников
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(students);

            // Параметры отчёта (если нужны)
            HashMap<String, Object> parameters = new HashMap<>();

            // Заполнение отчёта данными
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Настройка диалога для выбора формата и места сохранения отчёта
            String[] options = {"PDF", "HTML"};
            int choice = JOptionPane.showOptionDialog(
                    frame,
                    "Выберите формат отчёта для сохранения:",
                    "Выбор формата отчёта",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == JOptionPane.CLOSED_OPTION) {
                return; // Пользователь закрыл диалог без выбора
            }

            // Настройка JFileChooser для выбора места сохранения
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Сохранить отчёт");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(options[choice] + " файлы", options[choice].toLowerCase()));
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

            int userSelection = fileChooser.showSaveDialog(frame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File saveFile = fileChooser.getSelectedFile();
                String filePath = saveFile.getAbsolutePath();

                // Добавляем расширение, если оно отсутствует
                if (!filePath.toLowerCase().endsWith("." + options[choice].toLowerCase())) {
                    filePath += "." + options[choice].toLowerCase();
                }

                if (choice == 0) { // PDF
                    JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);
                } else if (choice == 1) { // HTML
                    JasperExportManager.exportReportToHtmlFile(jasperPrint, filePath);
                }

                JOptionPane.showMessageDialog(frame, "Отчёт успешно сохранён: " + filePath, "Успех", JOptionPane.INFORMATION_MESSAGE);
                JasperViewer.viewReport(jasperPrint, false);
            }

        } catch (JRException e) {
            JOptionPane.showMessageDialog(frame, "Ошибка при создании отчёта: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
