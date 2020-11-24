package com.xokundevs.cardsvshumanity.serviceoutput;

import com.xokundevs.cardsvshumanity.actiPartida.Jugador;
import com.xokundevs.cardsvshumanity.javaConCod.SocketHandler;

import java.util.ArrayList;

import javax.crypto.SecretKey;

public class ServiceJoinGameOutput {
    private ArrayList<Jugador> jugadors;
    private SocketHandler socketHandler;
    private SecretKey secretKey;
    private String lobbyName;

    public ServiceJoinGameOutput(ArrayList<Jugador> jugadors, SocketHandler socketHandler, SecretKey secretKey, String lobbyName) {
        this.jugadors = jugadors;
        this.socketHandler = socketHandler;
        this.secretKey = secretKey;
        this.lobbyName = lobbyName;
    }

    public ArrayList<Jugador> getJugadors() {
        return jugadors;
    }

    public void setJugadors(ArrayList<Jugador> jugadors) {
        this.jugadors = jugadors;
    }

    public SocketHandler getSocketHandler() {
        return socketHandler;
    }

    public void setSocketHandler(SocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }
}
