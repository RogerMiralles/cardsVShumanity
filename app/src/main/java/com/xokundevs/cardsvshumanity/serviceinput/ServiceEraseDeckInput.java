package com.xokundevs.cardsvshumanity.serviceinput;

public class ServiceEraseDeckInput {
    private String userEmail;
    private String userPassword;
    private String targetDeckName;

    public ServiceEraseDeckInput(String userEmail, String userPassword, String targetDeckName) {
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.targetDeckName = targetDeckName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getTargetDeckName() {
        return targetDeckName;
    }

    public void setTargetDeckName(String targetDeckName) {
        this.targetDeckName = targetDeckName;
    }
}
