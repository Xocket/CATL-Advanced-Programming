package com.catl.code;

import java.util.concurrent.ThreadLocalRandom;

public class AirplaneDispatcher implements Runnable {

    private final Log log;
    private final Airport madridAirport;
    private final Airport barcelonaAirport;

    public AirplaneDispatcher(Airport madridAirport, Airport barcelonaAirport, Log log) {
        this.madridAirport = madridAirport;
        this.barcelonaAirport = barcelonaAirport;
        this.log = log;
    }

    @Override
    public void run() {
        dispatchAirplanes();
    }

    public void dispatchAirplanes() {
        for (int i = 1; i <= 8000; i++) {
            String id = String.format("%04d", i);
            String airport;
            if (i % 2 == 0) {
                airport = "Madrid";
            } else {
                airport = "Barcelona";
            }

            Airplane airplane = new Airplane(id, airport, this.getMadridAirport(), this.getBarcelonaAirport(), log);
            Thread airplaneThread = new Thread(airplane);
            airplaneThread.start();

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
