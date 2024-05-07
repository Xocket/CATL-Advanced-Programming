// Package declaration.
package com.catl.code;

// Importing classes.
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ParkingArea {

    private ArrayList<Airplane> airplaneList = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();

    private BlockingQueue<Airplane> airplaneQueue = new LinkedBlockingQueue<>();

    private AtomicInteger accumulativeNumberAirplanes = new AtomicInteger(0);
    private AtomicInteger maxSize = new AtomicInteger(0);

    // Add an airplane to the Parking Area.
    public void addAirplane(Airplane airplane) {

        try {
            airplaneQueue.put(airplane);
            lock.lock();
            try {
                airplaneList.add(airplane);
            } finally {
                lock.unlock();
            }
            accumulativeNumberAirplanes.incrementAndGet();
            maxSize.set(Math.max(maxSize.get(), airplaneQueue.size()));
        } catch (InterruptedException e) {
            System.out.println("ERROR - Adding airplane to Parking Area.");
        }
    }

    public void addAirplaneForInspection(Airplane airplane) {
        if (airplane == null) {
            System.out.println("ERROR - Attempted to add a null airplane to Parking Area.");
            return;
        }
        lock.lock();
        try {
            airplaneList.add(airplane);
        } finally {
            lock.unlock();
        }
    }

    public Airplane removeAirplane(Airplane airplane) {
        lock.lock();
        try {
            int index = airplaneList.indexOf(airplane);
            if (index != -1) {
                return airplaneList.remove(index);
            } else {
                return null;
            }
        } finally {
            lock.unlock();
        }
    }

    // Remove and returns an airplane from the head of the queue.
    public Airplane removeAirplaneForInspection() {
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
        lock.lock();
        try {
            for (Airplane airplane : this.getAirplaneList()) {
                sb.append(airplane.getID()).append(", ");
            }
        } finally {
            lock.unlock();
        }

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2);
        }

        return sb.toString();
    }

    public BlockingQueue<Airplane> getAirplaneQueue() {
        return airplaneQueue;
    }

    // Returns true if the object that calls it is at the head of the list.
    public boolean isAtHead(Airplane airplane) {
        return airplaneQueue.peek().equals(airplane);
    }

    public ArrayList<Airplane> getAirplaneList() {
        return airplaneList;
    }

    public int getNumberAirplanes() {
        int numAirplanes;
        lock.lock();
        try {
            numAirplanes = this.getAirplaneList().size();
        } finally {
            lock.unlock();
        }
        return numAirplanes;
    }
}
