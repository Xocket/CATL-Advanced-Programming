// Package declaration.
package com.catl.code;

// The Program class contains the execution code for the program.
public class Program {

    // The run method contains the Program logic.
    public void run() {

        // Hangars creation.
        Hangar madridHangar = new Hangar();
        Hangar barcelonaHangar = new Hangar();

        // Maintenance Halls creation.
        MaintenanceHall madridMaintenanceHall = new MaintenanceHall(20);
        MaintenanceHall barcelonaMaintenanceHall = new MaintenanceHall(20);

        
    }
}
