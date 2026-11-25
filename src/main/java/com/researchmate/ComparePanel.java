package com.researchmate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/** Tab 4: compare two papers (similarity %, common and unique keywords). */
public class ComparePanel extends JPanel implements MainFrame.Refreshable {

    private final PaperDao dao;
    private final JComboBox<Paper> paperA = new JComboBox<>();
    private final JComboBox<Paper> paperB = new JComboBox<>();
    private final JComboBox<Similarity> method = new JComboBox<>();
    private final JTextArea result = new JTextArea();

    public ComparePanel(PaperDao dao) {
        this.dao = dao;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // POLYMORPHISM: the combo holds Similarity objects; compare() works the same for both.
        method.addItem(new JaccardSimilarity());
        method.addItem(new CosineSimilarity());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton compare = new JButton("Compare");
        top.add(new JLabel("Paper A:"));
        top.add(paperA);
        top.add(new JLabel("Paper B:"));
        top.add(paperB);
        top.add(new JLabel("Method:"));
        top.add(method);
        top.add(compare);
        add(top, BorderLayout.NORTH);

        result.setEditable(false);
        result.setLineWrap(true);
        result.setWrapStyleWord(true);
        add(new JScrollPane(result), BorderLayout.CENTER);
        add(new JLabel("Note: this compares words only, not meaning. Similar words do not always mean similar ideas."),
                BorderLayout.SOUTH);

        compare.addActionListener(e -> compare());
    }

    @Override
    public void refresh() {
        Paper a = (Paper) paperA.getSelectedItem();
        Paper b = (Paper) paperB.getSelectedItem();
        paperA.removeAllItems();
        paperB.removeAllItems();
        try {
            for (Paper p : dao.findAll()) {
                paperA.addItem(p);
                paperB.addItem(p);
                if (a != null && a.getId() == p.getId()) {
                    paperA.setSelectedItem(p);
                }
                if (b != null && b.getId() == p.getId()) {
                    paperB.setSelectedItem(p);
                }
            }
        } catch (SQLException e) {
            Ui.showError(this, e);
        }
    }

    private void compare() {
        Paper a = (Paper) paperA.getSelectedItem();
        Paper b = (Paper) paperB.getSelectedItem();
        if (a == null || b == null || a.getId() == b.getId()) {
            Ui.message(this, "Please choose two different papers.");
            return;
        }
        try {
            String textA = dao.getContent(a.getId());
            String textB = dao.getContent(b.getId());
            if (textA.isBlank() || textB.isBlank()) {
                Ui.message(this, "Both papers need text. Import them from a PDF or add the text.");
                return;
            }
            Similarity s = (Similarity) method.getSelectedItem();
            double value = s.compare(textA, textB);

            Set<String> keysA = TextAnalyzer.topWordSet(textA, 20);
            Set<String> keysB = TextAnalyzer.topWordSet(textB, 20);
            Set<String> common = new TreeSet<>(keysA);
            common.retainAll(keysB);
            Set<String> onlyA = new TreeSet<>(keysA);
            onlyA.removeAll(keysB);
            Set<String> onlyB = new TreeSet<>(keysB);
            onlyB.removeAll(keysA);

            StringBuilder sb = new StringBuilder();
            sb.append("A: ").append(a).append("\nB: ").append(b).append("\n\n");
            sb.append("Method: ").append(s).append('\n');
            sb.append(String.format(Locale.US, "Similarity: %.1f %%%n%n", value * 100));
            sb.append("Common top-20 keywords: ").append(common.isEmpty() ? "(none)" : String.join(", ", common)).append("\n\n");
            sb.append("Only in A: ").append(onlyA.isEmpty() ? "(none)" : String.join(", ", onlyA)).append("\n\n");
            sb.append("Only in B: ").append(onlyB.isEmpty() ? "(none)" : String.join(", ", onlyB)).append('\n');
            result.setText(sb.toString());
            result.setCaretPosition(0);
        } catch (SQLException e) {
            Ui.showError(this, e);
        }
    }
}
