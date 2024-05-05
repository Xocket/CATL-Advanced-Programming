// Package declaration.
package com.catl.code;

// Importing classes.
import java.util.concurrent.ThreadLocalRandom;

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
    }

    @Override
    public void run() {
        pauseControl.checkPaused();
        accessHangar();
        pauseControl.checkPaused();
        accessParkingArea();
        pauseControl.checkPaused();
        boardPassengers();
        pauseControl.checkPaused();
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

        try {
            Thread.sleep(100000000);
        } catch (InterruptedException e) {
        }


        /*
            debarkPassengers();
            inspect();
            rest();

            // Swaps current and destination airports.
            flightConfiguration();
         */
    }

    // Airplane accesses Airport Hangar.
    private void accessHangar() {
        // Print in console.
        System.out.println("Airplane " + this.getID() + " with occupancy " + this.getOccupancy() + " entered the " + getCurrentAirport().getAirportName() + " Hangar.");
        // Log event.
        log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " with occupancy " + this.getOccupancy() + " entered the " + getCurrentAirport().getAirportName() + " Hangar.");

        // Enters the airport's hangar.
        this.getCurrentAirport().getHangar().addAirplane(this);
    }

    private synchronized void accessParkingArea() {
        // Wait until this airplane is at the head of the hangar queue.
        while (!this.getCurrentAirport().getHangar().isAtHead(this)) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("ERROR - Accessing Parking Area.");
            }
        }

        // Print in console.
        System.out.println("Airplane " + this.getID() + " entered the " + getCurrentAirport().getAirportName() + " Parking Area.");
        // Log event.
        log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " entered the " + getCurrentAirport().getAirportName() + " Parking Area.");

        // Now the airplane is at the head of the queue, it can leave the Hangar and enter the Parking Area.
        this.getCurrentAirport().getParkingArea().addAirplane(this.getCurrentAirport().getHangar().removeAirplane());
        // Notify all waiting threads.
        notifyAll();
    }

    // TODO: comment this code properly.
    private void boardPassengers() {

        // Wait until a boarding gate accepts this airplane.
        synchronized (this) {
            while (!isNotified) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.out.println("ERROR - Boarding passengers.");
                }
            }
            isNotified = false;
        }

        bg.setAirplaneStatus(this);

        for (int i = 0; i < 3; i++) {

            // Wait if paused.
            pauseControl.checkPaused();

            this.addPassengers(this.getCurrentAirport().offloadPassengers(this.getCapacity() - this.getNumPassengers()));
            bg.setAirplaneStatus(this);
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 3001));
            } catch (InterruptedException e) {
                System.out.println("ERROR - Boarding passengers attempts boardPassengers()");
            }
            if (this.getCapacity() == this.getNumPassengers()) {
                break;
            } else {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));
                } catch (InterruptedException e) {
                    System.out.println("ERROR - Waiting for passengers boardPassengers()");
                }
            }
        }

        embarkDisembark++;

        // Notify the boarding gate that it has finished boarding passengers.
        synchronized (this) {
            notify();
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
        // Wait until a runway this airplane.
        synchronized (this) {
            while (!isNotified) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.out.println("ERROR - Boarding passengers.");
                }
            }
            isNotified = false;
        }

        rw.setAirplaneStatus(this);

        try {
            System.out.println("Check up verifications.");
            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 3001));
            System.out.println("Taking off");
            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));
        } catch (InterruptedException e) {
        }

        takeOffLand++;
        // Notify the boarding gate that it has finished boarding passengers.
        synchronized (this) {
            notify();
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
