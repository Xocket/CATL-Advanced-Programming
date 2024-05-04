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

public class TaxiArea {

    private List<Airplane> airplaneQueue = new ArrayList<>();
    private BlockingQueue<Airplane> readyAirplaneQueue = new LinkedBlockingQueue<>();
    private Lock lock = new ReentrantLock();

    private AtomicInteger accumulativeNumberAirplanes = new AtomicInteger(0);
    private AtomicInteger maxSize = new AtomicInteger(0);

    // Add an airplane to the Taxi Area.
    public void addAirplane(Airplane airplane) {
        lock.lock();
        try {
            airplaneQueue.add(airplane);
        } finally {
            lock.unlock();
            accumulativeNumberAirplanes.incrementAndGet();
            maxSize.set(Math.max(maxSize.get(), airplaneQueue.size()));
        }
    }

    // Remove an airplane from the head of the queue and add it to the readyAirplaneQueue.
    public void makeAirplaneReady(Airplane airplane) {
        lock.lock();
        try {
            airplaneQueue.remove(airplane);
            readyAirplaneQueue.add(airplane);
        } finally {
            lock.unlock();
        }
    }

    // Add an airplane to the readyAirplaneQueue.
    public void addReadyAirplane(Airplane airplane) {
        readyAirplaneQueue.add(airplane);
    }

    // Remove and returns an airplane from the head of the readyAirplaneQueue.
    public Airplane removeReadyAirplane() {
        try {
            return readyAirplaneQueue.take();
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

}
