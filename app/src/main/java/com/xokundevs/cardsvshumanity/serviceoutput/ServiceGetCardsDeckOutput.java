package com.xokundevs.cardsvshumanity.serviceoutput;

import java.io.Serializable;
import java.util.ArrayList;

public class ServiceGetCardsDeckOutput implements Serializable {

    private ArrayList<ServiceCardWhiteInfoOutput> whiteCardList;
    private ArrayList<ServiceCardBlackInfoOutput> blackCardList;

    public ServiceGetCardsDeckOutput(ArrayList<ServiceCardWhiteInfoOutput> _whiteCardList, ArrayList<ServiceCardBlackInfoOutput> _blackCardList){
        this.whiteCardList = _whiteCardList;
        this.blackCardList = _blackCardList;
    }

    public ArrayList<ServiceCardWhiteInfoOutput> getWhiteCardList() {
        return whiteCardList;
    }

    public void setWhiteCardList(ArrayList<ServiceCardWhiteInfoOutput> whiteCardList) {
        this.whiteCardList = whiteCardList;
    }

    public ArrayList<ServiceCardBlackInfoOutput> getBlackCardList() {
        return blackCardList;
    }

    public void setBlackCardList(ArrayList<ServiceCardBlackInfoOutput> blackCardList) {
        this.blackCardList = blackCardList;
    }
}
