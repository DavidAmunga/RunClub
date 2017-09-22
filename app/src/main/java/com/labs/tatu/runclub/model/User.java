package com.labs.tatu.runclub.model;

/**
 * Created by amush on 22-Sep-17.
 */

public class User {
    private String userName,userPhotoUrl;

    public User(String userName, String userPhotoUrl) {
        this.userName = userName;
        this.userPhotoUrl = userPhotoUrl;
    }
    public User()
    {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }
}
