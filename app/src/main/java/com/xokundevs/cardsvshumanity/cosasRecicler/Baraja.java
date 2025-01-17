package com.xokundevs.cardsvshumanity.cosasRecicler;

import java.io.Serializable;

public class Baraja implements Serializable {

    private String nombre;
    private String email;
    private String username;
    private String idioma;
    private int numCartas;

    public Baraja(String nombre){
        this.nombre=nombre;
    }

    public Baraja(String nombre, String email, String username, String idioma){
        this.nombre=nombre;
        this.email=email;
        this.idioma=idioma;
        this.username = username;
    }

    @Override
    public String toString() {
        return "Baraja{" +
                "nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", idioma='" + idioma + '\'' +
                ", numCartas=" + numCartas +
                '}';
    }

    public Baraja(String nombre, String email, String username, int numCartas, String idioma){
        this.nombre=nombre;
        this.email=email;
        this.idioma=idioma;
        this.numCartas=numCartas;
        this.username = username;
    }

    public String getNombre() {  return nombre;   }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public int getNumCartas() {
        return numCartas;
    }

    public void setNumCartas(int numCartas) {
        this.numCartas = numCartas;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
