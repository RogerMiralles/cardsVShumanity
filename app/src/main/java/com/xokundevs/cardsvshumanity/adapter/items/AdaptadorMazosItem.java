package com.xokundevs.cardsvshumanity.adapter.items;

public class AdaptadorMazosItem {
    private String deckEmail;
    private String deckName;
    private boolean checked;

    public AdaptadorMazosItem(String deckEmail, String deckName, boolean checked) {
        this.deckEmail = deckEmail;
        this.deckName = deckName;
        this.checked = checked;
    }

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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
