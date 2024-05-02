// Package declaration.
package com.catl.code;

// The Program class contains the execution code for the program.
public class Program {

    // The run method contains the Program logic.
    public void run() {
        // Create Log object to store events.
        Log log = new Log();

        // Create both airports.
        Airport madridAirport = new Airport("Madrid");
        Airport barcelonaAirport = new Airport("Barcelona");

        // Create an instance of BusDispatcher.
        BusDispatcher busDispatcher = new BusDispatcher(madridAirport, barcelonaAirport, log);

        // Create a new thread with the instance of BusDispatcher and start it.
        Thread busDispatcherThread = new Thread(busDispatcher);
        busDispatcherThread.start();

        // Create an instance of BusDispatcher.
        AirplaneDispatcher airplaneDispatcher = new AirplaneDispatcher(madridAirport, barcelonaAirport, log);

        // Create a new thread with the instance of AirplaneDispatcher and start it.
        Thread airplaneDispatcherThread = new Thread(airplaneDispatcher);
        airplaneDispatcherThread.start();
    }
}
