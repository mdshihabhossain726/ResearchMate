package com.researchmate;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Font;
import java.awt.Insets;
import java.sql.SQLException;

/** Start of the program: connect to PostgreSQL, create the tables, open the login window. */
public class Main {

    public static void main(String[] args) {
        setLook();
        Db.loadSettings();
        try {
            Db.setup();
        } catch (SQLException e) {
            Ui.message(null, "Cannot connect to PostgreSQL:\n" + e.getMessage()
                    + "\n\nCheck that PostgreSQL is running and that db.properties has the right password.");
            return;
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    /** Modern flat look (FlatLaf); if it is not available, the system look is used. Only looks, no logic. */
    private static void setLook() {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // the default look is fine too
            }
        }
        UIManager.put("defaultFont", new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("Table.rowHeight", 28);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.alternateRowColor", new java.awt.Color(0xF5F8FF));
        UIManager.put("TableHeader.font", new Font(Font.SANS_SERIF, Font.BOLD, 14));
        UIManager.put("TabbedPane.font", new Font(Font.SANS_SERIF, Font.BOLD, 15));
        UIManager.put("TabbedPane.tabInsets", new Insets(8, 18, 8, 18));
        UIManager.put("TextArea.margin", new Insets(8, 10, 8, 10));
        UIManager.put("TableHeader.background", new java.awt.Color(0xEEF2FF));
        UIManager.put("Table.selectionBackground", new java.awt.Color(0xDBEAFE));
        UIManager.put("Table.selectionForeground", new java.awt.Color(0x1E3A8A));
        UIManager.put("TabbedPane.tabType", "underlined");
        UIManager.put("TabbedPane.underlineColor", new java.awt.Color(0x2563EB));
        UIManager.put("TabbedPane.selectedForeground", new java.awt.Color(0x2563EB));
        UIManager.put("TabbedPane.showTabSeparators", false);
        UIManager.put("ProgressBar.arc", 999);
    }
}
