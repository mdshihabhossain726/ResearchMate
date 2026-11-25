package com.researchmate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Tab 1: list, search, add, edit, delete papers and export them to CSV. */
public class PapersPanel extends JPanel implements MainFrame.Refreshable {

    private final PaperDao dao;
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Title", "Authors", "Year"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JTextField searchField = new JTextField(25);
    private List<Paper> shown = new ArrayList<>();

    public PapersPanel(PaperDao dao) {
        this.dao = dao;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton search = new JButton("Search");
        JButton all = new JButton("Show all");
        top.add(new JLabel("Title or author:"));
        top.add(searchField);
        top.add(search);
        top.add(all);
        add(top, BorderLayout.NORTH);

        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(3).setMaxWidth(80);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add");
        JButton edit = new JButton("Edit");
        JButton delete = new JButton("Delete");
        JButton export = new JButton("Export CSV");
        bottom.add(add);
        bottom.add(edit);
        bottom.add(delete);
        bottom.add(export);
        add(bottom, BorderLayout.SOUTH);

        search.addActionListener(e -> load(searchField.getText()));
        searchField.addActionListener(e -> load(searchField.getText()));
        all.addActionListener(e -> {
            searchField.setText("");
            load("");
        });
        add.addActionListener(e -> addPaper());
        edit.addActionListener(e -> editPaper());
        delete.addActionListener(e -> deletePaper());
        export.addActionListener(e -> exportCsv());
    }

    @Override
    public void refresh() {
        load(searchField.getText());
    }

    private void load(String text) {
        try {
            shown = dao.search(text);
        } catch (SQLException e) {
            Ui.showError(this, e);
            return;
        }
        model.setRowCount(0);
        for (Paper p : shown) {
            model.addRow(new Object[]{p.getId(), p.getTitle(), p.getAuthors(), p.getYear()});
        }
    }

    private Paper selected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            Ui.message(this, "Please select a paper in the table first.");
            return null;
        }
        return shown.get(row);
    }

    private void addPaper() {
        Paper p = askPaper(null);
        if (p == null) {
            return;
        }
        try {
            dao.add(p);
            load(searchField.getText());
        } catch (SQLException e) {
            Ui.showError(this, e);
        }
    }

    private void editPaper() {
        Paper old = selected();
        if (old == null) {
            return;
        }
        Paper p = askPaper(old);
        if (p == null) {
            return;
        }
        try {
            dao.update(p);
            load(searchField.getText());
        } catch (SQLException e) {
            Ui.showError(this, e);
        }
    }

    private void deletePaper() {
        Paper p = selected();
        if (p == null || !Ui.confirm(this, "Delete \"" + p.getTitle() + "\"?")) {
            return;
        }
        try {
            dao.delete(p.getId());
            load(searchField.getText());
        } catch (SQLException e) {
            Ui.showError(this, e);
        }
    }

    /** Shows the input form. existing == null means "add" (then a text box for the paper text is shown too). */
    private Paper askPaper(Paper existing) {
        JTextField title = new JTextField(existing == null ? "" : existing.getTitle(), 35);
        JTextField authors = new JTextField(existing == null ? "" : existing.getAuthors(), 35);
        JTextField year = new JTextField(existing == null ? "" : String.valueOf(existing.getYear()), 8);
        JTextArea content = new JTextArea(6, 35);
        content.setLineWrap(true);
        content.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 4));
        form.add(new JLabel("Title:"));
        form.add(title);
        form.add(new JLabel("Authors:"));
        form.add(authors);
        form.add(new JLabel("Year:"));
        form.add(year);
        if (existing == null) {
            form.add(new JLabel("Paper text (optional, needed for Keywords / Compare):"));
            form.add(new JScrollPane(content));
        }

        while (true) {
            int answer = JOptionPane.showConfirmDialog(this, form, existing == null ? "Add paper" : "Edit paper",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (answer != JOptionPane.OK_OPTION) {
                return null;
            }
            try {
                int y = Ui.checkPaper(title.getText(), authors.getText(), year.getText());
                if (existing == null) {
                    return new Paper(title.getText().trim(), authors.getText().trim(), y, content.getText());
                }
                Paper p = new Paper(existing.getId(), title.getText().trim(), authors.getText().trim(), y);
                return p;
            } catch (IllegalArgumentException ex) {
                Ui.message(this, ex.getMessage());   // keep the dialog open: loop again
            }
        }
    }

    private void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("papers.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getParentFile(), file.getName() + ".csv");
        }
        if (file.exists() && !Ui.confirm(this, file.getName() + " already exists. Replace it?")) {
            return;
        }
        try (Writer out = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
            out.write("﻿");   // BOM so Excel shows non-English letters correctly
            out.write("ID,Title,Authors,Year\r\n");
            for (Paper p : dao.findAll()) {
                out.write(p.getId() + "," + Ui.csv(p.getTitle()) + "," + Ui.csv(p.getAuthors()) + "," + p.getYear() + "\r\n");
            }
            Ui.message(this, "Saved: " + file.getAbsolutePath());
        } catch (IOException | SQLException e) {
            Ui.showError(this, e);
        }
    }
}
