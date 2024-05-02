// Package declaration.
package com.catl.code;

// Importing classes.
import java.util.concurrent.ThreadLocalRandom;

public class Airplane implements Runnable {

    private String id;
    private Log log;

    private int capacity;
    private int numPassengers;

    private Airport currentAirport;
    private Airport destinationAirport;

    public Airplane(String id, Airport currentAirport, Airport destinationAirport, Log log) {
        this.id = this.getRandomLetters() + "-" + id;

        this.currentAirport = currentAirport;
        this.destinationAirport = destinationAirport;

        this.log = log;
        this.capacity = ThreadLocalRandom.current().nextInt(100, 301);
    }

    @Override
    public void run() {
        while (true) {
            accessHangar();
            accessParkingArea();
            boardPassengers();
            //accessTaxiArea();

            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
                System.out.println("oh shit");
            }

            /*
            
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
            // Wait for a notification.
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("ERROR - Accessing Parking Area.");
            }
        }

        // Now the airplane is at the head of the queue, it can leave the hangar.
        this.getCurrentAirport().getHangar().removeAirplane();

        // Notify all waiting threads.
        notifyAll();

        // Print in console.
        System.out.println("Airplane " + this.getID() + " entered the " + getCurrentAirport().getAirportName() + " Parking Area.");
        // Log event.
        log.logEvent(this.getCurrentAirport().getAirportName(), "Airplane " + this.getID() + " entered the " + getCurrentAirport().getAirportName() + " Parking Area.");

        // Enters the Parking Area from the Hangar.
        this.getCurrentAirport().getParkingArea().addAirplane(this);
    }

    // TODO: comment this code properly.
    private void boardPassengers() {
        // Wait until a boarding gate accepts this airplane.
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("ERROR - Boarding passengers.");
            }
        }

        for (int i = 0; i < 3; i++) {
            this.setNumPassengers(this.getCurrentAirport().offloadPassengers(this.getCapacity()));
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

    public String getOccupancy() {
        return "[" + this.getNumPassengers() + "/" + this.getCapacity() + "]";
    }

    public Log getLog() {
        return log;
    }

    @Override
    public String toString() {
        return "Airplane{" + "id=" + id + '}';
    }
}
