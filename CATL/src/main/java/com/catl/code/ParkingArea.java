// Package declaration.
package com.catl.code;

// Importing classes.
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ParkingArea {

    private BlockingQueue<Airplane> airplaneQueue = new LinkedBlockingQueue<>();

    private AtomicInteger accumulativeNumberAirplanes = new AtomicInteger(0);
    private AtomicInteger maxSize = new AtomicInteger(0);

    // Add an airplane to the Parking Area.
    public void addAirplane(Airplane airplane) {
        try {
            airplaneQueue.put(airplane);
            accumulativeNumberAirplanes.incrementAndGet();
            maxSize.set(Math.max(maxSize.get(), airplaneQueue.size()));
        } catch (InterruptedException e) {
            System.out.println("ERROR - Adding airplane to Parking Area.");
        }

    }

    // Remove and returns an airplane from the head of the queue.
    public Airplane removeAirplane() {
        try {
            return airplaneQueue.take();
        } catch (InterruptedException e) {
            System.out.println("ERROR - Removing airplane from Parking Area.");
        }
        return null;
    }

    // Get the number of Airplanes  added in total.
    public int getTotalAirplanes() {
        return accumulativeNumberAirplanes.get();
    }

    // Get the maximum number of elements in the queue at once.
    public int getMaxSize() {
        return maxSize.get();
    }

    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        for (Airplane airplane : this.getAirplaneQueue()) {
            sb.append(airplane.getID()).append(", ");
        }
        return sb.toString();
    }

    public BlockingQueue<Airplane> getAirplaneQueue() {
        return airplaneQueue;
    }

}
