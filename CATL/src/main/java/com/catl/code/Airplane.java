// Package declaration.
package com.catl.code;

// Importing classes.
import java.time.Duration;
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
        this.isBorn = true;
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

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
        }

        /*
        accessTaxiArea();
        pauseControl.checkPaused();
        accessRunway();
        pauseControl.checkPaused();
        takeOff();
        pauseControl.checkPaused();
        fly();
        pauseControl.checkPaused();
        requestLanding();
        pauseControl.checkPaused();
         */
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
        // Print in console.
        System.out.println("Airplane " + this.getID() + " entered the " + getCurrentAirport().getAirportName() + " Parking Area.");
        // Log event.
        log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " entered the " + getCurrentAirport().getAirportName() + " Parking Area.");

        this.getCurrentAirport().getParkingArea().addAirplane(this.getCurrentAirport().getHangar().removeAirplane(this));
        System.out.println();
    }

    private void processBoarding() {
        Semaphore semaphore = this.getCurrentAirport().getSemaphoreBG();
        ReentrantLock[] locks = this.getCurrentAirport().getLocksBG();
        BlockingQueue queue = this.getCurrentAirport().getParkingArea().getAirplaneQueue();
        Airplane checker = null;

        boolean active = true;
        try {
            synchronized (queue) {
                while (!queue.peek().equals(this)) {
                    queue.wait();
                }

                semaphore.acquire();
                System.out.println(this);
                queue.remove();
                queue.notifyAll();
                active = false;
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

                        embarkDisembark++;
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

        // Before requesting runway, pilots make checks for 1-5 seconds.
        int durationChecks = ThreadLocalRandom.current().nextInt(1000, 5001);
        System.out.println("Pilots of flight " + this.getOccupancyID() + " performing pre-flight checks for " + String.format("%.1f", (double) durationChecks / 1000) + " s.");

        try {
            Thread.sleep(durationChecks);
        } catch (InterruptedException e) {
            System.out.println("ERROR - Performing pre-flight checks.");
        }

        currentAirport.getTaxiArea().makeAirplaneReady(this);
    }

    public void accessRunway() {
        Semaphore semaphore = this.getCurrentAirport().getSemaphoreR();
        ReentrantLock[] locks = this.getCurrentAirport().getLocksR();

        try {
            // Acquire a permit from the semaphore.
            semaphore.acquire();

            for (int i = 0; i < locks.length; i++) {
                // Try to acquire the lock for the current runway.
                if (locks[i].tryLock()) {
                    try {
                        // Set the airplane status.
                        this.getCurrentAirport().getRunway(i).setAirplane(this);

                        try {
                            System.out.println("Check up verifications.");
                            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 3001));
                            System.out.println("Taking off.");
                            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));

                        } catch (InterruptedException e) {
                        }
                        takeOffLand++;
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

        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));
        } catch (InterruptedException e) {
        }
        this.getCurrentAirport().getAirway().addAirplane(this);
    }

    public void fly() {

        int flightDuration = ThreadLocalRandom.current().nextInt(15000, 30001);
        System.out.println(this.getID() + " flying for " + String.format("%.1f", (double) flightDuration / 1000) + " s.");
        try {
            Thread.sleep(flightDuration);
        } catch (InterruptedException e) {
        }
    }

    /*
    public void requestLanding() {
        this.getCurrentAirport().getAirway().makeAirplaneReady(this);
    }
     */
    public void requestLanding() {
        // Wait until a runway this airplane.
        int a = 0;
        a++;
        synchronized (this) {
            while (!isNotified) {
                a++;
                try {

                    wait();
                } catch (InterruptedException e) {
                    System.out.println("ERROR - Boarding passengers.");
                }
            }

            isNotified = false;

            rw.setAirplaneStatus(this);

            try {
                System.out.println("TRYING TO LAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAND");
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 3001));
                System.out.println("TRYING TO LAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAND");
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));
            } catch (InterruptedException e) {
            }

            takeOffLand++;

            // Notify the boarding gate that it has finished boarding passengers.
            synchronized (this) {
                notify();
            }
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

}
