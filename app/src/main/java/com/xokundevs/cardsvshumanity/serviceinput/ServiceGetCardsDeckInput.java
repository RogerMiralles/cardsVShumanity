package com.xokundevs.cardsvshumanity.serviceinput;

public class ServiceGetCardsDeckInput {
    private String deckEmailOwner;
    private String deckName;

    public ServiceGetCardsDeckInput(String deckEmailOwner, String deckName) {
        this.deckEmailOwner = deckEmailOwner;
        this.deckName = deckName;
    }

    public String getDeckEmailOwner() {
        return deckEmailOwner;
    }

    public void setDeckEmailOwner(String deckEmailOwner) {
        this.deckEmailOwner = deckEmailOwner;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }
}
