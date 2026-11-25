package com.researchmate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UiTest {

    @Test
    void csvQuoting() {
        assertEquals("plain", Ui.csv("plain"));
        assertEquals("\"a,b\"", Ui.csv("a,b"));
        assertEquals("\"say \"\"hi\"\"\"", Ui.csv("say \"hi\""));
        assertEquals("", Ui.csv(null));
    }

    @Test
    void validPaper() {
        assertEquals(2021, Ui.checkPaper("Title", "Author", " 2021 "));
    }

    @Test
    void invalidPapers() {
        assertThrows(IllegalArgumentException.class, () -> Ui.checkPaper(" ", "A", "2021"));
        assertThrows(IllegalArgumentException.class, () -> Ui.checkPaper("T", "", "2021"));
        assertThrows(IllegalArgumentException.class, () -> Ui.checkPaper("T", "A", "abc"));
        assertThrows(IllegalArgumentException.class, () -> Ui.checkPaper("T", "A", "1800"));
        assertThrows(IllegalArgumentException.class, () -> Ui.checkPaper("T", "A", "3000"));
    }
}
