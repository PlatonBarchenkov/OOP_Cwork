package com.bichpormak;

import com.bichpormak.gui.SchoolManagementSystem;

import javax.swing.SwingUtilities;

/**
 * Точка входа в программу. Запуск приложения.
 */
public class Main {
    public static void main(String[] args) {
        // Запуск интерфейса в потоке обработки событий Swing
        SwingUtilities.invokeLater(() -> new SchoolManagementSystem());
    }
}
