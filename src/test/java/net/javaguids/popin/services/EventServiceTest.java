package net.javaguids.popin.services;

import net.javaguids.popin.database.Database;
import net.javaguids.popin.database.EventDAO;
import net.javaguids.popin.database.UserDAO;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {

    private EventService eventService;
    private EventDAO eventDAO;
    private AuthService authService;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws Exception {
        eventService = new EventService();
        eventDAO = new EventDAO();
        authService = new AuthService();
        userDAO = new UserDAO();

        // clean DB between tests
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM registrations");
            st.executeUpdate("DELETE FROM events");
            st.executeUpdate("DELETE FROM users");
        }
    }

    private User createOrganizer() {
        authService.registerUser("org", "pw", "ORGANIZER");
        return authService.login("org", "pw").orElseThrow();
    }

    @Test
    void createEventPersistsToDatabase() {
        User org = createOrganizer();

        LocalDateTime dt = LocalDateTime.now().plusDays(1);
        boolean created = eventService.createEvent(
                "Service Test Event",
                "desc",
                dt,
                "Venue",
                20,
                org.getId(),
                null
        );

        assertTrue(created);

        List<Event> all = eventDAO.findAll();
        assertEquals(1, all.size());
        assertEquals("Service Test Event", all.get(0).getTitle());
    }

    @Test
    void updateEventChangesStoredValues() {
        User org = createOrganizer();

        LocalDateTime dt = LocalDateTime.now().plusDays(1);
        eventService.createEvent(
                "Old Title",
                "Old desc",
                dt,
                "Venue",
                10,
                org.getId(),
                null
        );

        List<Event> before = eventDAO.findAll();
        assertEquals(1, before.size());
        Event existing = before.get(0);

        existing.setTitle("New Title");
        existing.setDescription("New desc");

        boolean updated = eventService.updateEvent(existing, null);
        assertTrue(updated);

        // reload via findAll() instead of findById()
        List<Event> after = eventDAO.findAll();
        assertEquals(1, after.size());
        Event reloaded = after.get(0);

        assertEquals("New Title", reloaded.getTitle());
        assertEquals("New desc", reloaded.getDescription());
    }

    @Test
    void deleteEventRemovesFromDatabase() {
        User org = createOrganizer();

        LocalDateTime dt = LocalDateTime.now().plusDays(1);
        eventService.createEvent(
                "To Delete",
                "desc",
                dt,
                "Venue",
                10,
                org.getId(),
                null
        );

        List<Event> before = eventDAO.findAll();
        assertEquals(1, before.size());
        Event e = before.get(0);

        boolean deleted = eventService.deleteEvent(e.getId());
        assertTrue(deleted);

        // again, no findById → just check list is empty
        List<Event> after = eventDAO.findAll();
        assertTrue(after.isEmpty(), "Event list should be empty after deletion");
    }


    @Test
    void createFreeEventWorks() {
        boolean created = eventService.createEvent(
                "Title", "Desc",
                LocalDateTime.now(),
                "Venue", 20, 1,
                null  // free
        );
        assertTrue(created);
    }


    @Test
    void createPaidEventWorks() {
        boolean created = eventService.createEvent(
                "Concert", "Live",
                LocalDateTime.now(),
                "Hall", 50, 1,
                12.5
        );
        assertTrue(created);
    }


}