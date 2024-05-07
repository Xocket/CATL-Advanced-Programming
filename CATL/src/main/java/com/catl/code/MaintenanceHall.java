// Package declaration.
package com.catl.code;

// Importing classes.
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class MaintenanceHall {

    private BlockingQueue<Airplane> airplaneQueue;
    private AtomicInteger accumulativeNumberAirplanes = new AtomicInteger(0);

    private ReentrantLock lock = new ReentrantLock();

    // Constructor.
    public MaintenanceHall(int capacity) {
        this.airplaneQueue = new LinkedBlockingQueue<>(capacity);
    }

    // Method to add an airplane to the maintenance hall.
    public void addAirplane(Airplane airplane) {
        lock.lock();
        try {
            airplaneQueue.put(airplane);
            airplane.getCurrentAirport().getParkingArea().removeAirplane(airplane);

            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("ERROR - Adding airplane to Maintenance Hall.");
        } finally {
            lock.lock();
        }
    }

    // Method to remove an airplane from the maintenance hall.
    public void removeAirplane(Airplane airplane) {
        lock.lock();
        try {
            airplaneQueue.remove(airplane);
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("ERROR - Removing airplane from Maintenance Hall.");
        } finally {
            lock.unlock();
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
