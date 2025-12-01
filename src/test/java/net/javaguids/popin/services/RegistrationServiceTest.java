package net.javaguids.popin.services;

import net.javaguids.popin.database.Database;
import net.javaguids.popin.database.EventDAO;
import net.javaguids.popin.database.RegistrationDAO;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationServiceTest {

    private RegistrationService registrationService;
    private RegistrationDAO registrationDAO;
    private EventDAO eventDAO;
    private AuthService authService;
    private EventService eventService;

    @BeforeEach
    void setUp() throws Exception {
        registrationService = new RegistrationService();
        registrationDAO = new RegistrationDAO();
        eventDAO = new EventDAO();
        authService = new AuthService();
        eventService = new EventService();

        // Reset DB tables
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM registrations");
            st.executeUpdate("DELETE FROM events");
            st.executeUpdate("DELETE FROM users");
        }
    }

    private User createAttendee(String username) {
        authService.registerUser(username, "pw12345", "ATTENDEE");
        return authService.login(username, "pw12345").orElseThrow();
    }

    private User createOrganizer() {
        authService.registerUser("org", "pw", "ORGANIZER");
        return authService.login("org", "pw").orElseThrow();
    }

    private Event createEventWithCapacity(int capacity) {
        User org = createOrganizer();
        LocalDateTime dt = LocalDateTime.now().plusDays(1);
        eventService.createEvent(
                "Reg Test",
                "desc",
                dt,
                "Venue",
                capacity,
                org.getId(),
                null
        );
        return eventDAO.findAll().get(0);
    }

    @Test
    void canRegisterUserForEvent() {
        User attendee = createAttendee("alice");
        Event event = createEventWithCapacity(5);

        boolean ok = registrationService.registerUser(event.getId(), attendee.getId());
        assertTrue(ok);

        int count = registrationDAO.countRegistered(event.getId());
        assertEquals(1, count);
    }

    @Test
    void cannotDoubleRegisterSameUser() {
        User attendee = createAttendee("bob");
        Event event = createEventWithCapacity(5);

        // first registration succeeds
        registrationService.registerUser(event.getId(), attendee.getId());

        // second registration should throw IllegalStateException
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> registrationService.registerUser(event.getId(), attendee.getId())
        );

        assertEquals("You are already registered for this event.", ex.getMessage());
    }

    @Test
    void eventFullThrowsRuntimeException() {
        Event event = createEventWithCapacity(1);

        User u1 = createAttendee("u1");
        User u2 = createAttendee("u2");

        // First fills the event
        registrationService.registerUser(event.getId(), u1.getId());

        // Second should throw some runtime exception (e.g., EventFullException)
        assertThrows(RuntimeException.class,
                () -> registrationService.registerUser(event.getId(), u2.getId()));
    }

    @Test
    void registeringTwiceThrows() {
        // 1. Create event in DB so RegistrationService can find it
        Event event = new Event(
                "Test Event",
                "desc",
                LocalDateTime.now().plusDays(1),
                "Venue",
                10,
                1
        );
        eventDAO.createEvent(event); // <-- IMPORTANT

        int eventId = eventDAO.findAll().get(0).getId();
        int userId = 123;

        // 2. First registration should succeed
        registrationService.registerUser(eventId, userId);

        // 3. Second registration should throw
        assertThrows(IllegalStateException.class, () ->
                registrationService.registerUser(eventId, userId)
        );
    }





}