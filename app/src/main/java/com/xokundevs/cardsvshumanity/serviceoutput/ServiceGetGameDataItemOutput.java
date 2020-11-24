package com.xokundevs.cardsvshumanity.serviceoutput;

public class ServiceGetGameDataItemOutput {
    private String gameName;
    private String ownerUsername;
    private int currentUsers;
    private int maxPlayers;

    public ServiceGetGameDataItemOutput(String gameName, String ownerUsername, int currentUsers, int maxPlayers) {
        this.gameName = gameName;
        this.ownerUsername = ownerUsername;
        this.currentUsers = currentUsers;
        this.maxPlayers = maxPlayers;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public int getCurrentUsers() {
        return currentUsers;
    }

    public void setCurrentUsers(int currentUsers) {
        this.currentUsers = currentUsers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
}
