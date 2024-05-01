package com.catl.code;

import java.util.concurrent.ThreadLocalRandom;

public class BusDispatcher implements Runnable {

    private final Log log;

    public BusDispatcher(Log log) {
        this.log = log;
    }

    @Override
    public void run() {
        dispatchBuses();
    }

    public void dispatchBuses() {
        for (int i = 1; i <= 4000; i++) {
            String id = String.format("%04d", i);
            String airport;
            if (i % 2 == 0) {
                airport = "Madrid";
            } else {
                airport = "Barcelona";
            }

            Bus bus = new Bus(id, airport, log); // Assuming 'log' is defined elsewhere
            Thread busThread = new Thread(bus);
            busThread.start();

            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(5, 10));
            } catch (InterruptedException e) {
                System.out.println("ERROR - Dispatching buses.");
            }
        }
    }
}
