package com.example.christopher.myapplication;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by Christopher on 11/11/2016.
 * Code from http://stackoverflow.com/questions/14478179/background-service-with-location-listener-in-android
 */

public class SendLocationService extends Service {
    public static String BROADCAST_MYLOCATION = "com.MyApplication.MY_LOCATION";
    public static String BROADCAST_SENDLOCATIONRESPONSE = "com.MyApplication.SEND_LOCATION_RESPONSE";
    LocationManager locationManager;
    LocationListener listener;

    SharedPreferences sessionInfo;

    @Override
    public void onCreate() {
        sessionInfo = getSharedPreferences("sessionInfo", 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try{
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            listener = new MyLocationListener();
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, true);
            locationManager.requestLocationUpdates(bestProvider, ApplicationConstants.SEND_LOCATION_FREQUENCY_MS, 0, listener);
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
            Intent intent = new Intent(BROADCAST_MYLOCATION);
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

    
    private class SendLocationTask extends AsyncTask<Location, Integer, Intent>{
        @Override
        protected Intent doInBackground(Location... params) {
            Intent intent = new Intent(BROADCAST_SENDLOCATIONRESPONSE);
            Location location = params[0];
            try{
                Log.d("DEBUG", "START SEND LOCATION TASK");
                InetAddress address = InetAddress.getByName("csclserver.hopto.org");
                Socket mySocket = new Socket();
                mySocket.setSoTimeout(ApplicationConstants.SERVER_TIMEOUT_MS);
                mySocket.connect(new InetSocketAddress(address, 50001), ApplicationConstants.SERVER_TIMEOUT_MS);
                PrintWriter printWriter = new PrintWriter(mySocket.getOutputStream(), true);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
                JSONObject jsonMessage = new JSONObject();
                JSONObject jsonLocation = new JSONObject();
                jsonMessage.put("type", "SEND_LOCATION");
                jsonLocation.put("latitude", location.getLatitude());
                jsonLocation.put("longitude", location.getLongitude());
                jsonMessage.put("location", jsonLocation);
                jsonMessage.put("sessionid", sessionInfo.getString("sessionid", ""));
                printWriter.println(jsonMessage.toString());
                String serverResponse = bufferedReader.readLine();
                intent.putExtra("response", serverResponse);
                intent.putExtra("connected", true);
                mySocket.close();
            }
            catch (SocketTimeoutException e){
                Log.d("DEBUG", "TIMEOUT");
                intent.putExtra("connected", false);
                intent.putExtra("response", "none");
                e.printStackTrace();
            }
            catch (UnknownHostException e){
                Log.d("DEBUG", "UNKNOWN HOST");
                intent.putExtra("connected", false);
                intent.putExtra("response", "none");
                e.printStackTrace();
            }
            catch (IOException e){
                Log.d("DEBUG", "IOERROR");
                intent.putExtra("connected", false);
                intent.putExtra("response", "none");
                e.printStackTrace();
            }
            catch (JSONException e){
                Log.d("DEBUG", "JSONBS");
                intent.putExtra("connected", false);
                intent.putExtra("response", "none");
                e.printStackTrace();
            }
            catch (Exception e){
                Log.d("DEBUG", "REGULAR");
                intent.putExtra("connected", false);
                intent.putExtra("response", "none");
                e.printStackTrace();
            }
            finally {
                Log.d("DEBUG", "DONE DOINBACKGROUND");
                return intent;
            }
        }

        @Override
        protected void onPostExecute(Intent intent) {
            sendBroadcast(intent);
        }
    }
}
