package com.researchmate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.sql.SQLException;
import java.time.Year;
import java.util.concurrent.ExecutionException;

/** Tab 2: choose a PDF, read its text (in a background thread) and save it as a paper. */
public class ImportPanel extends JPanel {

    private final PaperDao dao;
    private final JTextField title = new JTextField(30);
    private final JTextField authors = new JTextField(30);
    private final JTextField year = new JTextField(String.valueOf(Year.now().getValue()), 6);
    private final JTextArea text = new JTextArea();
    private final JLabel status = new JLabel("Choose a PDF file to start.");
    private final JButton choose = new JButton("Choose PDF...");
    private final JButton save = new JButton("Save paper");

    public ImportPanel(PaperDao dao) {
        this.dao = dao;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(choose);
        top.add(status);
        add(top, BorderLayout.NORTH);

        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        add(new JScrollPane(text), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(new JLabel("Title:"));
        bottom.add(title);
        bottom.add(new JLabel("Authors:"));
        bottom.add(authors);
        bottom.add(new JLabel("Year:"));
        bottom.add(year);
        bottom.add(save);
        add(bottom, BorderLayout.SOUTH);

        save.setEnabled(false);
        choose.addActionListener(e -> choosePdf());
        save.addActionListener(e -> savePaper());
    }

    private void choosePdf() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("PDF files", "pdf"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        choose.setEnabled(false);
        save.setEnabled(false);
        status.setText("Reading " + file.getName() + " ...");

        // MULTITHREADING: reading a big PDF is slow, so it runs in the background and the window stays alive.
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return PdfReader.readText(file);
            }

            @Override
            protected void done() {
                choose.setEnabled(true);
                try {
                    String result = get();
                    text.setText(result);
                    text.setCaretPosition(0);
                    String name = file.getName();
                    title.setText(name.substring(0, name.length() - 4).replace('_', ' ').replace('-', ' '));
                    status.setText("Done: " + result.length() + " characters. Fill the fields and press Save.");
                    save.setEnabled(true);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    status.setText("Could not read the PDF.");
                    Ui.showError(ImportPanel.this, (Exception) ex.getCause());
                }
            }
        }.execute();
    }

    private void savePaper() {
        try {
            int y = Ui.checkPaper(title.getText(), authors.getText(), year.getText());
            dao.add(new Paper(title.getText().trim(), authors.getText().trim(), y, text.getText()));
            Ui.message(this, "Paper saved. You can see it in \"My Papers\".");
            text.setText("");
            title.setText("");
            authors.setText("");
            save.setEnabled(false);
            status.setText("Choose a PDF file to start.");
        } catch (IllegalArgumentException e) {
            Ui.message(this, e.getMessage());
        } catch (SQLException e) {
            Ui.showError(this, e);
        }
    }
}
