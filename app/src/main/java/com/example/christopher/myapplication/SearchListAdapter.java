package com.example.christopher.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import java.util.List;

/**
 * Created by Christopher on 1/29/2017.
 */

public class SearchListAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<Person> searchResult;
    private Map mapActivity;
    private SparseArray<Bitmap> imageCache;

    private LayoutInflater myInflater;
    private boolean mNotifyOnChange = true;

    public SearchListAdapter(Context cntxt, ArrayList<Person> srchRslt){
        super(cntxt, R.layout.friend_search_box);
        context = cntxt;
        myInflater = LayoutInflater.from(context);
        searchResult = srchRslt;
    }

    public SearchListAdapter(Context cntxt, ArrayList<Person> srchRslt, Map map, SparseArray<Bitmap> cache){
        super(cntxt, R.layout.friend_search_box);
        context = cntxt;
        myInflater = LayoutInflater.from(context);
        searchResult = srchRslt;
        mapActivity = map;
        imageCache = cache;
    }

    @Override
    public int getCount() {
        return searchResult.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return searchResult.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getPosition(Object item) {
        return searchResult.indexOf(item);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder myHolder;
        int type = getItemViewType(position);
        if (convertView == null){
            myHolder = new ViewHolder();
            switch (type){
                case 1:
                    convertView = myInflater.inflate(R.layout.friend_search_box, parent, false);
                    myHolder.nameBox = (TextView) convertView.findViewById(R.id.searchListNameView);
                    myHolder.profilePicture = (ImageView) convertView.findViewById(R.id.searchListImageView);
                    myHolder.addButton = (ImageButton) convertView.findViewById(R.id.searchListButton);
                    myHolder.addButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myHolder.addButton.setEnabled(false);
                            myHolder.addButton.setColorFilter(Color.GRAY);
                            //myHolder.addButton.refreshDrawableState();
                            new SendRequestTask().execute(myHolder);
                        }
                    });
                    break;
            }
            convertView.setTag(myHolder);
        }
        else{
            myHolder = (ViewHolder) convertView.getTag();
        }
        myHolder.nameBox.setText(searchResult.get(position).getName());
        myHolder.deviceID = searchResult.get(position).getDeviceID();
        myHolder.pos = position;

        Bitmap image = imageCache.get(Integer.parseInt(myHolder.deviceID), null);
        if (image != null){
            myHolder.profilePicture.setImageBitmap(image);
        }
        return convertView;
    }

    static class ViewHolder{
        ImageView profilePicture;
        TextView nameBox;
        String deviceID;
        ImageButton addButton;
        int pos;
    }

    private class SendRequestTask extends AsyncTask<ViewHolder, String, Integer>{
        JSONObject response;
        ViewHolder myHolder;
        @Override
        protected Integer doInBackground(ViewHolder... params) {
            myHolder = params[0];
            Socket mySocket;
            boolean serverGotRequest = false;
            int requestReqistered = 0; //0 if no answer server, 1 if new request registered, 2 if request already pending, 3 if already friends
            try{
                InetAddress address = InetAddress.getByName("csclserver.hopto.org");
                mySocket = new Socket();
                mySocket.setSoTimeout(ApplicationConstants.SERVER_TIMEOUT_MS);
                mySocket.connect(new InetSocketAddress(address, 50001),ApplicationConstants.SERVER_TIMEOUT_MS);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
                PrintWriter printWriter = new PrintWriter(mySocket.getOutputStream(), true);

                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("type", NetworkConstants.TYPE_SEND_REQUEST);
                jsonMessage.put("sessionid", mapActivity.sessionInfo.getString("sessionid", ""));
                jsonMessage.put("target", myHolder.deviceID);
                printWriter.println(jsonMessage);
                String unparsed = bufferedReader.readLine();
                mySocket.close();

                response = new JSONObject(unparsed);
                requestReqistered = response.getInt("request_registered");
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
                return requestReqistered;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            mapActivity.checkSessionResponse(response);
            switch (result){
                case 0: //No contact with server
                    myHolder.addButton.setEnabled(true);
                    myHolder.addButton.clearColorFilter();
                    break;
                case 1: //New friend request registered
                    Toast.makeText(context, "Friend request sent!", Toast.LENGTH_SHORT).show();
                    break;
                case 2: //Friend request pending
                    Toast.makeText(context, "Friend request is already pending...", Toast.LENGTH_LONG).show();
                    break;
                case 3: //Already friends
                    Toast.makeText(context, "You two are already friends...", Toast.LENGTH_LONG).show();
                    break;

            }
            /*if (result){
                searchResult.remove(myHolder.pos);
                notifyDataSetChanged();
            }
            else{
                myHolder.addButton.setEnabled(true);
                myHolder.addButton.clearColorFilter();
            }*/
        }
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
        searchResult = people;
        notifyDataSetChanged();
    }
}
