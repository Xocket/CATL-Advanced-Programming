package com.catl.code;

import java.io.*;
import java.util.concurrent.locks.*;

public class Log {

    private final ReentrantLock lock = new ReentrantLock();
    private final String logFolderName = "Log";
    private final String logFileName = "airportEvolution.txt";
    private boolean banner = false;

    public void logEvent(String airport, String event) {
        lock.lock();
        try {
            // Get the current working directory
            String currentDir = System.getProperty("user.dir");
            // Create the relative file path for the Log folder
            File logDir = new File(currentDir + File.separator + logFolderName);
            File logFile = new File(logDir, logFileName);

            // Check if the Log directory exists, if not, create it
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));

            String timeStamp = new java.text.SimpleDateFormat("yyyy/MM/dd - HH:mm:ss:SSS").format(new java.util.Date());

            if (!banner) {
                out.println("------------------------------ AIRPORT EVOLUTION ------------------------------\n");
                banner = true;
            }

            out.println(timeStamp + " - " + airport + " - " + event);
            out.close();

        } catch (IOException e) {
            System.err.println("There was a problem writing to the log file");
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
