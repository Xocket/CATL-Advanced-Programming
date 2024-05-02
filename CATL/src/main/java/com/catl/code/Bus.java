// Package declaration.
package com.catl.code;

// Importing classes.
import java.util.concurrent.ThreadLocalRandom;

// Bus class modeled as a thread.
public class Bus implements Runnable {

    // Variable definition.
    private final String id;            // Bus ID with format "B-XXXX".
    private final String airportName;   // Name of the airport it belongs to.
    private final Airport airport;      // Airport it belongs to.
    private int numPassengers;          // Number of passengers it's carrying.
    private final Log log;              // Log object to log events.

    // Bus class constructor.
    public Bus(String id, String airportName, Airport airport, Log log) {
        this.id = "B-" + id;
        this.airportName = airportName;
        this.airport = airport;
        this.numPassengers = 0;
        this.log = log;
    }

    // Run method of the Bus class.
    @Override
    public void run() {
        // Continuous lifetime cycle of every bus.
        while (true) {
            arriveDowntown();
            boardPassengersDowntown();
            goToAirport();
            arriveAirport();
            boardPassengersAirport();
            goToDowntown();
        }
    }

    // The bus arrives at the downtown bus stop.
    private void arriveDowntown() {
        // Print in console.
        //BRUH System.out.println("Bus " + this.getID() + " arrived downtown.");
        // Log event.
        //BRUH log.logEvent(this.getAirportName(), "Bus " + this.getID() + " arrived downtown.");

        // The bus waits at the stop 2-5 seconds.
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 5001));
        } catch (InterruptedException e) {
            System.out.println("ERROR - " + this.getID() + " arriveDowntown()");
        }
    }

    // // Passengers board the bus in downtown.
    private void boardPassengersDowntown() {
        // A random number of 0-50 passengers board the bus.
        this.setNumPassengers(ThreadLocalRandom.current().nextInt(0, 51));

        // Print in console.
        //BRUH System.out.println("Bus " + this.getID() + " picked up [" + this.getNumPassengers() + "] passengers downtown.");
        // Log event.
        //BRUH log.logEvent(this.getAirportName(), "Bus " + this.getID() + " picked up [" + this.getNumPassengers() + "] passengers downtown.");
    }

    // The bus heads to the airport.
    private void goToAirport() {
        // Print in console.
        //BRUH System.out.println("Bus " + this.getID() + " heading to " + this.getAirportName() + " airport.");
        // Log event.
        //BRUH log.logEvent(this.getAirportName(), "Bus " + this.getID() + " heading to " + this.getAirportName() + " airport.");

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
        //BRUH System.out.println("Bus " + this.getID() + " arrived to " + this.getAirportName() + " airport.");
        // Log event.
        //BRUH log.logEvent(this.getAirportName(), "Bus " + this.getID() + " arrived to " + this.getAirportName() + " airport.");

        // Passengers transfer to the airport.
        this.airport.addPassengers(numPassengers);

        // The bus waits at the airport for passengers for 2-5 seconds.
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 5001));
        } catch (InterruptedException e) {
            System.out.println("ERROR - " + this.getID() + " arriveAirport()");
        }
    }

    // A random number of passengers between 0 and 50 get in the bus from the airport.
    private void boardPassengersAirport() {
        // Randomly choose 0-50 passengers to board.
        int passengersToBoard = ThreadLocalRandom.current().nextInt(0, 1);
        // Board the maximum number of passengers possible (airport might have fewer than the randomly chosen value).
        this.setNumPassengers(this.airport.offloadPassengers(passengersToBoard));

        // Print in console.
        //BRUH System.out.println("Bus " + this.getID() + " picked up [" + this.getNumPassengers() + "] passengers at " + this.getAirportName() + " airport.");
        // Log event.
        //BRUH log.logEvent(this.getAirportName(), "Bus " + this.getID() + " picked up [" + this.getNumPassengers() + "] passengers at " + this.getAirportName() + " airport.");
    }

    // The bus heads downtown from the airport.
    private void goToDowntown() {
        // Print in console.
        //BRUH System.out.println("Bus " + this.getID() + " heading downtown.");
        // Log event.
        //BRUH log.logEvent(this.getAirportName(), "Bus " + this.getID() + " heading downtown.");

        // The bus takes 5-10 seconds to get downtown.
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(5000, 10001));
        } catch (InterruptedException e) {
            System.out.println("ERROR - " + this.getID() + " goToDowntown()");
        }
    }

    // Returns the bus ID.
    public String getID() {
        return id;
    }

    // Returns the airport's name the bus belongs to.
    public String getAirportName() {
        return airportName;
    }

    // Returns the number of passengers the bus is carrying.
    public int getNumPassengers() {
        return numPassengers;
    }

    // Sets the number of passengers the bus is carrying.
    public void setNumPassengers(int numPassengers) {
        this.numPassengers = numPassengers;
    }
}
