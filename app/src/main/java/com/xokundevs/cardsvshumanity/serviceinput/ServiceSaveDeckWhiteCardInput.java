package com.xokundevs.cardsvshumanity.serviceinput;

public class ServiceSaveDeckWhiteCardInput {
    private String nombre;

    public ServiceSaveDeckWhiteCardInput(String nombre) {
        this.nombre = nombre;
    }

    public ServiceSaveDeckWhiteCardInput(String nombre, int id) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
