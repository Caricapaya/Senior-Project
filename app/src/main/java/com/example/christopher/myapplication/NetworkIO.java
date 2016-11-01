package com.example.christopher.myapplication;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Christopher on 10/21/2016.
 */

public class NetworkIO extends Thread {
    public Handler IOHandler;
    private Handler responseHandler;
    private Socket mySocket;
    private boolean isConnected;
    private String serverSays;

    private BufferedReader bufferedReader;
    private PrintWriter printWriter;


    public NetworkIO(Handler respH){
        responseHandler = respH;
        bufferedReader = null;
        printWriter = null;
    }

    public void run(){
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }

        IOHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //super.handleMessage(msg);
                Type whatType = Type.values()[msg.what];
                Message response;
                String serverResponse;
                JSONObject jsonMessage;
                JSONObject jsonLocation;

                switch (whatType){
                    case TEST :
                        response = typeTest(msg);
                        responseHandler.sendMessage(response);
                        break;
                    case CONNECTION_STATUS:
                        response = Message.obtain();
                        response.what = Type.CONNECTION_STATUS.ordinal();
                        response.obj = serverSays;
                        responseHandler.sendMessage(response);
                        break;
                    case SEND_LOCATION:
                        //String locationString = "SEND_LOCATION:" + ((LatLng)msg.obj).latitude + "," + ((LatLng)msg.obj).longitude;
                        /*try{
                            if (printWriter == null){
                                printWriter = new PrintWriter(mySocket.getOutputStream(), true);
                            }
                            jsonMessage = new JSONObject();
                            jsonLocation = new JSONObject();
                            jsonMessage.put("type", "SEND_LOCATION");
                            jsonLocation.put("latitude", ((LatLng)msg.obj).latitude);
                            jsonLocation.put("longitude", ((LatLng)msg.obj).longitude);
                            jsonMessage.put("location", jsonLocation);
                            Log.d("TEST", jsonMessage.toString(4));
                            //printWriter.println(locationString);
                            printWriter.println(jsonMessage.toString());
                            serverResponse = bufferedReader.readLine();
                            response = Message.obtain();
                            response.what = Type.SEND_LOCATION.ordinal();
                            response.obj = serverResponse;
                            responseHandler.sendMessage(response);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }*/
                        try{
                            response = typeSendLocation(msg);
                            responseHandler.sendMessage(response);
                        }
                        catch (IOException e){
                            e.printStackTrace();
                        }
                        break;
                    case GET_LOCATIONS:
                        /*try{
                            if (bufferedReader == null){
                                bufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
                            }
                            if (printWriter == null){
                                printWriter = new PrintWriter(mySocket.getOutputStream(), true);
                            }
                            //printWriter.println("GET_LOCATIONS");
                            jsonMessage = new JSONObject();
                            //jsonLocation = new JSONObject();
                            jsonMessage.put("type", "GET_LOCATIONS");
                            printWriter.println(jsonMessage);

                            response = Message.obtain();
                            response.what = Type.GET_LOCATIONS.ordinal();
                            response.obj = bufferedReader.readLine();
                            responseHandler.sendMessage(response);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }*/
                        try{
                            response = typeGetLocations(msg);
                            responseHandler.sendMessage(response);
                        }
                        catch (IOException e){
                            e.printStackTrace();
                        }
                        break;
                    case DISCONNECT:
                        try{
                            if (printWriter == null){
                                printWriter = new PrintWriter(mySocket.getOutputStream(), true);
                            }
                            printWriter.println("QUIT");
                            mySocket.close();
                            response = Message.obtain();
                            response.what = Type.SEND_LOCATION.ordinal();
                            response.obj = "Disconnected!";
                            responseHandler.sendMessage(response);
                        }
                        catch (Exception e){
                            Log.d("QUIT", "exception");
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };

        try{
            InetAddress address = InetAddress.getByName("csclserver.hopto.org");
            mySocket = new Socket(address, 50001);
            //SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            //UncertifiedSSLSocketFactory socketFactory = new UncertifiedSSLSocketFactory();
            //mySocket = (SSLSocket) socketFactory.createSocket(address, 50001);

            mySocket.setSoTimeout(3000); // wait up to 3 seconds for response
            if (bufferedReader == null){
                bufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
            }
            if (printWriter == null){
                printWriter = new PrintWriter(mySocket.getOutputStream(), true);
            }

        }catch (Exception e){
            e.printStackTrace();

        }
        Looper.loop();
    }

    public enum Type{
        TEST, STRING_RESPONSE, CONNECT, CONNECTION_STATUS, IS_CONNECTED, SEND_LOCATION, GET_LOCATIONS, DISCONNECT
    }

    private Message typeTest(Message msg){
        Message response = Message.obtain();
        response.obj = "BAD BOYS";
        response.what = Type.STRING_RESPONSE.ordinal();
        return response;
    }

    private Message typeSendLocation(Message msg) throws IOException{
        Message response = null;
        try{
            if (printWriter == null){
                printWriter = new PrintWriter(mySocket.getOutputStream(), true);
            }
            if (bufferedReader == null){
                bufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
            }
            JSONObject jsonMessage = new JSONObject();
            JSONObject jsonLocation = new JSONObject();
            jsonMessage.put("type", "SEND_LOCATION");
            jsonLocation.put("latitude", ((LatLng)msg.obj).latitude);
            jsonLocation.put("longitude", ((LatLng)msg.obj).longitude);
            jsonMessage.put("location", jsonLocation);
            //printWriter.println(locationString);
            printWriter.println(jsonMessage.toString());
            String serverResponse = bufferedReader.readLine();
            response = Message.obtain();
            response.what = Type.SEND_LOCATION.ordinal();
            response.obj = serverResponse;
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        finally {
            return response;
        }

    }

    private Message typeGetLocations(Message msg) throws IOException{
        Message response = null;
        try{
            if (bufferedReader == null){
                bufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
            }
            if (printWriter == null){
                printWriter = new PrintWriter(mySocket.getOutputStream(), true);
            }
            //printWriter.println("GET_LOCATIONS");
            JSONObject jsonMessage = new JSONObject();
            //jsonLocation = new JSONObject();
            jsonMessage.put("type", "GET_LOCATIONS");
            printWriter.println(jsonMessage);

            response = Message.obtain();
            response.what = Type.GET_LOCATIONS.ordinal();
            response.obj = bufferedReader.readLine();
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        finally {
            return response;
        }
    }
}
