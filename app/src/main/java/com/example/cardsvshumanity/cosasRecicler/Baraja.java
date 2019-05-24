package com.example.cardsvshumanity.cosasRecicler;

public class Baraja {

    private String nombre;

    public Baraja(String nombre){
        this.nombre=nombre;
    }

    public String getNombre() {  return nombre;   }

    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return "Baraja{" +"nombre='" + nombre + '\'' + '}';
    }


}
