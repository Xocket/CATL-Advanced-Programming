// Package declaration.
package com.catl.code;

// PauseControl class to handle threads pausing and resuming.
public final class PauseControl {

    // Boolean to store wether program is paused or not.
    private boolean paused = false;

    // Method to pause the program.
    public synchronized void pause() {
        paused = true;
    }

    // Method to resume the program.
    public synchronized void resume() {
        if (paused) {
            paused = false;

            // Notify all threads to resume.
            notifyAll();
        }
    }

    // Method to check if program is paused.
    public synchronized void checkPaused() {
        while (paused) {
            // Threads wait when the program is paused.
            try {
                wait();
            } catch (InterruptedException e) {
                
            }

        }
    }
}
