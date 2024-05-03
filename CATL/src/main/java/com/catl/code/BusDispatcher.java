// Package declaration.
package com.catl.code;

// Importing classes.
import java.util.concurrent.ThreadLocalRandom;

public class BusDispatcher implements Runnable {

    private final Log log;
    private final Airport madridAirport;
    private final Airport barcelonaAirport;
    private final PauseControl pauseControl;

    public BusDispatcher(Airport madridAirport, Airport barcelonaAirport, Log log, PauseControl pauseControl) {
        this.madridAirport = madridAirport;
        this.barcelonaAirport = barcelonaAirport;
        this.log = log;
        this.pauseControl = pauseControl;
    }

    @Override
    public void run() {
        dispatchBuses();
    }

    public void dispatchBuses() {
        for (int i = 1; i <= 4000; i++) {

            // Wait if paused.
            pauseControl.checkPaused();

            String id = String.format("%04d", i);
            String airport;

            if (i % 2 == 0) {
                airport = "Madrid";
                Bus bus = new Bus(id, airport, this.getMadridAirport(), log, pauseControl);
                Thread busThread = new Thread(bus);
                busThread.start();

            } else {
                airport = "Barcelona";
                Bus bus = new Bus(id, airport, this.getBarcelonaAirport(), log, pauseControl);
                Thread busThread = new Thread(bus);
                busThread.start();
            }

            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1001));
            } catch (InterruptedException e) {
                System.out.println("ERROR - Dispatching buses.");
            }
        }
    }

    public Airport getMadridAirport() {
        return madridAirport;
    }

    public Airport getBarcelonaAirport() {
        return barcelonaAirport;
    }
}
