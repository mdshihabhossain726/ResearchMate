package com.researchmate;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

/** Reads the text of a PDF file with Apache PDFBox. */
public class PdfReader {

    /**
     * @throws IOException with a readable message if the file is not a PDF, is damaged,
     *                     is password-protected, or has no text (for example a scanned image)
     */
    public static String readText(File file) throws IOException {
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            throw new IOException("Please choose a .pdf file.");
        }
        try (PDDocument document = Loader.loadPDF(file)) {
            String text = new PDFTextStripper().getText(document);
            text = text.replace("\u0000", "").trim();   // PostgreSQL cannot store the NUL character
            if (text.isEmpty()) {
                throw new IOException("No text found. The PDF may be a scanned image.");
            }
            return text;
        } catch (IOException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("No text found")) {
                throw e;
            }
            throw new IOException("This PDF cannot be read (damaged or password-protected): " + e.getMessage());
        }
    }
}
