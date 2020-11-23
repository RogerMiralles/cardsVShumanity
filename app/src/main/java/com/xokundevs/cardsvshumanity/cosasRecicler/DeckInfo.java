package com.xokundevs.cardsvshumanity.cosasRecicler;

import android.os.Parcel;
import android.os.Parcelable;

public class DeckInfo implements Parcelable {
    private String deckEmailOwner;
    private String deckName;
    private String deckLanguage;
    private String deckOwnerUsername;
    private int deckSize;
    private boolean editable;

    public DeckInfo(String deckEmailOwner, String deckName, String deckLanguage, String deckOwnerUsername, int deckSize, boolean editable) {
        this.deckEmailOwner = deckEmailOwner;
        this.deckName = deckName;
        this.deckLanguage = deckLanguage;
        this.deckSize = deckSize;
        this.deckOwnerUsername = deckOwnerUsername;
        this.editable = editable;
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

    public String getDeckLanguage() {
        return deckLanguage;
    }

    public void setDeckLanguage(String deckLanguage) {
        this.deckLanguage = deckLanguage;
    }

    public String getDeckOwnerUsername() {
        return deckOwnerUsername;
    }

    public void setDeckOwnerUsername(String deckOwnerUsername) {
        this.deckOwnerUsername = deckOwnerUsername;
    }

    public int getDeckSize() {
        return deckSize;
    }

    public void setDeckSize(int deckSize) {
        this.deckSize = deckSize;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public static final Creator<DeckInfo> CREATOR = new Creator<DeckInfo>() {
        @Override
        public DeckInfo createFromParcel(Parcel source) {
            return new DeckInfo(source);
        }

        @Override
        public DeckInfo[] newArray(int size) {
            return new DeckInfo[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deckEmailOwner);
        dest.writeString(deckName);
        dest.writeString(deckLanguage);
        dest.writeString(deckOwnerUsername);
        dest.writeInt(deckSize);
        dest.writeByte(editable? (byte)1 : (byte)0);
    }

    protected DeckInfo(Parcel source){
        deckEmailOwner = source.readString();
        deckName = source.readString();
        deckLanguage = source.readString();
        deckOwnerUsername = source.readString();
        deckSize = source.readInt();
        editable = (source.readByte() == (byte) 1);
    }
}
