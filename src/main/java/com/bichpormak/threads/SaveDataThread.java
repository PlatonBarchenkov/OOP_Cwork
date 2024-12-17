package com.bichpormak.threads;

import com.bichpormak.gui.SchoolManagementSystem;
import com.bichpormak.exceptions.DataSaveException;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

/**
 * Класс для сохранения данных в отдельном потоке.
 */
public class SaveDataThread extends Thread {
    private SchoolManagementSystem mainApp;
    private java.util.concurrent.CountDownLatch loadLatch;
    private java.util.concurrent.CountDownLatch saveLatch;

    public SaveDataThread(SchoolManagementSystem mainApp, java.util.concurrent.CountDownLatch loadLatch, java.util.concurrent.CountDownLatch saveLatch) {
        super("SaveDataThread");
        this.mainApp = mainApp;
        this.loadLatch = loadLatch;
        this.saveLatch = saveLatch;
    }

    @Override
    public void run() {
        try {
            System.out.println(getName() + ": Ожидание завершения загрузки данных.");
            loadLatch.await(); // Ждем завершения загрузки данных
            System.out.println(getName() + ": Начало сохранения данных.");

            mainApp.saveDataToFile();

            System.out.println(getName() + ": Завершено сохранение данных.");
            saveLatch.countDown(); // Уведомляем следующий поток о завершении сохранения
        } catch (InterruptedException e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Поток сохранения данных был прерван.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            });
            e.printStackTrace();
        } catch (DataSaveException e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Ошибка сохранения данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            });
            e.printStackTrace();
        }
    }
}
