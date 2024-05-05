// Package declaration.
package com.catl.code;

// Runway class which defines the behavior of each one of the four runways.
public class Runway {

    private final int runwayNumber;
    private Airplane airplane;
    private String airplaneStatus;

    public Runway(int runwayNumber) {
        this.runwayNumber = runwayNumber;
        this.airplane = null;
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

    public void setAirplaneStatusNull() {
        this.airplaneStatus = "";
    }
}
