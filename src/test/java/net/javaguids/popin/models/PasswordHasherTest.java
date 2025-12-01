package net.javaguids.popin.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PasswordHasher core functionality.
 */
class PasswordHasherTest {

    @Test
    void hashPasswordProducesNonPlainText() {
        String raw = "secret123";
        String hash = PasswordHasher.hashPassword(raw);

        assertNotNull(hash);
        assertNotEquals(raw, hash, "Hash should not equal plain text password");
    }

    @Test
    void matchPasswordAcceptsCorrectPassword() {
        String raw = "secret123";
        String hash = PasswordHasher.hashPassword(raw);

        assertTrue(PasswordHasher.matchPassword(raw, hash));
    }

    @Test
    void matchPasswordRejectsWrongPassword() {
        String raw = "secret123";
        String hash = PasswordHasher.hashPassword(raw);

        assertFalse(PasswordHasher.matchPassword("wrong-pass", hash));
    }
}
