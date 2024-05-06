// Package declaration.
package com.catl.code;

// Importing classes.
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

// Airport class which contains all the zones and methods for airplanes to navigate them.
public class Airport extends UnicastRemoteObject implements AirportInterface {

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
    // An array of locks to protect each individual Runway.
    private final ReentrantLock[] locksR;

    // Airport class constructor.
    public Airport(String airportName) throws RemoteException {

        // Initialize the airport name.
        this.airportName = airportName;

        // Initialize the boardingGates and runways arrays.
        this.boardingGates = new BoardingGate[6];

        // Initialize each semaphore.
        this.semaphoreBG = new Semaphore(6, true);
        this.semaphoreR = new Semaphore(1);

        // Initialize the locksBG and the locksR arrays.
        this.locksBG = new ReentrantLock[6];
        this.locksR = new ReentrantLock[4];

        // Initialize each BoardingGate object and its associated lock.
        for (int i = 0; i < 6; i++) {
            String type;
            type = switch (i) {
                case 0 ->
                    "takeoff";
                case 5 ->
                    "landing";
                default ->
                    "both";
            };
            this.boardingGates[i] = new BoardingGate(i, type);
            this.locksBG[i] = new ReentrantLock();
        }

        runwayStatus = new AtomicBoolean[4];
        openRunways = new LinkedBlockingQueue<>();

        // Initialize the runways and their statuses
        for (int i = 0; i < runwayStatus.length; i++) {
            runwayStatus[i] = new AtomicBoolean(true); // All runways start as open
            openRunways.add(new Runway(i)); // Add new Runway objects to the queue
        }

        runways = new Runway[4]; // Initialize the runways array
        for (int i = 0; i < runwayStatus.length; i++) {
            runways[i] = new Runway(i); // Populate the array
            openRunways.add(runways[i]); // Add runways to the queue
        }

        this.lockBoardingGates = new ReentrantLock();
    }

    private final AtomicBoolean[] runwayStatus; // Array to keep track of runway statuses
    private final BlockingQueue<Runway> openRunways; // Queue to manage available runways

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

    private final ReentrantLock lockBoardingGates;

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

    // Method to get a runway by index
    public Runway getRunway(int i) {
        if (i >= 0 && i < runways.length) {
            return runways[i];
        }
        return null; // Return null or throw an exception if the index is out of bounds
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

    public MaintenanceHall getMaintenanceHall() {
        return maintenanceHall;
    }

    @Override
    public AtomicInteger getNumPassengers() throws RemoteException {
        return currentPassengers;
    }

    @Override
    public int getNumAirplanesInHangar() throws RemoteException {
        return this.getHangar().getNumberAirplanes();
    }

    @Override
    public int getNumAirplanesInMaintenance() throws RemoteException {
        return this.getMaintenanceHall().getNumberAirplanes();
    }

    @Override
    public int getNumAirplanesInParking() throws RemoteException {
        return this.getParkingArea().getNumberAirplanes();
    }

    @Override
    public int getNumAirplanesInTaxi() throws RemoteException {
        return this.getTaxiArea().getNumberAirplanes();
    }

    @Override
    public String airwayStatus() throws RemoteException {
        return this.getAirway().getStatus();
    }

    @Override
    public void closeRunway(int runwayNumber) throws RemoteException {
        runwayStatus[runwayNumber].set(false); // Set the status to closed
        openRunways.removeIf(runway -> runway.getRunwayNumber() == runwayNumber); // Remove from queue
    }

    @Override
    public void openRunway(int runwayNumber) throws RemoteException {
        runwayStatus[runwayNumber].set(true); // Set the status to open
        openRunways.add(new Runway(runwayNumber)); // Add back to the queue

    }

    public AtomicBoolean[] getRunwayStatus() {
        return runwayStatus;
    }

    public BlockingQueue<Runway> getOpenRunways() {
        return openRunways;
    }

    public AtomicBoolean[] getRunwayStatusArray() {
        return runwayStatus;
    }

    public boolean gateIsAvailableExceptLast() {
        lockBoardingGates.lock();
        try {
            for (int i = 0; i < boardingGates.length - 1; i++) {
                if (boardingGates[i].getIsAvailable().get()) {
                    return true; // At least one gate is available
                }
            }
            return false; // No gate is available
        } finally {
            lockBoardingGates.unlock();
        }
    }

    public boolean gateIsAvailableExceptFirst() {
        lockBoardingGates.lock();
        try {
            for (int i = 1; i < boardingGates.length; i++) {
                if (boardingGates[i].getIsAvailable().get()) {
                    return true; // At least one gate is available
                }
            }
            return false; // No gate is available
        } finally {
            lockBoardingGates.unlock();
        }
    }

}
