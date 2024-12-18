package com.bichpormak.threads;

import com.bichpormak.gui.SchoolManagementSystem;
import com.bichpormak.exceptions.DataLoadException;

import java.util.concurrent.CountDownLatch;

/**
 * Поток для загрузки данных из базы данных.
 */
public class LoadDataThread extends Thread {
    private SchoolManagementSystem sms;
    private CountDownLatch latch;

    public LoadDataThread(SchoolManagementSystem sms, CountDownLatch latch) {
        this.sms = sms;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            sms.loadDataFromDatabase();
        } catch (DataLoadException e) {
            javax.swing.JOptionPane.showMessageDialog(null, e.getMessage(), "Ошибка загрузки", javax.swing.JOptionPane.ERROR_MESSAGE);
            sms.setButtonStatesAfterLoad(false);
        } finally {
            latch.countDown();
            sms.setButtonStatesDuringLoad(false);
        }
    }
}
