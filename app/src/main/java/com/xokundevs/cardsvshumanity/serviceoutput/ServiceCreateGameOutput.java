package com.xokundevs.cardsvshumanity.serviceoutput;

import com.xokundevs.cardsvshumanity.javaConCod.SocketHandler;

import javax.crypto.SecretKey;


public class ServiceCreateGameOutput {
    private SocketHandler socketHandler;
    private SecretKey secretKey;

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
}
