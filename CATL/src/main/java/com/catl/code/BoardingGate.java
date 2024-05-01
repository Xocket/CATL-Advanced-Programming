package com.catl.code;

import java.util.concurrent.locks.*;

class BoardingGate {

    private Airplane airplane;
    private final Lock lock = new ReentrantLock();
    private final Condition gateAvailable = lock.newCondition();

    public void occupy(Airplane airplane) {
        lock.lock();
        try {
            while (this.airplane != null) {
                gateAvailable.await();
            }
            this.airplane = airplane;
        } catch (InterruptedException e) {
            // Do nothing.
        } finally {
            lock.unlock();
        }
    }

    public void leave() {
        lock.lock();
        try {
            this.airplane = null;
            gateAvailable.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
