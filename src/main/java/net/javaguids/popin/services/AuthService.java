package net.javaguids.popin.services;

import net.javaguids.popin.database.UserDAO;
import net.javaguids.popin.exceptions.InvalidCredentialsException;
import net.javaguids.popin.models.Role;
import net.javaguids.popin.models.User;
import net.javaguids.popin.utils.PasswordHasher;

import java.util.Optional;

public class AuthService implements AuthServiceInterface {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public Optional<User> login(String username, String plainPassword) {
        if (username == null || username.isBlank()
                || plainPassword == null || plainPassword.isBlank()) {
            throw new InvalidCredentialsException("Username and password cannot be empty.");
        }

        Optional<User> userOpt = userDAO.findByUsername(username);

        // user not found
        if (userOpt.isEmpty()) {
            throw new InvalidCredentialsException("Invalid username or password.");
        }

        User user = userOpt.get();

        if (!PasswordHasher.matchPassword(plainPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid username or password.");
        }

        return Optional.of(user);
    }

    @Override
    public boolean registerUser(String username, String plainPassword, String roleName) {
        if (username == null || username.isBlank()) {
            throw new InvalidCredentialsException("Username cannot be empty.");
        }
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new InvalidCredentialsException("Password cannot be empty.");
        }
        if (roleName == null || roleName.isBlank()) {
            throw new InvalidCredentialsException("Role is required.");
        }

        String normalizedRole = roleName.trim().toUpperCase();
        Role role = new Role(normalizedRole);
        String hash = PasswordHasher.hashPassword(plainPassword);

        User user = User.create(username, hash, role);

        return userDAO.createUser(user);
    }
}