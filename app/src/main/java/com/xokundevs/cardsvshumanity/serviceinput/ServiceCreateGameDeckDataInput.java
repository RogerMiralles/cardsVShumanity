package com.xokundevs.cardsvshumanity.serviceinput;

public class ServiceCreateGameDeckDataInput {
    private String deckName;
    private String deckEmail;

    public ServiceCreateGameDeckDataInput(String deckName, String deckEmail) {
        this.deckName = deckName;
        this.deckEmail = deckEmail;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public String getDeckEmail() {
        return deckEmail;
    }

    public void setDeckEmail(String deckEmail) {
        this.deckEmail = deckEmail;
    }
}
