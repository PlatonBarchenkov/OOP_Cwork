package com.bichpormak.threads;

import com.bichpormak.gui.SchoolManagementSystem;
import com.bichpormak.exceptions.DataSaveException;

import java.util.concurrent.CountDownLatch;

/**
 * Поток для сохранения данных в базу данных.
 */
public class SaveDataThread extends Thread {
    private SchoolManagementSystem sms;
    private CountDownLatch loadLatch;
    private CountDownLatch saveLatch;

    public SaveDataThread(SchoolManagementSystem sms, CountDownLatch loadLatch, CountDownLatch saveLatch) {
        this.sms = sms;
        this.loadLatch = loadLatch;
        this.saveLatch = saveLatch;
    }

    @Override
    public void run() {
        try {
            sms.saveDataToDatabase();
        } catch (DataSaveException e) {
            javax.swing.JOptionPane.showMessageDialog(null, e.getMessage(), "Ошибка сохранения", javax.swing.JOptionPane.ERROR_MESSAGE);
        } finally {
            saveLatch.countDown();
        }
    }
}
