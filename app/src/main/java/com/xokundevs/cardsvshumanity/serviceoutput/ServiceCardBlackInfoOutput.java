package com.xokundevs.cardsvshumanity.serviceoutput;

public class ServiceCardBlackInfoOutput {

    private String nombre;
    private int numEspacios;

    public ServiceCardBlackInfoOutput(String nombre, int numEspacios){
        this.nombre = nombre;
        this.numEspacios = numEspacios;

    }
    public ServiceCardBlackInfoOutput(String nombre){
        this.nombre=nombre;
    }

    public String getNombre() {  return nombre;   }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getNumEspacios() {
        return numEspacios;
    }

    public void setNumEspacios(int numEspacios) {
        this.numEspacios = numEspacios;
    }
}
