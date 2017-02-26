package com.example.christopher.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by Christopher on 12/5/2016.
 * Code from http://stackoverflow.com/questions/14013833/populating-a-custom-listview-from-listarray
 */
public class RequestListAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<Person> pendingRequests;
    private Map mapActivity;

    private LayoutInflater myInflater;
    private boolean mNotifyOnChange = true;

    public RequestListAdapter(Context cntxt, ArrayList<Person> requests){
        super(cntxt, R.layout.friend_request_box);
        context = cntxt;
        pendingRequests = requests;
        myInflater = LayoutInflater.from(cntxt);
    }

    public RequestListAdapter(Context cntxt, ArrayList<Person> requests, Map map){
        super(cntxt, R.layout.friend_request_box);
        context = cntxt;
        pendingRequests = requests;
        myInflater = LayoutInflater.from(cntxt);
        mapActivity = map;
    }

    @Override
    public int getCount() {
        return pendingRequests.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return pendingRequests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getPosition(Object item) {
        return pendingRequests.indexOf(item);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder myHolder;
        int type = getItemViewType(position);
        if (convertView == null){
            myHolder = new ViewHolder();
            switch (type){
                case 1:
                    convertView = myInflater.inflate(R.layout.friend_request_box, parent, false);
                    myHolder.nameBox = (TextView) convertView.findViewById(R.id.friendRequestNameView);
                    myHolder.profilePicture = (ImageView) convertView.findViewById(R.id.friendRequestImageView);
                    myHolder.nameBox.setText(pendingRequests.get(position).getName());
                    myHolder.declineButton = (Button) convertView.findViewById(R.id.declineRequestButton);
                    myHolder.acceptButton = (Button) convertView.findViewById(R.id.acceptRequestButton);

                    myHolder.declineButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new FriendRequestResponseTask().execute(myHolder, false);
                        }
                    });

                    myHolder.acceptButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new FriendRequestResponseTask().execute(myHolder, true);
                        }
                    });
                    break;
            }
            convertView.setTag(myHolder);
        }
        else{
            myHolder = (ViewHolder) convertView.getTag();
        }
        myHolder.deviceID = pendingRequests.get(position).getDeviceID();
        myHolder.pos = position;
        return convertView;
    }

    private class FriendRequestResponseTask extends AsyncTask<Object, Integer, Boolean>{
        JSONObject response;
        ViewHolder myHolder;

        @Override
        protected Boolean doInBackground(Object... params) {
            Socket mySocket;
            myHolder = (ViewHolder) params[0];
            boolean accept = (Boolean) params[1];
            boolean isFriends = false;
            try{
                InetAddress address = InetAddress.getByName("csclserver.hopto.org");
                mySocket = new Socket();
                mySocket.setSoTimeout(ApplicationConstants.SERVER_TIMEOUT_MS);
                mySocket.connect(new InetSocketAddress(address, 50001),ApplicationConstants.SERVER_TIMEOUT_MS);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
                PrintWriter printWriter = new PrintWriter(mySocket.getOutputStream(), true);

                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("type", NetworkConstants.TYPE_RESPOND_REQUEST);
                jsonMessage.put("sessionid", mapActivity.sessionInfo.getString("sessionid", ""));
                jsonMessage.put("target", myHolder.deviceID);
                if (accept){
                    jsonMessage.put("response", "accept");
                }
                else{
                    jsonMessage.put("response", "decline");
                }
                printWriter.println(jsonMessage);
                String unparsed = bufferedReader.readLine();
                mySocket.close();

                response = new JSONObject(unparsed);
                isFriends = response.getBoolean("friends");
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
                return isFriends;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mapActivity.checkSessionResponse(response);

            pendingRequests.remove(myHolder.pos);
            notifyDataSetChanged();
        }
    }

    static class ViewHolder{
        ImageView profilePicture;
        TextView nameBox;
        String deviceID;
        Button declineButton;
        Button acceptButton;
        int pos;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mNotifyOnChange = true;
    }

    @Override
    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }

    public void refresh(ArrayList<Person> people){
        pendingRequests = people;
        notifyDataSetChanged();
    }



}
