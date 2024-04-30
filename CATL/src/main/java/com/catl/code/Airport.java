// Package declaration.
package com.catl.code;

// Importing classes.
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Airport {
    // Each airport has one Hangar with unlimited capacity.
    private ConcurrentLinkedQueue<Airplane> hangar = new ConcurrentLinkedQueue<>();

    // Each airport has one MaintenanceHall with a capacity for 20 airplanes simultaneously.
    private ConcurrentLinkedQueue<Airplane> maintenanceHall = new ConcurrentLinkedQueue<>();

    // Each airport has 6 BoardingGates.
    private BoardingGate[] boardingGates = new BoardingGate[6];

    // Each airport has 4 Runways.
    private Runway[] runways = new Runway[4];

    // Each airport has one ParkingArea with unlimited capacity.
    private ConcurrentLinkedQueue<Airplane> parkingArea = new ConcurrentLinkedQueue<>();

    // Each airport has one TaxiArea with unlimited capacity.
    private ConcurrentLinkedQueue<Airplane> taxiArea = new ConcurrentLinkedQueue<>();

    // The system only has 2 airways connecting the two airports.
    private ConcurrentLinkedQueue<Airplane> airway = new ConcurrentLinkedQueue<>();

    // Locks for synchronization.
    private final ReentrantLock lock = new ReentrantLock();

    // Constructor
    public Airport() {
        for (int i = 0; i < 6; i++) {
            boardingGates[i] = new BoardingGate();
        }

        for (int i = 0; i < 4; i++) {
            runways[i] = new Runway();
        }
    }

    // Other methods would go here...
}
