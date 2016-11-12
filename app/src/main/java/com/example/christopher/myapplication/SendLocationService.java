package com.example.christopher.myapplication;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Christopher on 11/11/2016.
 * Code from http://stackoverflow.com/questions/14478179/background-service-with-location-listener-in-android
 */

public class SendLocationService extends Service {
    public static String BROADCAST_ACTION = "com.MyApplication.MY_LOCATION";
    LocationManager locationManager;
    LocationListener listener;
    Intent intent;

    @Override
    public void onCreate() {
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try{
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            listener = new MyLocationListener();
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, true);
            locationManager.requestLocationUpdates(bestProvider, 10000, 0, listener);
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
        finally {
            return START_STICKY;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            locationManager.removeUpdates(listener);
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
    }

    private class MyLocationListener implements LocationListener{
        @Override
        public void onLocationChanged(final Location location) {
            intent.putExtra("latitude", location.getLatitude());
            intent.putExtra("longitude", location.getLongitude());
            sendBroadcast(intent);

            SendLocationTask sendLocationTask = new SendLocationTask();
            sendLocationTask.execute(location);
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }

    private class SendLocationTask extends AsyncTask<Location, Integer, String>{
        @Override
        protected String doInBackground(Location... params) {
            Location location = params[0];
            try{
                InetAddress address = java.net.InetAddress.getByName("csclserver.hopto.org");
                Socket mySocket = new Socket(address, 50001);
                PrintWriter printWriter = new PrintWriter(mySocket.getOutputStream(), true);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
                JSONObject jsonMessage = new JSONObject();
                JSONObject jsonLocation = new JSONObject();
                jsonMessage.put("type", "SEND_LOCATION");
                jsonLocation.put("latitude", location.getLatitude());
                jsonLocation.put("longitude", location.getLongitude());
                jsonMessage.put("location", jsonLocation);
                printWriter.println(jsonMessage.toString());
                String serverResponse = bufferedReader.readLine();
                intent.putExtra("response", serverResponse);
                mySocket.close();
            }
            catch (UnknownHostException e){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            finally {
                return null;
            }
        }
    }
}
