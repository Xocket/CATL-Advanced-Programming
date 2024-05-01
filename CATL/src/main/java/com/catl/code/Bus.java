package com.catl.code;

import java.util.concurrent.ThreadLocalRandom;

public class Bus implements Runnable {

    private final String id;
    private final String airport;
    private int numPassengers;
    private final Log log;

    public Bus(String id, String airport, Log log) {
        this.id = "B-" + id;
        this.airport = airport;
        this.log = log;
    }

    @Override
    public void run() {
        while (true) {
            arriveDowntown();
            boardPassengers();
            goToAirport();
            arriveAirport();
            boardPassengers();
            goToDowntown();
        }
    }

    // The bus arrives at the downtown bus stop.
    private void arriveDowntown() {
        // Print in console.
        System.out.println(this.getID() + " arrived downtown.");
        // Log event.
        log.logEvent(this.getAirport(), this.getID() + " arrived downtown.");

        // The bus waits at the stop 2-5 seconds.
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 5001));
        } catch (InterruptedException e) {
            System.out.println("ERROR - " + this.getID() + " arriveDowntown()");
        }
    }

    // A random number of passengers between 0 and 50 get in the bus.
    private void boardPassengers() {
        // Using ThreadLocalRandom for optimization.
        this.numPassengers = ThreadLocalRandom.current().nextInt(0, 51);
        System.out.println(this.getID() + " picked up [" + this.getNumPassengers() + "] passengers.");
        log.logEvent(this.getAirport(), this.getID() + " picked up [" + this.getNumPassengers() + "] passengers.");
    }

    // The bus heads to the airport.
    private void goToAirport() {
        // Print in console.
        System.out.println(this.getID() + " heading to " + this.getAirport() + " airport.");
        // Log event.
        log.logEvent(this.getAirport(), this.getID() + " heading to " + this.getAirport() + " airport.");

        // The bus takes 5-10 seconds to get to the airport.
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(5000, 10001));
        } catch (InterruptedException e) {
            System.out.println("ERROR - " + this.getID() + " goToAirport()");
        }
    }

    // The bus arrives at the airport.
    private void arriveAirport() {
        // Print in console.
        System.out.println(this.getID() + " arrived to " + this.getAirport() + " airport.");
        // Log event.
        log.logEvent(this.getAirport(), this.getID() + " arrived to " + this.getAirport() + " airport.");

        // TODO: add passengers to the airport system.
        // The bus waits at the airport for passengers for 2-5 seconds.
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 5001));
        } catch (InterruptedException e) {
            System.out.println("ERROR - " + this.getID() + " arriveAirport()");
        }

        // TODO: substract passangers from the airport system. Probably use a lock or
        // some other kind of synchronization mechanism since the airport might have
        // fewer passangers than the ones randomly generated for the bus to take.
    }

    private void goToDowntown() {
        // Print in console.
        System.out.println(this.getID() + " heading downtown.");
        // Log event.
        log.logEvent(this.getAirport(), this.getID() + " heading downtown.");

        // The bus takes 5-10 seconds to get downtown.
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(5000, 10001));
        } catch (InterruptedException e) {
            System.out.println("ERROR - " + this.getID() + " goToDowntown()");
        }
    }

    public String getID() {
        return id;
    }

    public String getAirport() {
        return airport;
    }

    public int getNumPassengers() {
        return numPassengers;
    }

    public void setNumPassengers(int numPassengers) {
        this.numPassengers = numPassengers;
    }
}
