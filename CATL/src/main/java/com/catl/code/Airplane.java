// Package declaration.
package com.catl.code;

// Importing classes.
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

// Airplane class implemented as a thread. Models the airplane lifecycle behavior.
public class Airplane implements Runnable {

    private final PauseControl pauseControl;

    private String id;
    private Log log;

    private int capacity;
    private int numPassengers;

    private Airport currentAirport;
    private Airport destinationAirport;

    private int embarkDisembark;
    private int takeOffLand;
    private boolean landing;
    private int timesFlown;

    private boolean isBorn;

    public boolean isNotified() {
        return isNotified;
    }

    public void setIsNotified(boolean isNotified) {
        this.isNotified = isNotified;
    }

    private volatile boolean isNotified = false;

    private BoardingGate bg;

    public void setBg(BoardingGate bg) {
        this.bg = bg;
    }

    private Runway rw;

    public void setRunway(Runway rw) {
        this.rw = rw;
    }

    public Airplane(String id, Airport currentAirport, Airport destinationAirport, Log log, PauseControl pauseControl) {
        this.id = this.getRandomLetters() + "-" + id;

        this.currentAirport = currentAirport;
        this.destinationAirport = destinationAirport;

        this.log = log;
        this.capacity = ThreadLocalRandom.current().nextInt(100, 301);
        this.bg = null;
        this.numPassengers = 0;
        this.pauseControl = pauseControl;
        this.embarkDisembark = 0;
        this.takeOffLand = 0;
        this.landing = false;
        this.isBorn = true;
        this.timesFlown = 0;
    }

    public void runAction(Runnable action) {
        action.run();
        pauseControl.checkPaused();
    }

    @Override
    public void run() {
        while (true) {
            runAction(this::accessHangar);
            runAction(this::accessParkingArea);
            runAction(this::processBoarding);
            runAction(this::accessTaxiArea);
            runAction(this::accessRunway);
            runAction(this::takeOff);
            runAction(this::fly);
            runAction(this::land);
            runAction(this::processDisembark);
            runAction(this::accessParkingArea);
            runAction(this::getInspection);
            runAction(this::preconfigurationFlight);
        }

    }

