package com.xokundevs.cardsvshumanity.serviceinput;

import java.util.ArrayList;

public class ServiceSaveDeckInput {
    private String deckEmail;
    private String deckName;
    private String deckUsername;
    private String deckLanguage;
    private ArrayList<ServiceSaveDeckWhiteCardInput> whiteCards;
    private ArrayList<ServiceSaveDeckBlackCardInput> blackCards;

    public String getDeckEmail() {
        return deckEmail;
    }

    public void setDeckEmail(String deckEmail) {
        this.deckEmail = deckEmail;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public String getDeckUsername() {
        return deckUsername;
    }

    public void setDeckUsername(String deckUsername) {
        this.deckUsername = deckUsername;
    }

    public int getDeckSize() {
        return whiteCards.size() + blackCards.size();
    }


    public String getDeckLanguage() {
        return deckLanguage;
    }

    public void setDeckLanguage(String deckLanguage) {
        this.deckLanguage = deckLanguage;
    }

    public ArrayList<ServiceSaveDeckWhiteCardInput> getWhiteCards() {
        return whiteCards;
    }

    public void setWhiteCards(ArrayList<ServiceSaveDeckWhiteCardInput> whiteCards) {
        this.whiteCards = whiteCards;
    }

    public ArrayList<ServiceSaveDeckBlackCardInput> getBlackCards() {
        return blackCards;
    }

    public void setBlackCards(ArrayList<ServiceSaveDeckBlackCardInput> blackCards) {
        this.blackCards = blackCards;
    }
}
