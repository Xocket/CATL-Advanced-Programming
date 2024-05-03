// Package declaration.
package com.catl.code;

// Importing classes.
import java.util.concurrent.ThreadLocalRandom;

public class AirplaneDispatcher implements Runnable {

    private final Log log;
    private final Airport madridAirport;
    private final Airport barcelonaAirport;
    private final PauseControl pauseControl;

    public AirplaneDispatcher(Airport madridAirport, Airport barcelonaAirport, Log log, PauseControl pauseControl) {
        this.madridAirport = madridAirport;
        this.barcelonaAirport = barcelonaAirport;
        this.log = log;
        this.pauseControl = pauseControl;
    }

    @Override
    public void run() {
        dispatchAirplanes();
    }

    public void dispatchAirplanes() {
        for (int i = 1; i <= 8000; i++) {

            // Wait if paused.
            pauseControl.checkPaused();

            String id = String.format("%04d", i);
            if (i % 2 == 0) {
                Airplane airplane = new Airplane(id, this.getMadridAirport(), this.getBarcelonaAirport(), log, pauseControl);
                Thread airplaneThread = new Thread(airplane);
                airplaneThread.start();
            } else {
                Airplane airplane = new Airplane(id, this.getBarcelonaAirport(), this.getMadridAirport(), log, pauseControl);
                Thread airplaneThread = new Thread(airplane);
                airplaneThread.start();
            }

            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 3001));
            } catch (InterruptedException e) {
                System.out.println("ERROR - Dispatching airplanes.");
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
