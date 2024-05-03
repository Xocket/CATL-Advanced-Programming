// Package declaration.
package com.catl.code;

// Importing classes.
import java.util.concurrent.atomic.AtomicInteger;

public class Airport {

    private BoardingGate[] boardingGates = new BoardingGate[6];
    private Thread[] boardingGateThreads = new Thread[6];

    private Hangar hangar = new Hangar();
    private MaintenanceHall maintenanceHall = new MaintenanceHall(20);
    private ParkingArea parkingArea = new ParkingArea();
    private TaxiArea taxiArea = new TaxiArea();

    private String airportName;
    
    private String statusBusToDowntown;
    private String statusBusToAirport;

    // TODO: Change variable from AtomicInteger to int (using locks) later on if necessary.
    private AtomicInteger currentPassengers = new AtomicInteger(0);
    private AtomicInteger totalPassengers = new AtomicInteger(0);

    public Airport(String airportName) {
        // Initialize each Boarding Gate in the boardingGates array and start each thread.
        for (int i = 0; i < 6; i++) {
            this.boardingGates[i] = new BoardingGate(this.getParkingArea(), i);
            this.boardingGateThreads[i] = new Thread(boardingGates[i]);
            this.boardingGateThreads[i].start();
        }
        this.airportName = airportName;
    }

    // Method to add passengers.
    public void addPassengers(int passengers) {
        currentPassengers.addAndGet(passengers);
        totalPassengers.addAndGet(passengers);
    }

    // Method to offload passengers, ensuring total passengers never go below 0.
    public int offloadPassengers(int passengers) {
        while (true) {
            int nowPassengers = this.currentPassengers.get();
            int passengersToOffload = Math.min(passengers, nowPassengers);
            int newPassengers = Math.max(0, nowPassengers - passengers);
            if (this.currentPassengers.compareAndSet(nowPassengers, newPassengers)) {
                return passengersToOffload;

            }
        }
    }

    public String getAirportName() {
        return airportName;
    }

    // Method to get total  passengers.
    public int getCurrentPassengers() {
        return currentPassengers.get();
    }

    // Method to get the historic total number of passengers.
    public int getTotalPassengers() {
        return totalPassengers.get();
    }

    public ParkingArea getParkingArea() {
        return parkingArea;
    }

    public Hangar getHangar() {
        return hangar;
    }

    public BoardingGate getBoardingGates(int i) {
        return boardingGates[i];
    }

    public String getStatusBusToDowntown() {
        return statusBusToDowntown;
    }

    public void setStatusBusToDowntown(String statusBusToDowntown) {
        this.statusBusToDowntown = statusBusToDowntown;
    }

    public String getStatusBusToAirport() {
        return statusBusToAirport;
    }

    public void setStatusBusToAirport(String statusBusToAirport) {
        this.statusBusToAirport = statusBusToAirport;
    }
    
    
}
