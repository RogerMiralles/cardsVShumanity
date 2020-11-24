package com.xokundevs.cardsvshumanity.serviceinput;

public class ServiceJoinGameInput {
    private String gameName;
    private String passwordName;

    public ServiceJoinGameInput(String gameName, String passwordName) {
        this.gameName = gameName;
        this.passwordName = passwordName;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getPasswordName() {
        return passwordName;
    }

    public void setPasswordName(String passwordName) {
        this.passwordName = passwordName;
    }
}
