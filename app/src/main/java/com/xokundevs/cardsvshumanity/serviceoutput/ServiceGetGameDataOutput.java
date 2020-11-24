package com.xokundevs.cardsvshumanity.serviceoutput;

import java.util.ArrayList;

public class ServiceGetGameDataOutput {
    private ArrayList<ServiceGetGameDataItemOutput> gameDataList;

    public ServiceGetGameDataOutput() {
    }

    public ArrayList<ServiceGetGameDataItemOutput> getGameDataList() {
        return gameDataList;
    }

    public void setGameDataList(ArrayList<ServiceGetGameDataItemOutput> gameDataList) {
        this.gameDataList = gameDataList;
    }
}
