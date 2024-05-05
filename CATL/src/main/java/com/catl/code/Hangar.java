// Package declaration.
package com.catl.code;

// Importing classes.
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Hangar {

    private ArrayList<Airplane> airplaneList = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();

    private AtomicInteger accumulativeNumberAirplanes = new AtomicInteger(0);
    private AtomicInteger maxSize = new AtomicInteger(0);

    // Add an airplane to the Hangar.
    public void addAirplane(Airplane airplane) {
        lock.lock();
        try {
            airplaneList.add(airplane);
            accumulativeNumberAirplanes.incrementAndGet();
            maxSize.set(Math.max(maxSize.get(), airplaneList.size()));
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

    // Get the number of Airplanes added in total.
    public int getTotalAirplanes() {
        return accumulativeNumberAirplanes.get();
    }

    // Get the maximum number of elements in the list at once.
    public int getMaxSize() {
        return maxSize.get();
    }

    // Returns true if the object that calls it is at the head of the list.
    public boolean isAtHead(Airplane airplane) {
        lock.lock();
        try {
            return (!airplaneList.isEmpty() && airplaneList.get(0).equals(airplane));
        } finally {
            lock.unlock();
        }
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

        // Remove the last comma and space
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }

    public ArrayList<Airplane> getAirplaneList() {
        return airplaneList;
    }
}
