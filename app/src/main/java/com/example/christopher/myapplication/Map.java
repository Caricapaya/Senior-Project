package com.example.christopher.myapplication;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnClickListener;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.util.List;


public class Map extends AppCompatActivity implements LocationListener, OnMapReadyCallback, OnClickListener, OnLongClickListener, OnMarkerClickListener{
    GoogleMap myMap;
    LatLng lastLocation;
    Marker lastLocationMarker;
    Marker destinationMarker;
    List<Marker> otherMarkers;
    Polyline destinationRoute;

    boolean firstMapUpdate;

    NetworkIO networkThread;
    Handler UIHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastLocationMarker = null;
        destinationMarker = null;
        destinationRoute = null;
        firstMapUpdate = true;
        otherMarkers = new ArrayList<>();

        if (ConnectionResult.SUCCESS != GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)){
            GoogleApiAvailability.getInstance().getErrorDialog(this, GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this), 0).show();
            finish();
        }
        setContentView( R.layout.activity_map );

        TextView txtView = (TextView) findViewById(R.id.locationLabel);
        txtView.setOnClickListener(this);
        txtView.setOnLongClickListener(this);

        initializeIOThread();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else{
            setUpMap();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    setUpMap();
                }
                else {
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        try{
            myMap = map;
            myMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            myMap.setMyLocationEnabled(true);
            myMap.setIndoorEnabled(true);
            myMap.getUiSettings().setZoomControlsEnabled(true);
            myMap.getUiSettings().setMapToolbarEnabled(false);
            myMap.setOnMarkerClickListener(this);

            LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = locManager.getBestProvider(criteria, true);
            Location location = locManager.getLastKnownLocation(bestProvider);

            if (location != null){
                onLocationChanged(location);
            }
            locManager.requestLocationUpdates(bestProvider, 10000, 0, this);
        }
        catch (SecurityException e){
            Log.d("Permission error", e.getMessage());
            e.printStackTrace();
        }
    }

    public void setUpMap(){
        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        mapFrag.getMapAsync(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (lastLocationMarker != null) {
            lastLocationMarker.remove();
        }
        TextView localeTV = (TextView) findViewById(R.id.locationLabel);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        LatLng coordinates = lastLocation = new LatLng(latitude, longitude);
        MarkerOptions myMarker = new MarkerOptions().position(coordinates).title("We're Here!").snippet("IDK what this snippet is");
        lastLocationMarker = myMap.addMarker(myMarker);

        if (firstMapUpdate){
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
            myMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
            firstMapUpdate = false;
        }
        localeTV.setText("Longitude: " + longitude + "\nLatitude: " + latitude);

        Message sendLocation = Message.obtain();
        sendLocation.what = NetworkIO.Type.SEND_LOCATION.ordinal();
        sendLocation.obj = lastLocation;
        networkThread.IOHandler.sendMessage(sendLocation);

        Message updateMarkers = Message.obtain();
        updateMarkers.what = NetworkIO.Type.GET_LOCATIONS.ordinal();
        networkThread.IOHandler.sendMessage(updateMarkers);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        final Context context = this;
        if (marker.equals(destinationMarker)){
            AlertDialog.Builder optionDialog = new AlertDialog.Builder(this);
            optionDialog.setTitle("Options!");
            optionDialog.setMessage("Change this marker's icon, or draw a route here");

            optionDialog.setNegativeButton("Change Icon", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        ImageGetter dlManager = new ImageGetter();
                        dlManager.execute("http://people.aero.und.edu/~csantana/260/1/12041326_10153226440607635_1791570777_o.jpg", destinationMarker);
                    } catch (Exception e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Can't download and/or show image");
                        builder.setTitle("Error!");
                        builder.create().show();
                        e.printStackTrace();
                    }
                }
            });
            optionDialog.setPositiveButton("Get Route", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        PathGetter pathGetter = new PathGetter();
                        String requestURL = buildRequestURL(lastLocationMarker.getPosition(), marker.getPosition());
                        pathGetter.execute(requestURL);
                    } catch (Exception e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Can't download and/or draw path");
                        builder.setTitle("Error!");
                        builder.create().show();
                        e.printStackTrace();
                    }
                }
            });

            optionDialog.show();
            return true;
        }
        return false;
    }

    private String buildRequestURL(LatLng start, LatLng dest){
        String origin = "origin=" + start.latitude + "," + start.longitude;
        String destination = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = origin + "&" + destination + "&" + sensor;
        return "https://maps.googleapis.com/maps/api/directions/json?" + parameters;
    }



    private class PathGetter extends AsyncTask<String, Integer, String>{
        @Override
        protected String doInBackground(String... params) {
            String pathsData = "";
            try {
                pathsData = downloadFromURL(params[0]);
            }
            catch (Exception e){
                Log.d("Error in PathGetter", e.getMessage());
                e.printStackTrace();
            }
            return pathsData;
        }

        private String downloadFromURL(String myurl) throws IOException{
            String retData = "";
            BufferedReader inputBuffer;
            HttpURLConnection connection = null;
            try{
                connection = (HttpURLConnection) new URL(myurl).openConnection();
                connection.connect();
                inputBuffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder mysb = new StringBuilder();
                String line = "";
                while ((line = inputBuffer.readLine()) != null){
                    mysb.append(line);
                }
                retData = mysb.toString();
                inputBuffer.close();
            }
            catch (Exception e){
                Log.d("downloadFromURL error ", e.getMessage());
                e.printStackTrace();
            }
            finally {
                connection.getInputStream().close();
                connection.disconnect();
            }

            return retData;
        }

        @Override
        protected void onPostExecute(String s) {
            PathDecoder pathDecoder = new PathDecoder();
            pathDecoder.execute(s);
        }
    }

    private class PathDecoder extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>>{
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... params) {
            JSONObject jsonObject;
            List<List<HashMap<String, String>>> paths = null;
            try{
                jsonObject = new JSONObject(params[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                paths = parser.parse(jsonObject);
            }
            catch(Exception e){
                Log.d("Error in PathDecoder", e.getMessage());
                e.printStackTrace();
            }
            return paths;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            ArrayList<LatLng> points = null;
            PolylineOptions polylineOptions = null;

            for (int i = 0; i < lists.size(); i++) {
                points = new ArrayList<LatLng>();
                polylineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = lists.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                polylineOptions.addAll(points);
                polylineOptions.width(5);
                polylineOptions.color(Color.BLUE);
                destinationRoute = myMap.addPolyline(polylineOptions);
            }
        }
    }

    private class MarkerWrapper{
        Marker oldMarker;
        Bitmap fetchedImage;
        MarkerWrapper(Marker re, Bitmap bm){
            oldMarker = re;
            fetchedImage = bm;
        }

        Marker getOldMarker() {
            return oldMarker;
        }

        Bitmap getFetchedImage() {
            return fetchedImage;
        }
    }

    private class ImageGetter extends AsyncTask<Object, Integer, MarkerWrapper>{
        @Override
        protected MarkerWrapper doInBackground(Object... params) {
            try{
                Bitmap newIMG = BitmapFactory.decodeStream(new URL((String) params[0]).openConnection().getInputStream());
                Bitmap scaledIMG = Bitmap.createScaledBitmap(newIMG, 120, 120, false);
                newIMG.recycle();
                Marker marker = (Marker) params[1];
                return new MarkerWrapper(marker, scaledIMG);

            }
            catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(MarkerWrapper wrapper) {
            super.onPostExecute(wrapper);
            Marker marker = wrapper.getOldMarker();
            Bitmap newImage = wrapper.getFetchedImage();
            MarkerOptions replacementMarker = new MarkerOptions();
            replacementMarker.title(marker.getTitle()).snippet(marker.getSnippet()).position(marker.getPosition()).icon(BitmapDescriptorFactory.fromBitmap(newImage));
            marker.remove();

            destinationMarker = myMap.addMarker(replacementMarker);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.locationLabel: {
                if (destinationMarker != null){
                    destinationMarker.remove();
                }
                if (destinationRoute != null){
                    destinationRoute.remove();
                    destinationRoute = null;
                }
                Random rand = new Random();
                double longitude = lastLocation.longitude + (rand.nextDouble() / 50.0) - 0.01;
                double latitude = lastLocation.latitude + (rand.nextDouble() / 50.0) - 0.01;
                MarkerOptions destinationMarkerOptions = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Destination").snippet("Randomly selected destination").icon(BitmapDescriptorFactory.fromResource(R.drawable.target_marker));
                destinationMarker = myMap.addMarker(destinationMarkerOptions);
                return true;
        }
            default: {
                return false;
            }
        }
    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.locationLabel: {
                /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("No Functionality");
                builder.setTitle("Error?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();*/
                Message myMsg = Message.obtain();
                myMsg.what = NetworkIO.Type.SEND_LOCATION.ordinal();
                myMsg.obj = lastLocation;
                networkThread.IOHandler.sendMessage(myMsg);
            }
        }
    }

    public void initializeIOThread(){
        UIHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                //super.handleMessage(msg);
                NetworkIO.Type type = NetworkIO.Type.values()[msg.what];
                TextView txtView = (TextView) findViewById(R.id.locationLabel);
                switch (type){
                    case STRING_RESPONSE:
                        txtView.setText((String) msg.obj);
                        break;
                    case CONNECTION_STATUS:
                        txtView.setText((String) msg.obj);
                        break;
                    case SEND_LOCATION:
                        txtView.setText((String) msg.obj);
                        break;
                    case DISCONNECT:
                        txtView.setText((String) msg.obj);
                        break;
                    case GET_LOCATIONS:
                        updateLocations((String) msg.obj);
                }
            }
        };

        networkThread = new NetworkIO( UIHandler);
        networkThread.start();
    }

    private void updateLocations(String unparsed){
        for (Marker m : otherMarkers){
            m.remove();
        }
        for (String locationHash : unparsed.split("&")){
            if (locationHash.equals("NULL")){
                break;
            }
            String deviceID = locationHash.split(":")[0];
            Double latitude = Double.parseDouble(locationHash.split(":")[1].split(",")[0]);
            Double longitude = Double.parseDouble(locationHash.split(":")[1].split(",")[1]);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(deviceID);
            markerOptions.position(new LatLng(latitude, longitude));
            otherMarkers.add(myMap.addMarker(markerOptions));
        }
    }


}
