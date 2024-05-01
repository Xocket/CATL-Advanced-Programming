// Package declaration.
package com.catl.code;

public class Airport {

    private BoardingGate[] boardingGates = new BoardingGate[6];

    public Airport() {

        // Initialize each Boarding Gate in the boardingGates array.
        for (int i = 0; i < 6; i++) {
            this.boardingGates[i] = new BoardingGate();
        }
    }
}
