package com.xokundevs.cardsvshumanity.serviceinput;

import java.util.ArrayList;

public class ServiceCreateGameInput {
    private String gameName;
    private String gamePass;
    private int maxPlayers;
    private ArrayList<ServiceCreateGameDeckDataInput> deckData;

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGamePass() {
        return gamePass;
    }

    public void setGamePass(String gamePass) {
        this.gamePass = gamePass;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public ArrayList<ServiceCreateGameDeckDataInput> getDeckData() {
        return deckData;
    }

    public void setDeckData(ArrayList<ServiceCreateGameDeckDataInput> deckData) {
        this.deckData = deckData;
    }
}
