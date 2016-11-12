package com.example.christopher.myapplication;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Christopher on 11/12/2016.
 */

public class Person {
    private String name;
    private LatLng location;
    private String deviceID;
    public Person(String n, LatLng l, String id){name = n; location = l; deviceID = id;}

    public LatLng getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getDeviceID() {
        return deviceID;
    }
}
