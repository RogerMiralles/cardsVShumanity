package com.example.cardsvshumanity.cosasRecicler;

public class CartaNegra {

    private String nombre, email;
    private int numEspacios;
    private int id;

    public CartaNegra(String email, String nombre, int numEspacios, int id){
        this.id = id;
        this.nombre = nombre;
        this.numEspacios = numEspacios;
        this.email = email;
    }
    public CartaNegra(String nombre){
        this.nombre=nombre;
    }

    public String getNombre() {  return nombre;   }

    public void setNombre(String nombre) { this.nombre = nombre; }
}
