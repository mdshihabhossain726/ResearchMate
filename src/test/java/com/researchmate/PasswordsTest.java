package com.researchmate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordsTest {

    @Test
    void rightPasswordIsAcceptedAndWrongOneIsNot() {
        String stored = Passwords.hash("secret123");
        assertTrue(Passwords.verify("secret123", stored));
        assertFalse(Passwords.verify("secret124", stored));
    }

    @Test
    void samePasswordGivesDifferentHashes() {
        assertNotEquals(Passwords.hash("secret123"), Passwords.hash("secret123"));
    }

    @Test
    void brokenStoredValueIsRejected() {
        assertFalse(Passwords.verify("x", null));
        assertFalse(Passwords.verify("x", "no-colon"));
        assertFalse(Passwords.verify("x", "%%%:%%%"));
    }

    @Test
    void registrationChecks() {
        assertEquals("shihab", UserDao.check("Md Shihab", " Shihab ", "abcdef"));
        assertThrows(IllegalArgumentException.class, () -> UserDao.check(" ", "shihab", "abcdef"));
        assertThrows(IllegalArgumentException.class, () -> UserDao.check("A", "sh", "abcdef"));
        assertThrows(IllegalArgumentException.class, () -> UserDao.check("A", "bad name", "abcdef"));
        assertThrows(IllegalArgumentException.class, () -> UserDao.check("A", "shihab", "abc"));
    }
}