    private void accessHangar() {

        if (!isBorn) {
            int decision = ThreadLocalRandom.current().nextInt(2); // Generates a random number 0 or 1
            // If the airplane is not born, there is a 50% chance it accesses the hangar and sleeps.
            if (decision == 0) {
                this.getCurrentAirport().getHangar().addAirplane(this);
                // Print in console.
                System.out.println("Airplane " + this.getID() + " with occupancy " + this.getOccupancy() + " entered the " + getCurrentAirport().getAirportName() + " Hangar to sleep.");
                // Log event.
                log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " with occupancy " + this.getOccupancy() + " entered the " + getCurrentAirport().getAirportName() + " Hangar to sleep.");
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(15000, 30001));
                } catch (InterruptedException e) {

                }
            }

        } else {
            // If the airplane is born, it accesses the hangar and skips the sleep.
            this.getCurrentAirport().getHangar().addAirplane(this);
            // Print in console.
            System.out.println("Airplane " + this.getID() + " with occupancy " + this.getOccupancy() + " entered the " + getCurrentAirport().getAirportName() + " Hangar.");
            // Log event.
            log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " with occupancy " + this.getOccupancy() + " entered the " + getCurrentAirport().getAirportName() + " Hangar.");
            isBorn = false;
        }
    }

    private void accessParkingArea() {

        if (!landing) {
            this.getCurrentAirport().getHangar().removeAirplane(this);
            this.getCurrentAirport().getParkingArea().addAirplane(this);
            // Print in console.
            System.out.println("Airplane " + this.getID() + " entered the " + getCurrentAirport().getAirportName() + " Parking Area.");
            // Log event.
            log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " entered the " + getCurrentAirport().getAirportName() + " Parking Area.");

        } else {
            // Print in console.
            this.getCurrentAirport().getParkingArea().addAirplaneForInspection(this);

            System.out.println("Airplane " + this.getID() + " entered the " + getCurrentAirport().getAirportName() + " Parking Area and is performing checks.");
            // Log event.
            log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " entered the " + getCurrentAirport().getAirportName() + " Parking Area and is performing checks.");

            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));
            } catch (Exception e) {
            }
        }

    }

    // TODO: RACE CONDITION.
    public void processBoarding() {
        boolean hasBoarded = false;
        BlockingQueue queue = this.getCurrentAirport().getParkingArea().getAirplaneQueue();
        System.out.println(this.getOccupancyID() + queue);
        try {
            synchronized (queue) {
                while (!this.equals(queue.peek()) || !this.getCurrentAirport().gateIsAvailableExceptLast()) {
                    queue.wait();
                }
            }

            System.out.println("Airplane " + this.getID() + " looking to board.");
            log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " looking to board.");

            // Proceed with boarding
            ReentrantLock[] locks = this.getCurrentAirport().getLocksBG();
            for (int i = 0; i < locks.length - 1; i++) {
                if (locks[i].tryLock()) {
                    try {

                        // TODO: INVERT
                        this.getCurrentAirport().getBoardingGates(i).getIsAvailable().set(false);
                        this.getCurrentAirport().getParkingArea().getAirplaneList().remove(this);
                        this.getCurrentAirport().getBoardingGates(i).setAirplaneStatus(this);
                        queue.take();

                        synchronized (queue) {
                            queue.notifyAll();
                        }

                        log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " boarding passengers.");

                        for (int j = 0; j < 3; j++) {
                            // Wait if paused.
                            pauseControl.checkPaused();

                            this.addPassengers(this.getCurrentAirport().offloadPassengers(this.getCapacity() - this.getNumPassengers()));
                            this.getCurrentAirport().getBoardingGates(i).setAirplaneStatus(this);
                            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 3001));

                            if (this.getCapacity() == this.getNumPassengers()) {
                                break;
                            } else {
                                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));
                            }
                        }
                        hasBoarded = true;

                        this.getCurrentAirport().getBoardingGates(i).setAirplaneStatusNull();

                        // Notify all after acquiring the lock
                    } finally {
                        // Ensure the lock is released and the gate is available for others
                        locks[i].unlock();
                        this.getCurrentAirport().getBoardingGates(i).getIsAvailable().set(true);
                        synchronized (queue) {
                            queue.notifyAll();
                        }
                    }
                    break; // Exit the loop once a lock is acquired and processed
                }
            }
        } catch (InterruptedException e) {
        }
        log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " finished boarding passengers.");
        if (!hasBoarded) {
            System.out.println("THIS BOY HAS NOT BOARDED");
        }
    }

    /*
    private void processBoarding() {
        Semaphore semaphore = this.getCurrentAirport().getSemaphoreBG();
        ReentrantLock[] locks = this.getCurrentAirport().getLocksBG();
        BlockingQueue queue = this.getCurrentAirport().getParkingArea().getAirplaneQueue();

        try {
            synchronized (queue) {
                while (!queue.peek().equals(this)) {
                    queue.wait();
                }

                semaphore.acquire();
                System.out.println(this);
                queue.remove();
                this.getCurrentAirport().getParkingArea().getAirplaneList().remove(this);
                queue.notifyAll();
            }
        } catch (InterruptedException e) {
        }

        try {
            for (int i = 0; i < locks.length; i++) {
                // Try to acquire the lock for the current boarding gate.
                if (locks[i].tryLock()) {
                    try {
                        // Set the airplane status.
                        this.getCurrentAirport().getBoardingGates(i).setAirplaneStatus(this);

                        for (int j = 0; j < 3; j++) {
                            // Wait if paused.
                            pauseControl.checkPaused();

                            this.addPassengers(this.getCurrentAirport().offloadPassengers(this.getCapacity() - this.getNumPassengers()));
                            this.getCurrentAirport().getBoardingGates(i).setAirplaneStatus(this);
                            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 3001));

                            if (this.getCapacity() == this.getNumPassengers()) {
                                break;
                            } else {
                                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));
                            }
                        }

                        this.getCurrentAirport().getBoardingGates(i).setAirplaneStatusNull();
                    } finally {
                        // Release the lock for the current boarding gate.
                        locks[i].unlock();
                    }
                    // Break the loop as the airplane as found a boarding gate.
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // Release a permit back to the semaphore.
            semaphore.release();
        }

    }
     */
    private void accessTaxiArea() {
        currentAirport.getTaxiArea().addAirplane(this);
        System.out.println("Airplane " + getID() + " has entered the " + this.getCurrentAirport().getAirportName() + " airport Taxi Area.");
        log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + getID() + " has entered the " + this.getCurrentAirport().getAirportName() + " airport Taxi Area.");

        if (!landing) {
            // Before requesting runway, pilots make checks for 1-5 seconds.
            int durationChecks = ThreadLocalRandom.current().nextInt(1000, 5001);
            System.out.println("Pilots of flight " + this.getOccupancyID() + " performing pre-flight checks for " + String.format("%.1f", (double) durationChecks / 1000) + " s.");
            try {
                Thread.sleep(durationChecks);
            } catch (InterruptedException e) {
                System.out.println("ERROR - Performing pre-flight checks.");
            }

        } else {
            System.out.println(getID() + "Requesting boarding gate.");
        }

    }

    // Method to access a runway
    public void accessRunway() {

        log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + getID() + " requesting runway to take off.");
        BlockingQueue<Runway> openRunways = this.getCurrentAirport().getOpenRunways(); // Get the queue of open runways
        AtomicBoolean[] runwayStatus = this.getCurrentAirport().getRunwayStatusArray(); // Get the array of runway statuses
        Runway runway = null;
        try {
            runway = openRunways.take(); // Take a runway from the queue

            this.getCurrentAirport().getTaxiArea().removeAirplane(this);
            this.getCurrentAirport().getRunway(runway.getRunwayNumber()).setAirplaneStatus(this);

            log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + getID() + " runway granted.");

            // Simulate pre-flight checks and takeoff
            System.out.println("Airplane " + this.getID() + ": Check up verifications.");
            log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + getID() + ": pre-flight verifications.");
            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 3001));
            System.out.println("Airplane " + this.getID() + ": Taking off.");
            log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + getID() + ": taking off.");
            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));

            this.getCurrentAirport().getRunway(runway.getRunwayNumber()).setAirplaneStatusNull();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // Add the runway back to the queue if it's still open
            if (runwayStatus[runway.getRunwayNumber()].get()) {
                openRunways.add(runway);
            }
        }
    }

    public void takeOff() {
        this.getCurrentAirport().getAirway().addAirplane(this);

        System.out.println(this.getID() + " taking off towards " + this.getCurrentAirport().getAirportName() + " airport.");
        log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " taking off towards " + this.getCurrentAirport().getAirportName() + " airport.");

        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));
        } catch (InterruptedException e) {
        }
    }

    public void fly() {

        int flightDuration = ThreadLocalRandom.current().nextInt(15000, 30001);
        System.out.println(this.getID() + " flying for " + String.format("%.1f", (double) flightDuration / 1000) + " s.");
        log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " flying for " + String.format("%.1f", (double) flightDuration / 1000) + " s.");

        try {
            Thread.sleep(flightDuration);
        } catch (InterruptedException e) {
        }

        this.swapAirports();
        embarkDisembark++;
        takeOffLand++;
        landing = true;
        timesFlown++;
    }

    public void land() {
        BlockingQueue<Runway> openRunways = this.getCurrentAirport().getOpenRunways(); // Get the queue of open runways
        AtomicBoolean[] runwayStatus = this.getCurrentAirport().getRunwayStatusArray(); // Get the array of runway statuses
        Runway runway = null;

        try {
            while (runway == null) {
                runway = openRunways.poll();
                if (runway == null) {
                    System.out.println("Waiting for an open runway...");
                    log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " waiting for an open runway...");

                    Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));
                } else {

                    log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " landing at " + this.getCurrentAirport().getAirportName() + " airport.");
                    this.getCurrentAirport().getRunway(runway.getRunwayNumber()).setAirplaneStatus(this);
                    this.getDestinationAirport().getAirway().removeAirplane(this);

                    Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));

                    this.getCurrentAirport().getRunway(runway.getRunwayNumber()).setAirplaneStatusNull();

                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("The landing process was interrupted");
        } finally {
            // Add the runway back to the queue if it's still open
            if (runway != null && runwayStatus[runway.getRunwayNumber()].get()) {
                openRunways.add(runway);
            }
        }

        this.getCurrentAirport().getTaxiArea().addAirplane(this);

        // Travel to the boarding gate.
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(3000, 5001));
        } catch (InterruptedException e) {
        }
    }

    /*
    public void land() {
        Semaphore semaphore = this.getCurrentAirport().getSemaphoreR();
        ReentrantLock[] locks = this.getCurrentAirport().getLocksR();

        while (true) {
            if (semaphore.tryAcquire()) {
                break;
            } else {
                // No permits available, sleep for 1-5 seconds
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        this.getDestinationAirport().getAirway().removeAirplane(this);

        for (int i = 0; i < locks.length; i++) {
            // Try to acquire the lock for the current runway.
            if (locks[i].tryLock()) {
                try {
                    this.getCurrentAirport().getRunway(i).setAirplaneStatus(this);

                    try {
                        System.out.println("LANDING");
                        Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));
                    } catch (InterruptedException e) {
                    }

                    takeOffLand++;
                    this.getCurrentAirport().getRunway(i).setAirplaneStatusNull();
                } finally {
                    locks[i].unlock();
                }
                semaphore.release();
                break;
            }
        }
    }
     */
    private synchronized void processDisembark() {
        boolean hasDisembarked = false;
        log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " looking to disembark...");
        try {
            while (!this.getCurrentAirport().gateIsAvailableExceptFirst()) {
                wait();
            }
            // Proceed with boarding
            ReentrantLock[] locks = this.getCurrentAirport().getLocksBG();
            for (int i = 1; i < locks.length; i++) {
                if (locks[i].tryLock()) {
                    try {
                        log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " disembarking...");
                        this.getCurrentAirport().getBoardingGates(i).setAirplaneStatus(this);
                        this.currentAirport.getTaxiArea().removeAirplane(this);

                        // Sleep for 3-5 seconds to simulate the airplane getting to the boarding gate.
                        Thread.sleep(ThreadLocalRandom.current().nextInt(3000, 5001));

                        // Disembark all passengers.
                        this.getCurrentAirport().addPassengers(this.getNumPassengers());
                        this.numPassengers = 0;
                        this.getCurrentAirport().getBoardingGates(i).setAirplaneStatus(this);

                        // Sleep for 1-5 seconds to simulate the transfer of all passengers to the airport.
                        Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));

                        this.getCurrentAirport().getBoardingGates(i).setAirplaneStatusNull();
                        hasDisembarked = true;

                        // Notify all after acquiring the lock
                    } finally {
                        // Ensure the lock is released and the gate is available for others
                        locks[i].unlock();
                        this.getCurrentAirport().getBoardingGates(i).getIsAvailable().set(true);
                        notifyAll();
                    }
                    break; // Exit the loop once a lock is acquired and processed
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!hasDisembarked) {
            System.out.println("THIS BOY HAS NOT DISEMBARKED");
        }
    }

    /*
    private void processDisembark() {
        Semaphore semaphore = this.getCurrentAirport().getSemaphoreBG();
        ReentrantLock[] locks = this.getCurrentAirport().getLocksBG();

        try {
            // Acquire a semaphore permit.
            semaphore.acquire();

            for (int i = 0; i < locks.length; i++) {
                // Try to acquire the lock for the current boarding gate.
                if (locks[i].tryLock()) {
                    try {
                        // Set the airplane status.
                        this.currentAirport.getTaxiArea().removeAirplane(this);
                        this.getCurrentAirport().getBoardingGates(i).setAirplaneStatus(this);

                        // Sleep for 3-5 seconds to simulate the airplane getting to the boarding gate.
                        Thread.sleep(ThreadLocalRandom.current().nextInt(3000, 5001));

                        // Disembark all passengers.
                        this.getCurrentAirport().addPassengers(this.getNumPassengers());
                        this.numPassengers = 0;
                        this.getCurrentAirport().getBoardingGates(i).setAirplaneStatus(this);

                        // Sleep for 1-5 seconds to simulate the transfer of all passengers to the airport.
                        Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));

                        this.getCurrentAirport().getBoardingGates(i).setAirplaneStatusNull();
                    } finally {
                        // Release the lock for the current boarding gate.
                        locks[i].unlock();
                    }
                    // Break the loop as the airplane has found a boarding gate.
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // Release a permit back to the semaphore.
            semaphore.release();
        }
    }

     */
    private void getInspection() {
        log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " ready for inspection.");
        boolean deepInspection = false;
        if (timesFlown % 15 == 0) {
            deepInspection = true;
        }

        try {
            // Add the airplane to the maintenance hall queue
            this.getCurrentAirport().getMaintenanceHall().addAirplane(this);

            // Perform the inspection
            if (deepInspection) {
                // Deep inspection takes a random time between 5 and 10 seconds
                log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " getting a deep inspection.");
                Thread.sleep(ThreadLocalRandom.current().nextInt(5000, 10001));
            } else {
                // Quick check takes a random time between 1 and 5 seconds
                log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " getting a small inspection.");
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));

            }

            this.getCurrentAirport().getMaintenanceHall().removeAirplane(this);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void preconfigurationFlight() {
        log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " is starting lifecycle again.");
        landing = false;
        embarkDisembark++;
        takeOffLand++;

    }

    private String getRandomLetters() {
        char letter1 = (char) (ThreadLocalRandom.current().nextInt(26) + 'A');
        char letter2 = (char) (ThreadLocalRandom.current().nextInt(26) + 'A');

        return letter1 + "" + letter2;
    }

    public String getID() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public Airport getCurrentAirport() {
        return currentAirport;
    }

    public Airport getDestinationAirport() {
        return destinationAirport;
    }

    public int getNumPassengers() {
        return numPassengers;
    }

    public void setNumPassengers(int numPassengers) {
        this.numPassengers = numPassengers;
    }

    public void addPassengers(int newPassengers) {
        this.numPassengers += newPassengers;
    }

    public String getOccupancy() {
        return "[" + this.getNumPassengers() + "/" + this.getCapacity() + "]";
    }

    public String getOccupancyID() {
        return this.getID() + " [" + this.getNumPassengers() + "/" + this.getCapacity() + "]";
    }

    public Log getLog() {
        return log;
    }

    public int getEmbarkDisembark() {
        return embarkDisembark;
    }

    public void setEmbarkDisembark(int embarkDisembark) {
        this.embarkDisembark = embarkDisembark;
    }

    @Override
    public String toString() {
        return "Airplane{" + "id=" + id + '}';
    }

    int getTakeOffLand() {
        return takeOffLand;
    }

    public void swapAirports() {
        Airport tmpAirport = this.getCurrentAirport();
        this.setCurrentAirport(this.getDestinationAirport());
        this.setDestinationAirport(tmpAirport);
    }

    public void setCurrentAirport(Airport currentAirport) {
        this.currentAirport = currentAirport;
    }

    public void setDestinationAirport(Airport destinationAirport) {
        this.destinationAirport = destinationAirport;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Airplane other = (Airplane) obj;
        return Objects.equals(this.id, other.id);
    }

}
