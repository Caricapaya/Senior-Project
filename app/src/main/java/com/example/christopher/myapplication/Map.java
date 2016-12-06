package com.example.christopher.myapplication;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnClickListener;
import android.widget.Toast;


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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
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
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class Map extends AppCompatActivity implements OnMapReadyCallback, OnClickListener, OnLongClickListener, OnMarkerClickListener{
    GoogleMap myMap;
    LatLng lastLocation;
    Marker lastLocationMarker;
    Marker destinationMarker;
    Marker targetFriendMarker;
    HashMap<Marker, Person> markerToPerson;
    List<Marker> otherMarkers;
    List<Marker> friendMarkers;
    List<Person> friendLocations;
    Polyline destinationRoute;

    boolean friendLocationsUpdate;
    boolean firstMapUpdate;

    LocationReceiver receiver;
    Handler getLocationSheduler;
    TimedLocationGetter getLocationRoutine;

    SharedPreferences sessionInfo;
    // Sliding menu
    private final String[] navItems = {"1", "2", "3", "4", "5", "Sign Out"};
    private final String[] navItems2 = {"6", "7", "8", "9", "10","6", "7", "8", "9", "10","6", "7", "8", "9", "10"};
    private final String[] resultTest = {"CARL","CARL","CARL","CARL","CARL","CARL","CARL","CARL","CARL","CARL"};

    private ListView lvNavList;
    private ListView lvNavList2;
    private ListView searchResultList;
    private LinearLayout friendsNavigation;

    //private FrameLayout flContainer;
    private RelativeLayout flContainer;

    private DrawerLayout dlDrawer;
    // sliding menu done


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastLocationMarker = null;
        destinationMarker = null;
        targetFriendMarker = null;
        destinationRoute = null;
        firstMapUpdate = true;
        friendLocationsUpdate = false;
        otherMarkers = new ArrayList<>();
        friendMarkers = new ArrayList<>();
        markerToPerson = new HashMap<>();

        sessionInfo = getSharedPreferences("sessionInfo", 0);

        if (ConnectionResult.SUCCESS != GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)){
            GoogleApiAvailability.getInstance().getErrorDialog(this, GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this), 0).show();
            finish();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        setContentView( R.layout.activity_map );

        TextView txtView = (TextView) findViewById(R.id.locationLabel);
        txtView.setOnClickListener(this);
        //txtView.setOnLongClickListener(this);

        //initializeIOThread();

        IntentFilter filter = new IntentFilter();
        filter.addAction(SendLocationService.BROADCAST_MYLOCATION);
        filter.addAction(SendLocationService.BROADCAST_SENDLOCATIONRESPONSE);
        /*receiver = new LocationReceiver();
        registerReceiver(receiver, filter);*/

        startService(new Intent(this, SendLocationService.class));
        getLocationSheduler = new Handler();

        setUpMap();


        // 슬라이딩 여기 밑에서 원랜 슬라이드 메뉴가 적혀있었음.
        //setContentView(R.layout.activity_slide_menu);
        //setContentView(R.layout.activity_map);
        lvNavList = (ListView)findViewById(R.id.lv_activity_main_nav_list_start);
        lvNavList2 = (ListView)findViewById(R.id.listOfFriends);
        searchResultList = (ListView)findViewById(R.id.searchResults);

        flContainer = (RelativeLayout) findViewById(R.id.fl_activity_main_container);
        friendsNavigation = (LinearLayout) findViewById(R.id.friendsDrawer);
        //flContainer = (FrameLayout)findViewById(R.id.googleMap);

        //btn = (Button)findViewById(R.id.btn); //11/28 필요없는 것 같아서 일단 주석처리

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
        lvNavList2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navItems2));
        lvNavList2.setOnItemClickListener(new DrawerItemClickListener2());
        searchResultList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, resultTest));

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
                case 5:
                    signOut();
                    break;
            }
            dlDrawer.closeDrawer(lvNavList); // 이게 클릭하면 그냥 드로워를 다시 집어넣는 역할

        }
    }

    private void signOut(){
        stopService(new Intent(this, SendLocationService.class));
        new SignOutOfServerTask().execute();
        startActivity(new Intent(this, Login.class));
    }

    private class SignOutOfServerTask extends AsyncTask<String, Integer, String>{
        @Override
        protected String doInBackground(String... params) {
            Socket mySocket;
            try{
                InetAddress address = InetAddress.getByName("csclserver.hopto.org");
                mySocket = new Socket();
                mySocket.setSoTimeout(2000);
                mySocket.connect(new InetSocketAddress(address, 50001),2000);
                PrintWriter printWriter = new PrintWriter(mySocket.getOutputStream(), true);

                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("type", NetworkConstants.TYPE_QUIT);
                jsonMessage.put("sessionid", sessionInfo.getString("sessionid", ""));
                printWriter.println(jsonMessage);
                mySocket.close();
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            catch (UnknownHostException e){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            finally {
                return null;
            }
        }
    }


    private class DrawerItemClickListener2 implements ListView.OnItemClickListener {

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
            dlDrawer.closeDrawer(lvNavList2);

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
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(47.922308, -97.071289), 16));

            /*LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = locManager.getBestProvider(criteria, true);
            Location location = locManager.getLastKnownLocation(bestProvider);

            if (location != null){
                onLocationChanged(location);
            }
            locManager.requestLocationUpdates(bestProvider, 10000, 0, this);*/
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

    //Handles messages (intents) about this device's location and the relevant server response
    private class LocationReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SendLocationService.BROADCAST_MYLOCATION)) {
                if (myMap == null) {
                    return;
                }
                if (lastLocationMarker != null) {
                    lastLocationMarker.remove();
                }
                double longitude = intent.getDoubleExtra("longitude", 0);
                double latitude = intent.getDoubleExtra("latitude", 0);
                LatLng coordinates = lastLocation = new LatLng(latitude, longitude);
                MarkerOptions myMarker = new MarkerOptions().position(coordinates).title("We're Here!");
                lastLocationMarker = myMap.addMarker(myMarker);

                if (firstMapUpdate) {
                    myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 17), 2000, null);
                    firstMapUpdate = false;
                }


                if (friendLocationsUpdate){
                    friendLocationsUpdate = false;
                    Person currtarget = markerToPerson.get(targetFriendMarker);
                    if (currtarget == null){
                        Log.d("debug", "target is null");
                    }
                    else{
                        Log.d("debug", "target has value");
                    }
                    for (Marker m : friendMarkers){
                        m.remove();
                    }
                    markerToPerson.clear();
                    targetFriendMarker = null;
                    if (friendLocations != null)
                    {
                        if (currtarget == null){
                            Log.d("debug", "target is now null");
                        }
                        else{
                            Log.d("debug", "target now has value");
                        }
                        for (Person person : friendLocations){
                            String deviceID = person.getDeviceID();
                            String name = person.getName();
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.title(name);
                            markerOptions.snippet("Device ID: " + deviceID);
                            markerOptions.position(person.getLocation());
                            Marker tempMarker = myMap.addMarker(markerOptions);
                            friendMarkers.add(tempMarker);
                            markerToPerson.put(tempMarker, person);
                            if (person.isSamePerson(currtarget)){
                                targetFriendMarker = tempMarker;
                                Log.d("DEBUG", "Issametarget");
                            }
                            else{
                                Log.d("DEBUG", "Isnotsametarget");
                            }
                        }
                    }
                }
                if (targetFriendMarker != null){
                    try {
                        PathGetter pathGetter = new PathGetter();
                        String requestURL = buildRequestURL(lastLocationMarker.getPosition(), targetFriendMarker.getPosition());
                        pathGetter.execute(requestURL);
                    } catch (Exception e) {
                        targetFriendMarker = null;
                        Toast.makeText(getApplicationContext(),"Error: can't draw path", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                else{
                    if (destinationRoute != null){
                        destinationRoute.remove();
                    }
                }
            }
            else if (intent.getAction().equals(SendLocationService.BROADCAST_SENDLOCATIONRESPONSE)) {
                checkSessionResponse(intent.getStringExtra("response"));

                TextView locationLabel = (TextView) findViewById(R.id.locationLabel);
                if (intent.getBooleanExtra("connected", true)) {
                    locationLabel.setBackgroundColor(0xFF30E852);
                } else {
                    locationLabel.setBackgroundColor(Color.RED);
                }
            }
        }
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
        if (friendMarkers.contains(marker)){
            if (marker.equals(targetFriendMarker)){
                AlertDialog.Builder optionDialog = new AlertDialog.Builder(this);
                optionDialog.setTitle("Target Friend");
                optionDialog.setMessage("Stop drawing path?");

                optionDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });

                optionDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        destinationRoute.remove();
                        targetFriendMarker = null;
                    }
                });

                optionDialog.show();
                return true;
            }
            else{
                AlertDialog.Builder optionDialog = new AlertDialog.Builder(this);
                optionDialog.setTitle("Friend");
                optionDialog.setMessage("Draw path to this friend?");

                optionDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });

                optionDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            PathGetter pathGetter = new PathGetter();
                            String requestURL = buildRequestURL(lastLocationMarker.getPosition(), marker.getPosition());
                            pathGetter.execute(requestURL);
                            targetFriendMarker = marker;
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
                JSONParser parser = new JSONParser();
                paths = parser.parsePaths(jsonObject);
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
                if (destinationRoute != null){
                    destinationRoute.remove();
                }
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
                TextView locationLabel = (TextView) findViewById(R.id.locationLabel);
                String sessiontxt = "SESSION: " + sessionInfo.getString("sessionid", "none found") + "\n";
                String nametxt = "NAME: " + sessionInfo.getString("firstname", "");
                nametxt += sessionInfo.getString("middlename", "");
                nametxt += sessionInfo.getString("lastname", "");
                locationLabel.setText(sessiontxt + nametxt);
            }
            case R.id.searchButton: {
                EditText searchBox = (EditText) findViewById(R.id.searchEditText);
                String searchQuery = searchBox.getText().toString();
            }
        }
    }

    private class TimedLocationGetter implements Runnable{
        private volatile boolean isCancelled;
        public TimedLocationGetter(){
            isCancelled = false;
        }

        @Override
        public void run() {
            try{
                if (isCancelled){
                    return;
                }
                GetLocationsTask getLocationsTask = new GetLocationsTask();
                getLocationsTask.execute("");
            }
            finally {
                if (isCancelled){
                    return;
                }
                 getLocationSheduler.postDelayed(this, 5000);
            }
        }

        public void cancel(){
            isCancelled = true;
        }
    }


    //TODO implement realtime pathfinder
    private class GetLocationsTask extends AsyncTask<String, Integer, List<Person>>{
        JSONObject response;

        @Override
        protected List<Person> doInBackground(String... params) {
            Socket mySocket;
            List<Person> people = null;
            try{
                InetAddress address = InetAddress.getByName("csclserver.hopto.org");
                mySocket = new Socket();
                mySocket.setSoTimeout(2000);
                mySocket.connect(new InetSocketAddress(address, 50001),2000);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
                PrintWriter printWriter = new PrintWriter(mySocket.getOutputStream(), true);

                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("type", NetworkConstants.TYPE_GET_LOCATIONS);
                jsonMessage.put("sessionid", sessionInfo.getString("sessionid", ""));
                printWriter.println(jsonMessage);
                String unparsed = bufferedReader.readLine();
                mySocket.close();

                JSONParser parser = new JSONParser();
                response = new JSONObject(unparsed);
                people = parser.parseLocations(response);

            }
            catch (JSONException e){
                e.printStackTrace();
            }
            catch (UnknownHostException e){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            finally {
                Log.d("DEBUG", "END GET LOCATIONS TASK");
                return people;
            }
        };

        //TODO update current location and other markers at the same time
        @Override
        protected void onPostExecute(List<Person> persons) {
            checkSessionResponse(response);
            friendLocationsUpdate = true;
            if (persons.size() > 0){
                friendLocations = persons;
            }
            else{
                friendLocations = null;
            }
            /*for (Marker m : otherMarkers){
                m.remove();
            }
            if (persons == null){
                return;
            }
            for (Person person : persons){
                String deviceID = person.getDeviceID();
                String name = person.getName();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(name);
                markerOptions.snippet("Device ID: " + deviceID);
                markerOptions.position(person.getLocation());
                otherMarkers.add(myMap.addMarker(markerOptions));
            }*/
        }
    }

    public void checkSessionResponse(JSONObject serverMessage){
        if (serverMessage!= null){
            try{
                String sessionStatus = serverMessage.getString("sessionstatus");
                if (sessionStatus.equals("invalid")){
                    Bundle bundle = new Bundle();
                    bundle.putString("startuptoast", "Unknown session identifier");
                    //Toast.makeText(getApplicationContext(), "Unknown session identifier", Toast.LENGTH_SHORT);
                    stopService(new Intent(this, SendLocationService.class));
                    startActivity(new Intent(getApplicationContext(), Login.class).putExtras(bundle));
                }
                else if (sessionStatus.equals("timeout")){
                    Bundle bundle = new Bundle();
                    bundle.putString("startuptoast", "Session timed out, please log in again");
                    //Toast.makeText(getApplicationContext(), "Session timed out, please log in again", Toast.LENGTH_SHORT);
                    stopService(new Intent(this, SendLocationService.class));
                    startActivity(new Intent(getApplicationContext(), Login.class).putExtras(bundle));
                }
                else if (sessionStatus.equals("update")){
                    SharedPreferences.Editor sessionEditor = sessionInfo.edit();
                    String newSessionID = serverMessage.getString("sessionid");
                    sessionEditor.putString("sessionid", newSessionID);
                    sessionEditor.commit();
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkSessionResponse(String serverMessage){
        if (serverMessage == null){
            return;
        }
        try{
            checkSessionResponse(new JSONObject(serverMessage));
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        getLocationSheduler.removeCallbacks(getLocationRoutine);
        getLocationRoutine.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SendLocationService.BROADCAST_MYLOCATION);
        filter.addAction(SendLocationService.BROADCAST_SENDLOCATIONRESPONSE);
        receiver = new LocationReceiver();
        registerReceiver(receiver, filter);

        getLocationRoutine = new TimedLocationGetter();
        getLocationSheduler.postDelayed(getLocationRoutine, 0);
    }
}
