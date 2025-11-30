package net.javaguids.popin.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class PasswordHasher {

    /**
     * Hash a password using SHA-256.
     * Main method you should call when storing passwords.
     */
    public static String hashPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Compare a plain text password with a stored hash.
     */
    public static boolean matchPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }
        String computed = hashPassword(plainPassword);
        return computed.equals(storedHash);
    }

    // --- Convenience aliases in case other code already uses these names ---

    public static String hash(String password) {
        return hashPassword(password);
    }

    public static boolean matches(String plainPassword, String storedHash) {
        return matchPassword(plainPassword, storedHash);
    }
}