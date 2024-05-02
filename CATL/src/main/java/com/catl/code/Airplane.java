// Package declaration.
package com.catl.code;

// Importing classes.
import java.util.concurrent.ThreadLocalRandom;

public class Airplane implements Runnable {

    private final String id;
    private final String airportName;
    private final Log log;
    private final int capacity;

    private final Airport madridAirport;
    private final Airport barcelonaAirport;

    public Airplane(String id, String airportName, Airport madridAirport, Airport barcelonaAirport, Log log) {
        this.id = this.getRandomLetters() + "-" + id;
        this.airportName = airportName;

        this.madridAirport = madridAirport;
        this.barcelonaAirport = barcelonaAirport;

        this.log = log;
        this.capacity = ThreadLocalRandom.current().nextInt(100, 301);
    }

    @Override
    public void run() {
        while (true) {
        }
    }

    private String getRandomLetters() {
        char letter1 = (char) (ThreadLocalRandom.current().nextInt(26) + 'A');
        char letter2 = (char) (ThreadLocalRandom.current().nextInt(26) + 'A');

        return letter1 + "" + letter2;
    }

}
