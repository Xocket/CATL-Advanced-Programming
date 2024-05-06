package com.catl.code;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicInteger;

public interface AirportInterface extends Remote {

    AtomicInteger getNumPassengers() throws RemoteException;

    int getNumAirplanesInHangar() throws RemoteException;

    int getNumAirplanesInMaintenance() throws RemoteException;

    int getNumAirplanesInParking() throws RemoteException;

    int getNumAirplanesInTaxi() throws RemoteException;

    String airwayStatus() throws RemoteException;

    void closeRunway(int runwayNumber) throws RemoteException;

    void openRunway(int runwayNumber) throws RemoteException;
}
