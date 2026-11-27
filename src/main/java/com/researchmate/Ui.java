package com.researchmate;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.sql.SQLException;
import java.time.Year;
import java.util.Set;

/** Small helpers shared by all screens: message boxes, input checks and CSV text. */
public class Ui {

    public static void message(Component parent, String text) {
        JOptionPane.showMessageDialog(parent, text, "ResearchMate", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, Exception e) {
        String text;
        if (e instanceof SQLException sql && "23505".equals(sql.getSQLState())) {   // 23505 = duplicate value
            text = "This paper (same title and year) already exists.";
        } else {
            text = "Problem: " + e.getMessage();
        }
        JOptionPane.showMessageDialog(parent, text, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirm(Component parent, String question) {
        return JOptionPane.showConfirmDialog(parent, question, "Please confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /**
     * Checks the three required fields of a paper.
     * @return the year as a number
     * @throws IllegalArgumentException with a message that can be shown to the user
     */
    public static int checkPaper(String title, String authors, String yearText) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required.");
        }
        if (title.trim().length() > 500) {
            throw new IllegalArgumentException("Title is too long (maximum 500 characters).");
        }
        if (authors == null || authors.isBlank()) {
            throw new IllegalArgumentException("Authors are required.");
        }
        if (authors.trim().length() > 1000) {
            throw new IllegalArgumentException("Authors are too long (maximum 1000 characters).");
        }
        int maxYear = Year.now().getValue() + 1;
        try {
            int year = Integer.parseInt(yearText.trim());
            if (year < 1900 || year > maxYear) {
                throw new IllegalArgumentException("Year must be between 1900 and " + maxYear + ".");
            }
            return year;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Year must be a number like 2021.");
        }
    }

    /** Puts a value in quotes if it contains a comma, a quote or a line break (CSV rule). */
    public static String csv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private static final Set<String> MAIN_BUTTONS = Set.of("Add", "Search", "Compare", "Analyze", "Save paper");

    /** Only looks: gives every button a hand cursor, main buttons blue and "Delete" red. */
    public static void polish(Container root) {
        for (Component c : root.getComponents()) {
            if (c instanceof JButton b) {
                b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                b.setFocusPainted(false);
                if ("Delete".equals(b.getText())) {
                    b.setBackground(new Color(0xDC2626));
                    b.setForeground(Color.WHITE);
                } else if (MAIN_BUTTONS.contains(b.getText())) {
                    b.setBackground(new Color(0x2563EB));
                    b.setForeground(Color.WHITE);
                }
            }
            if (c instanceof Container inner) {
                polish(inner);
            }
        }
    }
}
