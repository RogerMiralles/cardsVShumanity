package com.xokundevs.cardsvshumanity.cosasRecicler;

public class CartaNegra {

    private String nombre;
    private String email;
    private int numEspacios;
    private int id;

    public CartaNegra(String email, String nombre, int id, int numEspacios){
        this.id = id;
        this.nombre = nombre;
        this.numEspacios = numEspacios;
        this.email=email;

    }
    public CartaNegra(String nombre){
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
