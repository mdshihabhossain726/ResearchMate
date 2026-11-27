package com.researchmate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/** The main window of one logged-in user: a blue header and four tabs. */
public class MainFrame extends JFrame {

    /** A screen that reloads its data when it is shown. */
    public interface Refreshable {
        void refresh();
    }

    public MainFrame(User user) {
        super("ResearchMate - " + user.getFullName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        PaperDao dao = new PaperDao(user.getId());   // this user's papers only
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("My Papers", new PapersPanel(dao));
        tabs.addTab("Import PDF", new ImportPanel(dao));
        tabs.addTab("Keywords", new KeywordsPanel(dao));
        tabs.addTab("Compare", new ComparePanel(dao));
        tabs.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        Ui.polish(tabs);

        add(header(user), BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        tabs.addChangeListener(e -> refreshTab(tabs.getSelectedComponent()));
        refreshTab(tabs.getSelectedComponent());
    }

    /** Blue bar on top: name of the app, who is logged in, and the Log out button. */
    private JPanel header(User user) {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, new Color(0x1E3A8A), getWidth(), 0, new Color(0x2563EB)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        bar.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));

        JLabel title = new JLabel("ResearchMate");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("Research Paper Assistant");
        subtitle.setForeground(new Color(0xBFDBFE));
        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        left.add(title);
        left.add(subtitle);
        bar.add(left, BorderLayout.WEST);

        JLabel who = new JLabel(user.getFullName() + "  (@" + user.getUsername() + ")");
        who.setForeground(Color.WHITE);
        JButton logout = new JButton("Log out");
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.setFocusPainted(false);
        logout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 0, 0, 14);
        right.add(who, gc);
        gc.insets = new Insets(0, 0, 0, 0);
        right.add(logout, gc);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private void refreshTab(Component c) {
        if (c instanceof Refreshable r) {
            r.refresh();
        }
    }
}
