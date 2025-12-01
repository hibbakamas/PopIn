package net.javaguids.popin.services;

public interface RegistrationServiceInterface {

    boolean registerUser(int eventId, int userId);

    boolean cancelRegistration(int eventId, int userId);

    boolean checkInUser(int eventId, int userId);

    boolean isEventFull(int eventId);

    boolean isUserRegistered(int eventId, int userId);
}