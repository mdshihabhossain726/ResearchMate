package com.researchmate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PdfReaderTest {

    @TempDir
    Path dir;

    @Test
    void rejectsNonPdf() throws IOException {
        File f = Files.writeString(dir.resolve("a.txt"), "hello").toFile();
        assertThrows(IOException.class, () -> PdfReader.readText(f));
    }

    @Test
    void rejectsFakePdf() throws IOException {
        File f = Files.writeString(dir.resolve("fake.pdf"), "this is not a pdf").toFile();
        assertThrows(IOException.class, () -> PdfReader.readText(f));
    }
}
