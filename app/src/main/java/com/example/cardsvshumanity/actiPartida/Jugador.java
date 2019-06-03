package com.example.cardsvshumanity.actiPartida;

import java.io.Serializable;

public class Jugador implements Serializable {
    private String nombre, email;
    private int puntos;

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
}
