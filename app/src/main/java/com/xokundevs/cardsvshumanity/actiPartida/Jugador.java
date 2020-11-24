package com.xokundevs.cardsvshumanity.actiPartida;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Jugador implements Parcelable {
    private String nombre, email;
    private int puntos;

    public int getPuntos() {
        return puntos;
    }


    public Jugador(String email, String nombre){
        this.email =email;
        this.nombre = nombre;
        puntos = 0;
    }
    public String getEmail() {
        return email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public static final Creator<Jugador> CREATOR = new Creator<Jugador>() {
        @Override
        public Jugador createFromParcel(Parcel source) {
            return new Jugador(source);
        }

        @Override
        public Jugador[] newArray(int size) {
            return new Jugador[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(nombre);
        dest.writeInt(puntos);
    }


    protected Jugador(Parcel source){
        email = source.readString();
        nombre = source.readString();
        puntos = source.readInt();
    }
}
