package com.example.christopher.myapplication;

/**
 * Created by Christopher on 2/14/2017.
 */

public class ApplicationConstants {
    public static final int SEND_LOCATION_FREQUENCY = 5; //seconds
    public static final int SEND_LOCATION_FREQUENCY_MS = SEND_LOCATION_FREQUENCY*1000;
    public static final int GET_LOCATIONS_FREQUENCY = 5;
    public static final int GET_LOCATIONS_FREQUENCY_MS = GET_LOCATIONS_FREQUENCY*1000;
    public static final int GET_REQUESTS_FREQUENCY = 30; //will round up to multiple of get locations frequency
    public static final int GET_REQUESTS_FREQUENCY_MS = GET_REQUESTS_FREQUENCY*1000;
    public static final int SERVER_TIMEOUT = 2;
    public static final int SERVER_TIMEOUT_MS = SERVER_TIMEOUT*1000;

}
