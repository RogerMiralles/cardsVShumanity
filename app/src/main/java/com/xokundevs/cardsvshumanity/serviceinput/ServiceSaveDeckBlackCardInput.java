package com.xokundevs.cardsvshumanity.serviceinput;

public class ServiceSaveDeckBlackCardInput {
    private String nombre;
    private int numEspacios;

    public ServiceSaveDeckBlackCardInput(String nombre, int numEspacios) {
        this.nombre = nombre;
        this.numEspacios = numEspacios;
    }

    public String getNombre() {
        return nombre;
    }

    public int getNumEspacios() {
        return numEspacios;
    }
}
