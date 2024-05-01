// Package declaration.
package com.catl.code;

public class Airport {

    private BoardingGate[] boardingGates = new BoardingGate[6];
    private Hangar hangar = new Hangar();
    private MaintenanceHall maintenanceHall = new MaintenanceHall(20);
    private ParkingArea parkingArea = new ParkingArea();
    private TaxiArea taxiArea = new TaxiArea();

    public Airport() {

        // Initialize each Boarding Gate in the boardingGates array.
        for (int i = 0; i < 6; i++) {
            this.boardingGates[i] = new BoardingGate();
        }
    }
}
