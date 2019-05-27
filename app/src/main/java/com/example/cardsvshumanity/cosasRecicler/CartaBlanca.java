package com.example.cardsvshumanity.cosasRecicler;

public class CartaBlanca {
    private String nombre, email;
    private int id;

    public CartaBlanca(String email, String nombre, int id){
        this.nombre = nombre;
        this.id = id;
        this.email = email;
    }

    public CartaBlanca(String nombre){
        this.nombre=nombre;
    }

    public String getNombre() {  return nombre;   }

    public void setNombre(String nombre) { this.nombre = nombre; }
}
