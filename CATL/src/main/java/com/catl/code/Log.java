package com.catl.code;

import java.io.*;
import java.util.concurrent.*;

public class Log {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final String logFolderName = "Log";
    private final String logFileName = "airportEvolution.txt";
    private final File logDir;
    private final File logFile;
    private boolean banner = false;

    public Log() {
        // Initialize the directory and file path once when the Log object is created.
        String currentDir = System.getProperty("user.dir");
        logDir = new File(currentDir + File.separator + logFolderName);
        logFile = new File(logDir, logFileName);
        // Check if the Log directory exists, if not, create it
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        // Add a shutdown hook to handle the executor shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void logEvent(String airport, String event) {
        executor.submit(() -> {
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)))) {
                // Generate the timestamp as close as possible to the event occurrence
                String timeStamp = new java.text.SimpleDateFormat("yyyy/MM/dd - HH:mm:ss:SSS").format(new java.util.Date());

                // Print the banner once
                if (!banner) {
                    out.println("------------------------------ AIRPORT EVOLUTION ------------------------------\n");
                    banner = true;
                }

                // Log the event with the timestamp
                out.println(timeStamp + " - " + airport + " - " + event);

            } catch (IOException e) {
                System.err.println("There was a problem writing to the log file");
            }
        });
    }

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
