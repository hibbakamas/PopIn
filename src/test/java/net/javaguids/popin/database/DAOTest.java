package net.javaguids.popin.database;

import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.User;
import net.javaguids.popin.services.AuthService;
import net.javaguids.popin.services.EventService;
import net.javaguids.popin.services.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DAOTest {

    private UserDAO userDAO;
    private EventDAO eventDAO;
    private RegistrationDAO registrationDAO;

    private AuthService authService;
    private EventService eventService;
    private RegistrationService registrationService;

    @BeforeEach
    void setUp() throws Exception {
        userDAO = new UserDAO();
        eventDAO = new EventDAO();
        registrationDAO = new RegistrationDAO();

        authService = new AuthService();
        eventService = new EventService();
        registrationService = new RegistrationService();

        // crude reset of tables – adjust table names if needed
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM registrations");
            st.executeUpdate("DELETE FROM events");
            st.executeUpdate("DELETE FROM users");
        }
    }

    @Test
    void userDaoCanFindUserCreatedViaAuthService() {
        // create user through the real flow
        boolean registered = authService.registerUser("alice", "password123", "ATTENDEE");
        assertTrue(registered);

        Optional<User> found = userDAO.findByUsername("alice");
        assertTrue(found.isPresent(), "UserDAO should find user by username");

        User u = found.get();
        assertEquals("alice", u.getUsername());
        assertEquals("ATTENDEE", u.getRole().getName());
    }

    @Test
    void eventDaoCreatesAndListsEvents() {
        // create an organizer so organizerId is valid
        authService.registerUser("org", "pw", "ORGANIZER");
        User org = authService.login("org", "pw").orElseThrow();

        LocalDateTime dt = LocalDateTime.now().plusDays(1);

        boolean created = eventService.createEvent(
                "DAO Test Event",
                "desc",
                dt,
                "Somewhere",
                40,
                org.getId(),  // organizer id
                null          // free event
        );
        assertTrue(created);

        List<Event> all = eventDAO.findAll();
        assertEquals(1, all.size());
        assertEquals("DAO Test Event", all.get(0).getTitle());
    }

    @Test
    void registrationDaoCountsAndListsRegistrations() {
        // create attendee
        authService.registerUser("bob", "pw", "ATTENDEE");
        User bob = authService.login("bob", "pw").orElseThrow();

        // create organizer + event
        authService.registerUser("org", "pw", "ORGANIZER");
        User org = authService.login("org", "pw").orElseThrow();

        LocalDateTime dt = LocalDateTime.now().plusDays(2);
        eventService.createEvent(
                "Reg Test Event",
                "desc",
                dt,
                "Venue",
                10,
                org.getId(),
                null
        );
        Event e = eventDAO.findAll().get(0);

        // register via RegistrationService (uses RegistrationDAO underneath)
        registrationService.registerUser(e.getId(), bob.getId());

        int count = registrationDAO.countRegistered(e.getId());
        assertEquals(1, count, "RegistrationDAO.countRegistered should be 1");

        List<Event> userEvents = registrationDAO.findByUserId(bob.getId());
        assertEquals(1, userEvents.size(), "RegistrationDAO.findByUserId should return 1 event");
        assertEquals(e.getId(), userEvents.get(0).getId());
    }
}
