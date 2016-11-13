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



// For sliding menu
/**
 * 네비게이션 드로어 적용
 *
 * 이창우
 */
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;




public class Map extends AppCompatActivity implements LocationListener, OnMapReadyCallback, OnClickListener, OnLongClickListener, OnMarkerClickListener{
    GoogleMap myMap;
    LatLng lastLocation;
    Marker lastLocationMarker;
    Marker destinationMarker;

    boolean firstMapUpdate;

    // Sliding menu
    private final String[] navItems = {"Brown", "Blue", "Green", "Orange", "Golden"};

    private ListView lvNavList;

    private FrameLayout flContainer;

    private DrawerLayout dlDrawer;

    private Button btn;
    // sliding menu done


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


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else{
            setUpMap();
        }



        // 슬라이딩 여기 밑에서 원랜 슬라이드 메뉴가 적혀있었음.
        //setContentView(R.layout.activity_slide_menu);
        //setContentView(R.layout.activity_map);
        lvNavList = (ListView)findViewById(R.id.lv_activity_main_nav_list);
        flContainer = (FrameLayout)findViewById(R.id.fl_activity_main_container);
        btn = (Button)findViewById(R.id.btn);

        // 여기 코멘트 처리 하는거로 일단 맵이 보이긴 하는데(에러없이)... 아놔 모르겠다.
/**        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "open", Toast.LENGTH_SHORT).show();
                dlDrawer.openDrawer(lvNavList);
            }
        });
**/
        //dlDrawer = (DrawerLayout)findViewById(R.id.dl_activity_main_drawer);
        dlDrawer = (DrawerLayout)findViewById(R.id.activity_map);
        lvNavList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navItems));
        lvNavList.setOnItemClickListener(new DrawerItemClickListener());
        // 슬라이딩 끝

    }


    //슬라이딩
    @Override
    public void onBackPressed() {
        if (dlDrawer.isDrawerOpen(lvNavList)) {
            dlDrawer.closeDrawer(lvNavList);
        } else {
            super.onBackPressed();
        }
    }
    //슬라이딩 끝

    //슬라이딩
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
            switch (position) {
                case 0:
                    flContainer.setBackgroundColor(Color.parseColor("#A52A2A"));
                    break;
                case 1:
                    flContainer.setBackgroundColor(Color.parseColor("#5F9EA0"));
                    break;
                case 2:
                    flContainer.setBackgroundColor(Color.parseColor("#556B2F"));
                    break;
                case 3:
                    flContainer.setBackgroundColor(Color.parseColor("#FF8C00"));
                    break;
                case 4:
                    flContainer.setBackgroundColor(Color.parseColor("#DAA520"));
                    break;
            }
            dlDrawer.closeDrawer(lvNavList);

        }
    }
    //슬라이딩 끝



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
                polylineOptions.width(2);
                polylineOptions.color(Color.CYAN);
                myMap.addPolyline(polylineOptions);
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
