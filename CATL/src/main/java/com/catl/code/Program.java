// Package declaration.
package com.catl.code;

// The Program class contains the execution code for the program.
public class Program {

    // The run method contains the Program logic.
    public void run() {

        // Create Log object to store events.
        Log log = new Log();

        // Create both airports.
        Airport madridAirport = new Airport();
        Airport barcelonaAirport = new Airport();

        // Create an instance of BusDispatcher.
        BusDispatcher busDispatcher = new BusDispatcher(log);

        // Create a new thread with the instance of BusDispatcher and start it.
        Thread busDispatcherThread = new Thread(busDispatcher);
        busDispatcherThread.start();

    }
}
