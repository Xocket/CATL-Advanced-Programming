// Package declaration.
package com.catl.code;

import java.util.concurrent.atomic.AtomicBoolean;

public class BoardingGate {

    private final int gateNumber;

    private Airplane airplane;
    private String airplaneStatus;
    private final String type;
    private AtomicBoolean isAvailable;

    public BoardingGate(int gateNumber, String type) {
        this.gateNumber = gateNumber;
        this.airplane = null;
        this.type = type;
        this.isAvailable = new AtomicBoolean(true);
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

    public String getType() {
        return type;
    }

    public AtomicBoolean getIsAvailable() {
        return isAvailable;
    }

}
