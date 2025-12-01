package net.javaguids.popin.services;

import net.javaguids.popin.models.User;

import java.util.Optional;

public interface AuthServiceInterface {

    Optional<User> login(String username, String plainPassword);

    boolean registerUser(String username, String plainPassword, String roleName);
}