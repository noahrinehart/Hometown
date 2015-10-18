package com.boilermakeproject.hometownapp;

public class Contact {
    //Instance Variables
    private String name;
    private String hometown;
    private String number;
    private double latitude;
    private double longitude;


    //Accessors
    public Contact(String name, String hometown, double latitude, double longitude) {
        this.name = name;
        this.hometown = hometown;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public String getName() {
        return this.name;
    }
    public String getNumber() {
        return this.number;
    }
    public String getHometown() {
        return this.hometown;
    }
    public double getLatitude() { return this.latitude; }
    public double getLongitude() { return this.longitude; }

    //Mutators
    public void setNumber(String number) {
        this.number = number;
    }
}