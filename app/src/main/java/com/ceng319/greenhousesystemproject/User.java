package com.ceng319.greenhousesystemproject;

public class User {
    public String registeredName, registeredEmail, registeredProductKey;

    public User(){

    }

    public User(String registeredName, String registeredEmail, String registeredProductKey) {
        this.registeredName = registeredName;
        this.registeredEmail= registeredEmail;
        this.registeredProductKey=registeredProductKey;
    }
}