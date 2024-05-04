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

    public Airplane(String id, Airport currentAirport, Airport destinationAirport, Log log, PauseControl pauseControl) {
        this.id = this.getRandomLetters() + "-" + id;

        this.currentAirport = currentAirport;
        this.destinationAirport = destinationAirport;

        this.log = log;
        this.capacity = ThreadLocalRandom.current().nextInt(100, 301);
        this.bg = null;
        this.numPassengers = 0;
        this.pauseControl = pauseControl;
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

        try {
            Thread.sleep(100000000);
        } catch (InterruptedException e) {
        }


        /*
            accessTaxiArea();
            takeOff();
            fly();
            land();
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

        System.out.println(embarkDisembark++);

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

        // Notify the boarding gate that it has finished boarding passengers.
        synchronized (this) {
            notify();
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
}
