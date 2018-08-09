package com.india.kalam.firebasetictactoe;

public class User {
    public String myID;
    public String opponentID;
    public String name;
    public String email;
    public String opponentEmail;
    public boolean request;
    public String accepted;
    public boolean currentlyPlaying;
    public Game myGame;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String name, String email, String id) {
        this.name = name;
        this.email = email;
        this.myID = id;
        opponentID = "";
        opponentEmail = "";
        accepted = "none";
        request = false;
        myGame = null;
        currentlyPlaying = false;
    }
}
