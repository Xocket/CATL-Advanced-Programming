package com.catl.code;

public class PauseControl {

    private boolean paused = false;

    public synchronized void pause() {
        paused = true;
    }

    public synchronized void resume() {
        if (paused) {
            paused = false;

            //Notify all threads to resume.
            notifyAll();
        }
    }

    public synchronized void checkPaused() {
        while (paused) {
            // Thread waits here when paused.
            try {
                wait();
            } catch (InterruptedException e) {
            }

        }
    }
}
