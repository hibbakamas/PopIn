module net.javaguids.popin {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;    // needed later for database

    // OPEN packages so FXML can access fields, controllers, etc.
    opens net.javaguids.popin to javafx.fxml;
    opens net.javaguids.popin.controllers to javafx.fxml;
    opens net.javaguids.popin.models to javafx.base;

    // EXPORT packages used by other modules / JavaFX loader
    exports net.javaguids.popin;
    exports net.javaguids.popin.controllers;
}