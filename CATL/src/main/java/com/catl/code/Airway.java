// Package declaration.
package com.catl.code;

// Importing classes.
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Airway {

    private List<Airplane> airplanesFlying = new ArrayList<>();
    private BlockingQueue<Airplane> readyAirplanesToLand = new LinkedBlockingQueue<>();
    private Lock lock = new ReentrantLock();

    private AtomicInteger accumulativeNumberAirplanes = new AtomicInteger(0);
    private AtomicInteger maxSize = new AtomicInteger(0);

    // Add an airplane to the Airway.
    public void addAirplane(Airplane airplane) {
        lock.lock();
        try {
            airplanesFlying.add(airplane);
        } finally {
            lock.unlock();
            accumulativeNumberAirplanes.incrementAndGet();
            maxSize.set(Math.max(maxSize.get(), airplanesFlying.size()));
        }
    }

    // Remove an airplane from the head of the queue and add it to the readyAirplanesToLand.
    public void makeAirplaneReady(Airplane airplane) {
        readyAirplanesToLand.add(airplane);
    }

    // Remove and returns an airplane from the head of the readyAirplanesToLand.
    public Airplane removeReadyAirplane() {
        try {
            Airplane airplane = readyAirplanesToLand.take();
            lock.lock();
            try {
                airplanesFlying.remove(airplane);
            } finally {
                lock.unlock();
            }
            return airplane;
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
        lock.lock();
        try {
            for (Airplane airplane : airplanesFlying) {
                sb.append(airplane.getID()).append(", ");
            }
        } finally {
            lock.unlock();
        }
        // Remove the last comma and space
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }

}
