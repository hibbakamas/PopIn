package net.javaguids.popin.models;

import java.time.LocalDateTime;

public class PaidEvent extends Event {

    private double price;

    public PaidEvent() {}

    public PaidEvent(String title, String description, LocalDateTime dateTime,
                     String venue, int capacity, int organizerId, double price) {
        super(title, description, dateTime, venue, capacity, organizerId);
        this.price = price;
    }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
