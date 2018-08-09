package com.india.kalam.firebasetictactoe;

public class Game {

    public boolean gameInProgress;
    public String currentTurn;
    public int currentMove;
    public String currentLetter;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Game() {
    }

    public Game(String currentTurn) {
        this.currentTurn = currentTurn;
        currentLetter = "X";
        gameInProgress = true;
        currentMove = -1;
    }
}
