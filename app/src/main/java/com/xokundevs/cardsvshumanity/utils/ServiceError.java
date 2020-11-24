package com.xokundevs.cardsvshumanity.utils;

import com.xokundevs.cardsvshumanity.javaConCod.Connection;

public class ServiceError extends Throwable {
    private int errorCode;

    public ServiceError(int _errorCode) {
        super(getErrorMessage(_errorCode));
        errorCode = _errorCode;

    }

    public ServiceError() {
        super(getErrorMessage(Connection.UNKNOWN_ERROR));
        errorCode = Connection.UNKNOWN_ERROR;
    }

    private static String getErrorMessage(int errorCode) {
        String msg = null;
        switch (errorCode) {
            case Connection.CREATE_USER_ERROR_EXISTING_USER:
                msg = "User cannot be created because it already exists";
                break;
            case Connection.CREATE_USER_ERROR_INVALID_EMAIL:
                msg = "email not valid exception";
                break;
            case Connection.CREATE_USER_ERROR_INVALID_PARAMETERS:
                msg = "An error has ocurred when registering the user";
                break;
            case Connection.INVALID_CREDENTIALS_ERROR:
                msg = "The email or password are incorrect";
                break;
            case Connection.USER_ERROR_NON_EXISTANT_USER:
                msg = "The user searched doesn't exists";
                break;
            case Connection.BARAJA_ERROR_NON_EXISTANT_BARAJA:
                msg = "The deck searched doesn't exists";
                break;
            case Connection.CREATE_USER_ERROR_LONG_EMAIL:
                msg = "The email passed is longer than permitted";
                break;
            case Connection.CREATE_USER_ERROR_LONG_USERNAME:
                msg = "The username sent is longer than permitted";
                break;
            case Connection.CREATE_USER_ERROR_INVALID_USERNAME:
                msg = "The username sent contains non valid characters";
                break;
            case Connection.PARTIDA_ERROR_NON_EXISTANT_PARTIDA:
                msg = "The chosen game doesn't exists or is closed";
                break;
            case Connection.PARTIDA_ERROR_EXISTING_PARTIDA:
                msg = "Cannot create game -> the name is already used";
                break;
            case Connection.PARTIDA_ERROR_NO_ENTRAR_DENIED:
                msg = "You could not enter in the lobby";
                break;
            case Connection.UNKNOWN_ERROR:
                msg = "Unkown error";
                break;
            case Connection.SOCKET_DISCONNECTED:
                msg = "Connection with the server lost";
                break;
        }
        return msg;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
