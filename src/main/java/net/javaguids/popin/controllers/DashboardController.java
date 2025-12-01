package net.javaguids.popin.controllers;

import net.javaguids.popin.models.User;

/**
 * Common contract for all dashboard controllers.
 * Demonstrates interface-based design and enforces a shared method signature.
 */
public interface DashboardController {
    void setLoggedInUser(User user);
}