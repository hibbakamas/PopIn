package net.javaguids.popin.exceptions;

public class EventFullException extends RuntimeException {
    public EventFullException(String message) {
        super(message);
    }

    public EventFullException(String message, Throwable cause) {
        super(message, cause);
    }
}