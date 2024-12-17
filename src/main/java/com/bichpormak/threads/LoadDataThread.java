package com.bichpormak.threads;

import com.bichpormak.gui.SchoolManagementSystem;
import com.bichpormak.exceptions.DataLoadException;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

/**
 * Класс для загрузки данных в отдельном потоке.
 */
public class LoadDataThread extends Thread {
    private SchoolManagementSystem mainApp;
    private java.util.concurrent.CountDownLatch latch;

    public LoadDataThread(SchoolManagementSystem mainApp, java.util.concurrent.CountDownLatch latch) {
        super("LoadDataThread");
        this.mainApp = mainApp;
        this.latch = latch;
    }

    @Override
    public void run() {
        System.out.println(getName() + ": Начало загрузки данных.");
        try {
            mainApp.loadDataFromFile();
            System.out.println(getName() + ": Завершена загрузка данных.");
            latch.countDown(); // Уведомляем следующий поток о завершении загрузки
        } catch (DataLoadException e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Ошибка в потоке загрузки данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            });
            e.printStackTrace();
        }
    }
}
