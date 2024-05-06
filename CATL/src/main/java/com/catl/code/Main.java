// Package declaration.
package com.catl.code;

// The Main class serves as the entry point of the program.
import java.rmi.RemoteException;

public class Main {

    // The main method execution.
    public static void main(String[] args) throws RemoteException {
        // Running the Program code.
        new Program().run();
    }
}
