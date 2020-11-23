package com.xokundevs.cardsvshumanity.serviceoutput;

public class ServiceSimpleDeckInfoOutput {
    private String deckEmail;
    private String deckName;
    private String deckUsername;
    private int deckSize;
    private String deckLanguage;
    private boolean editable;

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
        return deckSize;
    }

    public void setDeckSize(int deckSize) {
        this.deckSize = deckSize;
    }

    public String getDeckLanguage() {
        return deckLanguage;
    }

    public void setDeckLanguage(String deckLanguage) {
        this.deckLanguage = deckLanguage;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
