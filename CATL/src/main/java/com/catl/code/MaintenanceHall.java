// Package declaration.
package com.catl.code;

// Importing classes.
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class MaintenanceHall {

    private BlockingQueue<Airplane> airplaneQueue;
    private AtomicInteger accumulativeNumberAirplanes = new AtomicInteger(0);

    // Constructor.
    public MaintenanceHall(int capacity) {
        this.airplaneQueue = new LinkedBlockingQueue<>(capacity);
    }

    // Method to add an airplane to the maintenance hall.
    public void addAirplane(Airplane airplane) {
        try {
            airplaneQueue.put(airplane);
            accumulativeNumberAirplanes.incrementAndGet();
        } catch (InterruptedException e) {
            System.out.println("ERROR - Adding airplane to Maintenance Hall.");
        }
    }

    // Method to remove an airplane from the maintenance hall.
    public Airplane removeAirplane() {
        try {
            return airplaneQueue.take();
        } catch (InterruptedException e) {
            System.out.println("ERROR - Removing airplane from Maintenance Hall.");
            return null;
        }
    }

    // Get the number of Airplanes  added in total.
    public int getTotalAirplanes() {
        return accumulativeNumberAirplanes.get();
    }

}
