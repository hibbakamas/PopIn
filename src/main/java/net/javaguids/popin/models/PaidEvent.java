package net.javaguids.popin.models;

import java.time.LocalDateTime;

public class PaidEvent extends Event {

    private double price;

    // Constructor for NEW paid events (no id yet)
    public PaidEvent(String title,
                     String description,
                     LocalDateTime dateTime,
                     String venue,
                     int capacity,
                     int organizerId,
                     double price) {

        super(title, description, dateTime, venue, capacity, organizerId);
        this.price = price;
    }

    // Constructor for EXISTING paid events (loaded from DB)
    public PaidEvent(int id,
                     String title,
                     String description,
                     LocalDateTime dateTime,
                     String venue,
                     int capacity,
                     int organizerId,
                     double price) {

        super(id, title, description, dateTime, venue, capacity, organizerId);
        this.price = price;
    }

    // Getter and Setter
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}