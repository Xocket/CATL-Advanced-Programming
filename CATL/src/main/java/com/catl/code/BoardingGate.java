// Package declaration.
package com.catl.code;

class BoardingGate implements Runnable {

    private ParkingArea parkingArea;
    private Airplane airplane;
    private final int gateNumber;

    public BoardingGate(ParkingArea parkingArea, int gateNumber) {
        this.parkingArea = parkingArea;
        this.gateNumber = gateNumber;
    }

    @Override
    public void run() {
        while (true) {
            // Check if the boarding gate is unoccupied.
            if (airplane == null) {
                // Take an airplane from the parking area.
                airplane = parkingArea.removeAirplane();
            }

            // Process boarding.
            this.boardPassengers(airplane);
            airplane = null;
        }
    }

    private void boardPassengers(Airplane airplane) {
        // Board passengers onto the airplane.
        // Notify the airplane that it has been accepted by a boarding gate.
        synchronized (airplane) {
            airplane.notify();
            try {
                // Wait until the airplane finishes boarding passengers.
                airplane.wait();
            } catch (InterruptedException e) {
                System.out.println("ERROR - Boarding Gate waiting for airplane to finish boarding passengers.");
            }
        }

        // Print in console.
        System.out.println("Airplane " + airplane.getID() + " with occupancy " + airplane.getOccupancy() + " has boarded in Gate " + this.getGateNumber());
        // Log event.
        airplane.getLog().logEvent(airplane.getCurrentAirport().getAirportName(), "Airplane " + airplane.getID() + " with occupancy " + airplane.getOccupancy() + " has boarded in Gate " + this.getGateNumber());
    }

    public int getGateNumber() {
        return gateNumber;
    }

}
