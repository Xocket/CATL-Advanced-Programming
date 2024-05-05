// Package declaration.
package com.catl.code;

// Importing classes.
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

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

    @Override
    public void run() {
        pauseControl.checkPaused();
        accessHangar();
        pauseControl.checkPaused();
        accessParkingArea();
        pauseControl.checkPaused();
        processBoarding();
        pauseControl.checkPaused();
        accessTaxiArea();
        pauseControl.checkPaused();
        accessRunway();
        pauseControl.checkPaused();
        takeOff();
        pauseControl.checkPaused();
        fly();
        pauseControl.checkPaused();
        land();
        pauseControl.checkPaused();
        accessTaxiArea();
        pauseControl.checkPaused();
        processDisembark();
        pauseControl.checkPaused();
        accessParkingArea();
        pauseControl.checkPaused();
        //getInspection();
        //pauseControl.checkPaused();

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
        }
    }

    private void accessHangar() {
        // Print in console.
        System.out.println("Airplane " + this.getID() + " with occupancy " + this.getOccupancy() + " entered the " + getCurrentAirport().getAirportName() + " Hangar.");
        // Log event.
        log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " with occupancy " + this.getOccupancy() + " entered the " + getCurrentAirport().getAirportName() + " Hangar.");

        if (isBorn) {
            // If the airplane is born, it accesses the hangar and skips the sleep.
            this.getCurrentAirport().getHangar().addAirplane(this);
        } else {
            // If the airplane is not born, there is a 50% chance it accesses the hangar and sleeps.
            if (Math.random() < 0.5) {
                this.getCurrentAirport().getHangar().addAirplane(this);
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(15000, 30001));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // After the sleep or skip, set isBorn to false.
            isBorn = false;
        }
    }

    private void accessParkingArea() {

        if (!landing) {
            this.getCurrentAirport().getParkingArea().addAirplane(this.getCurrentAirport().getHangar().removeAirplane(this));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            // Print in console.
            System.out.println("Airplane " + this.getID() + " entered the " + getCurrentAirport().getAirportName() + " Parking Area.");
            // Log event.
            log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " entered the " + getCurrentAirport().getAirportName() + " Parking Area.");

        } else {
            this.getCurrentAirport().getParkingArea().addAirplaneForInspection(this);
            // Print in console.
            System.out.println("Airplane " + this.getID() + " entered the " + getCurrentAirport().getAirportName() + " Parking Area and is performing checks.");
            // Log event.
            log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " entered the " + getCurrentAirport().getAirportName() + " Parking Area and is performing checks.");

            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));
            } catch (InterruptedException e) {

            }

            getInspection();
        }

    }

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

    private void accessTaxiArea() {
        currentAirport.getTaxiArea().addAirplane(this);
        System.out.println("Airplane " + getID() + " has entered the " + this.getCurrentAirport().getAirportName() + " airport Taxi Area.");

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
            this.processDisembark();
        }

    }

    public void accessRunway() {
        Semaphore semaphore = this.getCurrentAirport().getSemaphoreR();
        ReentrantLock[] locks = this.getCurrentAirport().getLocksR();

        try {
            // Acquire a permit from the semaphore.
            semaphore.acquire();
            currentAirport.getTaxiArea().getAirplaneList().remove(this);

            for (int i = 0; i < locks.length; i++) {
                // Try to acquire the lock for the current runway.
                if (locks[i].tryLock()) {
                    try {
                        // Set the airplane status.
                        this.getCurrentAirport().getRunway(i).setAirplaneStatus(this);

                        try {
                            System.out.println("Check up verifications.");
                            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 3001));
                            System.out.println("Taking off.");
                            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));

                        } catch (InterruptedException e) {
                        }
                        this.getCurrentAirport().getRunway(i).setAirplaneStatusNull();
                    } finally {
                        // Release the lock for the current runway.
                        locks[i].unlock();
                    }
                    // Break from the loop as the airplane has taken off.
                    break;
                }
            }
        } catch (InterruptedException e) {
        } finally {
            semaphore.release();
        }

    }

    public void takeOff() {
        System.out.println(this.getID() + " taking off towards " + this.getCurrentAirport().getAirportName() + " airport.");
        this.getCurrentAirport().getAirway().addAirplane(this);
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));
        } catch (InterruptedException e) {
        }
    }

    public void fly() {

        int flightDuration = ThreadLocalRandom.current().nextInt(15000, 30001);
        System.out.println(this.getID() + " flying for " + String.format("%.1f", (double) flightDuration / 1000) + " s.");
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

    public void getInspection() {
        boolean deepInspection = false;
        if (timesFlown % 15 == 0) {
            deepInspection = true;
        }

        try {
            // Add the airplane to the maintenance hall queue
            this.getCurrentAirport().getMaintenanceHall().addAirplane(this);

            // Only remove the airplane from the parking area if it gets past the .put() method
            this.getCurrentAirport().getParkingArea().removeAirplane(this);

            // Perform the inspection
            if (deepInspection) {
                // Deep inspection takes a random time between 5 and 10 seconds
                Thread.sleep((int) (Math.random() * ((10000 - 5000) + 1)) + 5000);
            } else {
                // Quick check takes a random time between 1 and 5 seconds
                Thread.sleep((int) (Math.random() * ((5000 - 1000) + 1)) + 1000);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

}
