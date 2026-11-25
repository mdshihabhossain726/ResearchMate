package com.researchmate;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.Component;

/** The main window: four tabs. */
public class MainFrame extends JFrame {

    /** A screen that reloads its data when it is shown. */
    public interface Refreshable {
        void refresh();
    }

    public MainFrame() {
        super("ResearchMate Easy - Research Paper Assistant");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        PaperDao dao = new PaperDao();
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("My Papers", new PapersPanel(dao));
        tabs.addTab("Import PDF", new ImportPanel(dao));
        tabs.addTab("Keywords", new KeywordsPanel(dao));
        tabs.addTab("Compare", new ComparePanel(dao));
        add(tabs);

        tabs.addChangeListener(e -> refreshTab(tabs.getSelectedComponent()));
        refreshTab(tabs.getSelectedComponent());
    }

    private void refreshTab(Component c) {
        if (c instanceof Refreshable r) {
            r.refresh();
        }
    }
}
