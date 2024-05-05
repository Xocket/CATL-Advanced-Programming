// Package declaration.
package com.catl.code;

public class BoardingGate {

    private final int gateNumber;

    private Airplane airplane;
    private String airplaneStatus;

    public BoardingGate(int gateNumber) {
        this.gateNumber = gateNumber;
        this.airplane = null;
    }

    public int getGateNumber() {
        return gateNumber;
    }

    public void setAirplaneStatus(Airplane airplane) {
        // Check if airplane is null.
        if (airplane == null) {
            airplaneStatus = "";
            return;
        }

        int embarkDisembark = airplane.getEmbarkDisembark();
        String airplaneID = airplane.getOccupancyID();

        // Check if embarkDisembark is odd or even.
        if (embarkDisembark % 2 == 0) {
            airplaneStatus = "Boarding " + airplaneID;
        } else {
            airplaneStatus = "Disembark " + airplaneID;
        }
    }

    public String getAirplaneStatus() {
        return airplaneStatus;
    }

    public void setAirplaneStatusNull() {
        airplaneStatus = "";
    }

}
