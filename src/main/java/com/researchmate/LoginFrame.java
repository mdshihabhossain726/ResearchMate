package com.researchmate;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.sql.SQLException;

/** First window: sign in, or create a new account. Left = blue welcome side, right = the forms. */
public class LoginFrame extends JFrame {

    private static final Color BLUE = new Color(0x2563EB);
    private static final Color DARK = new Color(0x1E3A8A);
    private static final Color MUTED = new Color(0x64748B);

    private final UserDao users = new UserDao();
    private final CardLayout cards = new CardLayout();
    private final JPanel right = new JPanel(cards);

    private final JTextField loginUser = new JTextField();
    private final JPasswordField loginPass = new JPasswordField();
    private final JLabel loginMsg = errorLabel();
    private final JButton loginBtn = primary("Sign in");

    private final JTextField regName = new JTextField();
    private final JTextField regUser = new JTextField();
    private final JPasswordField regPass = new JPasswordField();
    private final JPasswordField regPass2 = new JPasswordField();
    private final JLabel regMsg = errorLabel();
    private final JButton regBtn = primary("Create account");

    public LoginFrame() {
        super("ResearchMate Easy - Sign in");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 640);
        setResizable(false);
        setLocationRelativeTo(null);

        loginUser.putClientProperty("JTextField.placeholderText", "your username");
        loginPass.putClientProperty("JPasswordField.showRevealButton", true);
        regName.putClientProperty("JTextField.placeholderText", "e.g. Md Shihab Hossain");
        regUser.putClientProperty("JTextField.placeholderText", "e.g. shihab");
        regPass.putClientProperty("JPasswordField.showRevealButton", true);
        regPass2.putClientProperty("JPasswordField.showRevealButton", true);

        right.setBackground(Color.WHITE);
        right.add(form("Welcome back", "Sign in to see your papers.",
                new String[]{"Username", "Password"}, new JComponent[]{loginUser, loginPass},
                loginMsg, loginBtn, "New here?", "Create an account", "register"), "login");
        right.add(form("Create account", "It is free and takes 20 seconds.",
                new String[]{"Full name", "Username", "Password (at least 6 characters)", "Repeat password"},
                new JComponent[]{regName, regUser, regPass, regPass2},
                regMsg, regBtn, "Already have an account?", "Sign in", "login"), "register");

        JPanel root = new JPanel(new GridLayout(1, 2));
        root.add(brandSide());
        root.add(right);
        setContentPane(root);

        loginBtn.addActionListener(e -> doLogin());
        regBtn.addActionListener(e -> doRegister());
        show("login");
    }

    /** The blue gradient side with the name of the app. */
    private JPanel brandSide() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, DARK, getWidth(), getHeight(), BLUE));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(40, 44, 40, 30));

        JLabel name = new JLabel("ResearchMate");
        name.setFont(name.getFont().deriveFont(Font.BOLD, 36f));
        name.setForeground(Color.WHITE);
        JLabel tag = new JLabel("Your research papers, organised.");
        tag.setFont(tag.getFont().deriveFont(16f));
        tag.setForeground(new Color(0xBFDBFE));

        p.add(Box.createVerticalGlue());
        p.add(name);
        p.add(Box.createVerticalStrut(8));
        p.add(tag);
        p.add(Box.createVerticalStrut(36));
        for (String line : new String[]{"Save and search your papers", "Read the text of a PDF",
                "Find the main keywords", "Compare two papers"}) {
            JLabel l = new JLabel("\u2713   " + line);
            l.setFont(l.getFont().deriveFont(15f));
            l.setForeground(Color.WHITE);
            p.add(l);
            p.add(Box.createVerticalStrut(12));
        }
        p.add(Box.createVerticalGlue());
        JLabel foot = new JLabel("Each account sees only its own papers.");
        foot.setForeground(new Color(0x93C5FD));
        p.add(foot);
        return p;
    }

    /** One white form: title, fields, red message line, big button and a link to the other form. */
    private JPanel form(String title, String subtitle, String[] labels, JComponent[] fields,
                        JLabel msg, JButton main, String switchText, String switchLink, String target) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(40, 48, 28, 48));

        JLabel t = new JLabel(title);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 28f));
        t.setForeground(DARK);
        JLabel s = new JLabel(subtitle);
        s.setForeground(MUTED);
        left(t);
        left(s);
        p.add(t);
        p.add(Box.createVerticalStrut(4));
        p.add(s);
        p.add(Box.createVerticalStrut(24));

        for (int i = 0; i < labels.length; i++) {
            JLabel l = new JLabel(labels[i]);
            l.setForeground(MUTED);
            left(l);
            p.add(l);
            p.add(Box.createVerticalStrut(4));
            JComponent f = fields[i];
            left(f);
            f.setPreferredSize(new Dimension(200, 38));
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            p.add(f);
            p.add(Box.createVerticalStrut(12));
        }

        left(msg);
        p.add(msg);
        p.add(Box.createVerticalStrut(8));
        left(main);
        main.setPreferredSize(new Dimension(200, 42));
        main.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        p.add(main);
        p.add(Box.createVerticalGlue());

        JButton link = new JButton(switchLink);
        link.putClientProperty("JButton.buttonType", "borderless");
        link.setForeground(BLUE);
        link.setContentAreaFilled(false);
        link.setBorderPainted(false);
        link.setFocusPainted(false);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.addActionListener(e -> show(target));
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        bottom.setBackground(Color.WHITE);
        JLabel st = new JLabel(switchText);
        st.setForeground(MUTED);
        bottom.add(st);
        bottom.add(link);
        left(bottom);
        bottom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        p.add(bottom);
        return p;
    }

    private void show(String name) {
        loginMsg.setText(" ");
        regMsg.setText(" ");
        cards.show(right, name);
        boolean login = "login".equals(name);
        getRootPane().setDefaultButton(login ? loginBtn : regBtn);   // Enter key presses this button
        setTitle(login ? "ResearchMate Easy - Sign in" : "ResearchMate Easy - Create account");
        (login ? loginUser : regName).requestFocusInWindow();
    }

    private void doLogin() {
        String u = loginUser.getText().trim();
        String p = new String(loginPass.getPassword());
        if (u.isEmpty() || p.isEmpty()) {
            loginMsg.setText("Please enter your username and password.");
            return;
        }
        try {
            User user = users.login(u, p);
            if (user == null) {
                loginMsg.setText("Wrong username or password.");
                return;
            }
            open(user);
        } catch (SQLException e) {
            loginMsg.setText("Database problem. Is PostgreSQL running?");
        }
    }

    private void doRegister() {
        String p = new String(regPass.getPassword());
        try {
            UserDao.check(regName.getText(), regUser.getText(), p);
            if (!p.equals(new String(regPass2.getPassword()))) {
                throw new IllegalArgumentException("The two passwords are not the same.");
            }
            open(users.register(regName.getText(), regUser.getText(), p));
        } catch (IllegalArgumentException e) {
            regMsg.setText(e.getMessage());
        } catch (SQLException e) {
            regMsg.setText("Database problem. Is PostgreSQL running?");
        }
    }

    private void open(User user) {
        dispose();
        new MainFrame(user).setVisible(true);
    }

    private static void left(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private static JLabel errorLabel() {
        JLabel l = new JLabel(" ");
        l.setForeground(new Color(0xDC2626));
        return l;
    }

    private static JButton primary(String text) {
        JButton b = new JButton(text);
        b.setBackground(BLUE);
        b.setForeground(Color.WHITE);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 15f));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
