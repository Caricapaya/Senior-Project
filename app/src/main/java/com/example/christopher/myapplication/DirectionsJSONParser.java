package com.example.christopher.myapplication;

import android.util.Log;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Christopher on 10/17/2016.
 *
 * //Code from:
 * //http://stackoverflow.com/questions/14710744/how-to-draw-road-directions-between-two-geocodes-in-android-google-map-v2
 */

class DirectionsJSONParser {
    List<List<HashMap<String, String>>> parse(JSONObject jData ){
        List<List<HashMap<String, String>>> parsedPaths = new ArrayList<>();
        JSONArray jPaths;
        JSONArray jLegs;
        JSONArray jSteps;

        try{
            jPaths = jData.getJSONArray("routes");
            Log.d("TEST",jData.toString(4));
            Log.d("TEST", "HELLO3");

            for (int i = 0; i < jPaths.length(); i++) {
                jLegs = ((JSONObject) jPaths.get(i)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<>();

                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyLine;
                        polyLine = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> pointList = decodePoly(polyLine);

                        for (int l = 0; l < pointList.size(); l++) {
                            HashMap<String, String> temp = new HashMap<>();
                            temp.put("lat", Double.toString(pointList.get(l).latitude)); //Test
                            temp.put("lng", Double.toString(pointList.get(l).longitude));
                            path.add(temp);
                        }
                    }
                }

                parsedPaths.add(path);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return parsedPaths;
    }

    //Format for encoding:
    //https://developers.google.com/maps/documentation/utilities/polylinealgorithm
    private List<LatLng> decodePoly(String encodedPolyLine){
        List<LatLng> polyline = new ArrayList<>();
        int index = 0;
        int length = encodedPolyLine.length();
        int lat = 0;
        int lng = 0;

        //offset lat and lng for each iteration. lat and lng are initialized to 0, so for the first iteration, point = offset.
        while (index < length){
            int b;
            int shift = 0;
            int result = 0;
            do{
                b = encodedPolyLine.charAt(index++) - 0x3F; //0x3F = 63 = '?'
                result |= (b & 0x1f) << shift;
                shift +=5;
            }while (b >= 0x20);
            int latOffset = ((result & 1) == 1 ? ~(result >> 1) : (result >> 1));
            lat += latOffset;

            shift = 0;
            result = 0;
            do{
                b = encodedPolyLine.charAt(index++) - 0x3F; //0x3F = 63 = '?'
                result |= (b & 0x1f) << shift;
                shift +=5;
            }while (b >= 0x20);
            int lngOffset = ((result & 1) == 1 ? ~(result >> 1) : (result >> 1));
            lng += lngOffset;

            LatLng point = new LatLng((double) lat/1E5, (double) lng/1E5);
            polyline.add(point);
        }
        return polyline;
    }


}
