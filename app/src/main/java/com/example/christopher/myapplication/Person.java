package com.example.christopher.myapplication;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Christopher on 11/12/2016.
 */

public class Person {
    private String name;
    private LatLng location;
    private String deviceID;
    private boolean isOnline;
    private boolean isVisible;
    private String areaName;
    public Person(String n, LatLng l, String id){name = n; location = l; deviceID = id; isOnline = false; areaName = ""; }
    public Person(String name, String id) {this.name = name; this.deviceID = id; isOnline = false; areaName = ""; }

    public LatLng getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public boolean isSamePerson(Person comp){
        if (comp != null){
            Log.d("DEBUG", "This ID: " + deviceID + " | Compare ID: " + comp.deviceID);
            return deviceID == comp.deviceID;
        }
        else{
            return false;
        }
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public boolean samePerson(Person prsn){
       return prsn.deviceID.equals(deviceID);
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean vs){
        isVisible = vs;
    }

    public String getAreaName(){
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public void setLocation(LatLng loc){location = loc;}

}
