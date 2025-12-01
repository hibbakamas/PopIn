package net.javaguids.popin.services;

import net.javaguids.popin.database.UserDAO;
import net.javaguids.popin.models.Role;
import net.javaguids.popin.models.User;
import net.javaguids.popin.utils.PasswordHasher;

import java.util.Optional;

public class AuthService implements AuthServiceInterface {
    private final UserDAO userDAO = new UserDAO();

    /**
     * Attempt login with username and password.
     */
    public Optional<User> login(String username, String plainPassword) {
        if (username == null || username.isBlank() ||
                plainPassword == null || plainPassword.isBlank()) {
            return Optional.empty();
        }

        Optional<User> userOpt = userDAO.findByUsername(username);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();
        boolean valid = PasswordHasher.matchPassword(plainPassword, user.getPasswordHash());
        return valid ? Optional.of(user) : Optional.empty();
    }

    /**
     * Register a new user with the given role.
     */
    public boolean registerUser(String username, String plainPassword, String roleName) {
        if (username == null || username.isBlank()) return false;
        if (plainPassword == null || plainPassword.isBlank()) return false;
        if (roleName == null || roleName.isBlank()) return false;

        String normalizedRole = roleName.trim().toUpperCase();
        Role role = new Role(normalizedRole);
        String hash = PasswordHasher.hashPassword(plainPassword);

        // use factory → Admin / Organizer / Attendee instance
        User user = User.create(username, hash, role);

        return userDAO.createUser(user);
    }
}