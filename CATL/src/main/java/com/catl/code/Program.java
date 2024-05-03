// Package declaration.
package com.catl.code;

// Importing classes.
import com.catl.ui.UserInterface;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

// The Program class contains the execution code for the program.
public class Program {

    // The run method contains the Program logic.
    public void run() {
        // Create Log object to store events.
        Log log = new Log();

        // Pause/Resume handler.
        PauseControl pauseControl = new PauseControl();

        // Create both airports.
        Airport madridAirport = new Airport("Madrid");
        Airport barcelonaAirport = new Airport("Barcelona");

        // Set UI theme.
        try {
            FlatMacDarkLaf.setup();
        } catch (Exception e) {
            System.err.println("Failed to initialize LaF.");
        }

        // Initialize a UserInterface object and start the UI.
        UserInterface ui = new UserInterface(madridAirport, barcelonaAirport, pauseControl);
        ui.startInterface();

        // Create an instance of BusDispatcher.
        BusDispatcher busDispatcher = new BusDispatcher(madridAirport, barcelonaAirport, log, pauseControl);

        // Create a new thread with the instance of BusDispatcher and start it.
        Thread busDispatcherThread = new Thread(busDispatcher);
        busDispatcherThread.start();

        // Create an instance of AirplaneDispatcherS.
        AirplaneDispatcher airplaneDispatcher = new AirplaneDispatcher(madridAirport, barcelonaAirport, log, pauseControl);

        // Create a new thread with the instance of AirplaneDispatcher and start it.
        Thread airplaneDispatcherThread = new Thread(airplaneDispatcher);
        airplaneDispatcherThread.start();
    }
}
