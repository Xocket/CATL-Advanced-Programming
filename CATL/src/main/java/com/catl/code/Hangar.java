// Package declaration.
package com.catl.code;

// Importing classes.
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Hangar {

    private ConcurrentLinkedQueue<Airplane> airplaneQueue = new ConcurrentLinkedQueue<>();

    private AtomicInteger accumulativeNumberAirplanes = new AtomicInteger(0);
    private AtomicInteger maxSize = new AtomicInteger(0);

    // Add an airplane to the Hangar.
    public void addAirplane(Airplane airplane) {
        airplaneQueue.add(airplane);
        accumulativeNumberAirplanes.incrementAndGet();
        maxSize.set(Math.max(maxSize.get(), airplaneQueue.size()));
    }

    // Remove and returns an airplane from the head of the queue.
    public Airplane removeAirplane() {
        return airplaneQueue.poll();
    }

    // Get the number of Airplanes  added in total.
    public int getTotalAirplanes() {
        return accumulativeNumberAirplanes.get();
    }

    // Get the maximum number of elements in the queue at once.
    public int getMaxSize() {
        return maxSize.get();
    }
}
