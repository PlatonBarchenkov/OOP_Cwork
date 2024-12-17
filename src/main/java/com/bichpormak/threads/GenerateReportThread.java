package com.bichpormak.threads;

import com.bichpormak.gui.SchoolManagementSystem;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

/**
 * Класс для генерации отчёта в отдельном потоке.
 */
public class GenerateReportThread extends Thread {
    private SchoolManagementSystem mainApp;
    private java.util.concurrent.CountDownLatch latch;

    public GenerateReportThread(SchoolManagementSystem mainApp, java.util.concurrent.CountDownLatch latch) {
        super("GenerateReportThread");
        this.mainApp = mainApp;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            System.out.println(getName() + ": Ожидание завершения сохранения данных.");
            latch.await(); // Ждем завершения сохранения данных
            System.out.println(getName() + ": Начало генерации отчёта.");

            mainApp.generateReport();

            System.out.println(getName() + ": Завершена генерация отчёта.");
        } catch (InterruptedException e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Поток генерации отчёта был прерван.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            });
            e.printStackTrace();
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Ошибка генерации отчёта: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            });
            e.printStackTrace();
        }
    }
}
