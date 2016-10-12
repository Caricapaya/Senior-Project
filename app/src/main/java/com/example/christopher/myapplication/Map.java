package com.example.christopher.myapplication;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.util.Random;


public class Map extends AppCompatActivity implements LocationListener, OnMapReadyCallback, OnClickListener, OnLongClickListener, OnMarkerClickListener{
    GoogleMap myMap;
    LatLng lastLocation;
    Marker lastLocationMarker;
    Marker destinationMarker;

    boolean firstMapUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastLocationMarker = null;
        destinationMarker = null;
        firstMapUpdate = true;

        if (ConnectionResult.SUCCESS != GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)){
            GoogleApiAvailability.getInstance().getErrorDialog(this, GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this), 0).show();
            finish();
        }
        setContentView( R.layout.activity_map );

        TextView txtView = (TextView) findViewById(R.id.locationLabel);
        txtView.setOnClickListener(this);
        txtView.setOnLongClickListener(this);


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
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(destinationMarker)){
            try {
                ImageGetter dlManager = new ImageGetter();
                dlManager.execute("http://people.aero.und.edu/~csantana/260/1/12041326_10153226440607635_1791570777_o.jpg", destinationMarker);
            } catch (Exception e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Can't download and/or show image");
                builder.setTitle("Error!");
                builder.create().show();
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    private class MarkerWrapper{
        Marker oldMarker;
        Bitmap fetchedImage;
        public MarkerWrapper(Marker re, Bitmap bm){
            oldMarker = re;
            fetchedImage = bm;
        }

        public Marker getOldMarker() {
            return oldMarker;
        }

        public Bitmap getFetchedImage() {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("No Functionality");
                builder.setTitle("Error?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
            }
        }
    }
}
