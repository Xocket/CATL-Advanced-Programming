// Package declaration.
package com.catl.code;

// Importing classes.
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// Class Log to save the application's events.
public class Log {

    // Variables declaration.
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final String logFolderName = "Log";
    private final String logFileName = "airportEvolution.txt";
    private final File logDir;
    private final File logFile;

    // Bool to print once the banner.
    private boolean banner = false;

    // Log class constructor.
    public Log() {
        // Initialize the directory and file path once when the Log object is created.
        String currentDir = System.getProperty("user.dir");
        logDir = new File(currentDir + File.separator + logFolderName);
        logFile = new File(logDir, logFileName);

        // Check if the Log directory exists, if not, create it.
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        // Add a shutdown hook to handle the executor shutdown.
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    // TODO: Optimize this method even further. Refactor the rest of the class if needed.
    public void logEvent(String airport, String event) {
        executor.submit(() -> {
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)))) {

                // Generate the timestamp as close as possible to the event occurrence.
                String timeStamp = new java.text.SimpleDateFormat("yyyy/MM/dd - HH:mm:ss:SSS").format(new java.util.Date());

                // Print the banner once.
                if (!banner) {
                    out.println("------------------------------ AIRPORT EVOLUTION ------------------------------\n");
                    banner = true;
                }

                // Modify the airport variable to have only the first 3 letters in upper case.
                String modifiedAirport = airport.substring(0, 3).toUpperCase();

                // Log the event.
                out.println(timeStamp + " - " + modifiedAirport + " - " + event);

            } catch (IOException e) {
                System.err.println("There was a problem writing to the log file");
            }
        });
    }

    // Shutdown method to stop the executor.
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
