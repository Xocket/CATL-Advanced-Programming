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
    public synchronized void addAirplane(Airplane airplane) {
        try {
            airplaneQueue.put(airplane);
            airplane.getCurrentAirport().getParkingArea().removeAirplane(airplane);

            accumulativeNumberAirplanes.incrementAndGet();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("ERROR - Adding airplane to Maintenance Hall.");
        }
    }

    // Method to remove an airplane from the maintenance hall.
    public void removeAirplane(Airplane airplane) {
        try {
            airplaneQueue.remove(airplane);
        } catch (Exception e) {
            System.out.println("ERROR - Removing airplane from Maintenance Hall.");
        }
    }

    // Get the number of Airplanes  added in total.
    public int getTotalAirplanes() {
        return accumulativeNumberAirplanes.get();
    }

    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        for (Airplane airplane : this.airplaneQueue) {
            sb.append(airplane.getID()).append(", ");
        }

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2);
        }

        return sb.toString();
    }

    public int getNumberAirplanes() {
        return airplaneQueue.size();
    }
}
