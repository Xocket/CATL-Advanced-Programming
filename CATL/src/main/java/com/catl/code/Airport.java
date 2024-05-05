// Package declaration.
package com.catl.code;

// Importing classes.
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

// Airport class which contains all the zones and methods for airplanes to navigate them.
public class Airport {

    // Create an array of 6 BoardingGate objects.
    private final BoardingGate[] boardingGates;

    // A semaphore to protect the boardingGates array.
    private final Semaphore semaphoreBG;
    // An array of locks to protect each individual BoardingGate.
    private final ReentrantLock[] locksBG;

    // Create an array of 4 Runway objects.
    private final Runway[] runways;

    // A semaphore to protect the runways array.
    private final Semaphore semaphoreR;
    // An array of locks to protect each individual BoardingGate.
    private final ReentrantLock[] locksR;

    // Airport class constructor.
    public Airport(String airportName) {

        // Initialize the airport name.
        this.airportName = airportName;

        // Initialize the boardingGates and runways arrays.
        this.boardingGates = new BoardingGate[6];
        this.runways = new Runway[4];

        // Initialize each semaphore.
        this.semaphoreBG = new Semaphore(6, true);
        this.semaphoreR = new Semaphore(4, true);

        // Initialize the locksBG and the locksR arrays.
        this.locksBG = new ReentrantLock[6];
        this.locksR = new ReentrantLock[4];

        // Initialize each BoardingGate object and its associated lock.
        for (int i = 0; i < 6; i++) {
            this.boardingGates[i] = new BoardingGate(i);
            this.locksBG[i] = new ReentrantLock();
        }

        // Initialize each Runway object and its associated lock.
        for (int i = 0; i < 4; i++) {
            this.runways[i] = new Runway(i);
            this.locksR[i] = new ReentrantLock();
        }
    }

    // TODO: finish commenting these.
    private Hangar hangar = new Hangar();
    private MaintenanceHall maintenanceHall = new MaintenanceHall(20);
    private ParkingArea parkingArea = new ParkingArea();
    private TaxiArea taxiArea = new TaxiArea();
    private Airway airway = new Airway();
    private String airportName;
    private String statusBusToDowntown;
    private String statusBusToAirport;

    private AtomicInteger currentPassengers = new AtomicInteger(0);
    private AtomicInteger totalPassengers = new AtomicInteger(0);

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

    public Runway getRunway(int i) {
        return runways[i];
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

    public TaxiArea getTaxiArea() {
        return taxiArea;
    }

    public Airway getAirway() {
        return airway;
    }

    public Semaphore getSemaphoreBG() {
        return semaphoreBG;
    }

    public ReentrantLock[] getLocksBG() {
        return locksBG;
    }

    public Semaphore getSemaphoreR() {
        return semaphoreR;
    }

    public ReentrantLock[] getLocksR() {
        return locksR;
    }
}
