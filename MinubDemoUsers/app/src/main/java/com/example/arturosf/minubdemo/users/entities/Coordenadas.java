package com.example.arturosf.minubdemo.users.entities;

/**
 * Created by ArturoSF on 11/12/17.
 */

public class Coordenadas {
    double latitude;
    double longitude;

    public Coordenadas(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Coordenadas() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
