package net.javaguids.popin.services;

import net.javaguids.popin.database.Database;
import net.javaguids.popin.database.UserDAO;
import net.javaguids.popin.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import net.javaguids.popin.exceptions.InvalidCredentialsException;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


import java.sql.Connection;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private AuthService authService;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws Exception {
        authService = new AuthService();
        userDAO = new UserDAO();

        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM registrations");
            st.executeUpdate("DELETE FROM events");
            st.executeUpdate("DELETE FROM users");
        }
    }

    @Test
    void registerUserCreatesUser() {
        boolean ok = authService.registerUser("alice", "securePw", "ATTENDEE");
        assertTrue(ok);

        Optional<User> u = userDAO.findByUsername("alice");
        assertTrue(u.isPresent());
        assertEquals("ATTENDEE", u.get().getRole().getName());
    }

    @Test
    void loginSucceedsWithCorrectCredentials() {
        authService.registerUser("bob", "pw12345", "ATTENDEE");

        Optional<User> loggedIn = authService.login("bob", "pw12345");
        assertTrue(loggedIn.isPresent());
        assertEquals("bob", loggedIn.get().getUsername());
    }

    @Test
    void loginFailsWithWrongPassword() {
        assertThrows(InvalidCredentialsException.class, () -> {
            authService.login("correctUser", "wrongPassword");
        });
    }

    @Test
    void registerUserFailsWithBlankUsername() {
        assertThrows(InvalidCredentialsException.class, () ->
                authService.registerUser("", "pw123", "ATTENDEE")
        );
    }

    @Test
    void loginFailsForNonExistingUser() {
        assertThrows(InvalidCredentialsException.class, () ->
                authService.login("no_such_user", "pw123")
        );
    }



}