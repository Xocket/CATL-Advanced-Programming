// Package declaration.
package com.catl.code;

// Importing classes.
// Runway class which defines the behavior of each one of the four runways.
public class Runway implements Runnable {

    private final int runwayNumber;
    private final TaxiArea taxiArea;
    private Airplane airplane;
    private String airplaneStatus;

    public Runway(TaxiArea taxiArea, int runwayNumber) {
        this.taxiArea = taxiArea;
        this.runwayNumber = runwayNumber;
        this.airplane = null;
    }

    @Override
    public void run() {
        while (true) {
            // Check if the boarding gate is unoccupied.
            if (airplane == null) {
                // Take an airplane from the parking area.
                airplane = taxiArea.removeReadyAirplane();
            }

            // Process boarding.
            airplane.setRunway(this);
            //setAirplaneStatus(airplane);

            this.takeOff(airplane);
            airplane = null;
            setAirplaneStatus(airplane);
        }
    }

    private void takeOff(Airplane airplane) {
        // Notify the airplane that it has been accepted by a boarding gate.
        synchronized (airplane) {
            airplane.setIsNotified(true);
            airplane.notify();
            try {
                // Wait until the airplane finishes boarding passengers.
                airplane.wait();
            } catch (InterruptedException e) {
                System.out.println("ERROR - Runway.");
            }
        }

        // Print in console.
        System.out.println("Airplane " + airplane.getID() + " with occupancy " + airplane.getOccupancy() + " has boarded in runway " + this.getRunwayNumber());
        // Log event.
        airplane.getLog().logEvent(airplane.getCurrentAirport().getAirportName(), "Airplane " + airplane.getID() + " with occupancy " + airplane.getOccupancy() + " has boarded in Gate " + this.getRunwayNumber());
    }

    public void setAirplaneStatus(Airplane airplane) {
        // Check if airplane is null.
        if (airplane == null) {
            airplaneStatus = "";
            return;
        }

        int takeOffLand = airplane.getTakeOffLand();
        String airplaneID = airplane.getOccupancyID();

        // Check if takeOffLand is odd or even.
        if (takeOffLand % 2 == 0) {
            airplaneStatus = "Take off " + airplaneID;
        } else {
            airplaneStatus = "Landing " + airplaneID;
        }
    }

    public String getAirplaneStatus() {
        return airplaneStatus;
    }

    public void setAirplane(Airplane airplane) {
        this.airplane = airplane;
    }

    public Airplane getAirplane() {
        return airplane;
    }

    public int getRunwayNumber() {
        return runwayNumber;
    }
}
