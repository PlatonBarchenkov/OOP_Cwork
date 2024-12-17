package com.bichpormak.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Класс для панели фильтрации и поиска данных.
 */
public class FilterPanel extends JPanel {
    private JComboBox<String> searchCriteria;
    private JTextField searchField;
    private JButton searchButton;
    private JButton resetButton;

    public FilterPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        searchCriteria = new JComboBox<>();
        searchField = new JTextField(20);
        searchButton = new JButton("Поиск");
        resetButton = new JButton("Сбросить");

        add(new JLabel("Критерий поиска: "));
        add(searchCriteria);
        add(new JLabel("Значение: "));
        add(searchField);
        add(searchButton);
        add(resetButton);
    }

    // Методы для обновления критериев поиска
    public void setSearchCriteria(String[] criteria) {
        searchCriteria.removeAllItems();
        for (String criterion : criteria) {
            searchCriteria.addItem(criterion);
        }
    }

    public String getSelectedCriterion() {
        return (String) searchCriteria.getSelectedItem();
    }

    public String getSearchValue() {
        return searchField.getText().trim();
    }

    public void addSearchListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    public void addResetListener(ActionListener listener) {
        resetButton.addActionListener(listener);
    }

    public void resetFields() {
        searchField.setText("");
    }
}
