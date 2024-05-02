// Package declaration.
package com.catl.code;

class BoardingGate implements Runnable {

    private final ParkingArea parkingArea;
    private Airplane airplane;

    public BoardingGate(ParkingArea parkingArea) {
        this.parkingArea = parkingArea;
    }

    @Override
    public void run() {
        try {
            while (true) {
                airplane = parkingArea.removeAirplane();
                // TODO: Process the airplane.
                airplane = null;
            }
        } catch (InterruptedException e) {
            System.out.println("ERROR - Boarding Gate");
        }
    }

}
