package com.researchmate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** Tab 3: the most frequent words of one paper. */
public class KeywordsPanel extends JPanel implements MainFrame.Refreshable {

    private final PaperDao dao;
    private final JComboBox<Paper> papers = new JComboBox<>();
    private final JSpinner topN = new JSpinner(new SpinnerNumberModel(20, 1, 200, 1));
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"Rank", "Keyword", "Count"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public KeywordsPanel(PaperDao dao) {
        this.dao = dao;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton analyze = new JButton("Analyze");
        top.add(new JLabel("Paper:"));
        top.add(papers);
        top.add(new JLabel("Top words:"));
        top.add(topN);
        top.add(analyze);
        add(top, BorderLayout.NORTH);

        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        add(new JLabel("Note: these are simply the most frequent words (stop words removed). It is not a topic model."),
                BorderLayout.SOUTH);

        analyze.addActionListener(e -> analyze());
    }

    @Override
    public void refresh() {
        Paper old = (Paper) papers.getSelectedItem();
        papers.removeAllItems();
        try {
            for (Paper p : dao.findAll()) {
                papers.addItem(p);
                if (old != null && old.getId() == p.getId()) {
                    papers.setSelectedItem(p);
                }
            }
        } catch (SQLException e) {
            Ui.showError(this, e);
        }
    }

    private void analyze() {
        Paper p = (Paper) papers.getSelectedItem();
        if (p == null) {
            Ui.message(this, "There is no paper yet. Add or import one first.");
            return;
        }
        try {
            String content = dao.getContent(p.getId());
            if (content.isBlank()) {
                Ui.message(this, "This paper has no text. Import it from a PDF or add the text.");
                return;
            }
            List<Map.Entry<String, Integer>> top = TextAnalyzer.topKeywords(content, (Integer) topN.getValue());
            model.setRowCount(0);
            int rank = 1;
            for (Map.Entry<String, Integer> e : top) {
                model.addRow(new Object[]{rank++, e.getKey(), e.getValue()});
            }
        } catch (SQLException e) {
            Ui.showError(this, e);
        }
    }
}
