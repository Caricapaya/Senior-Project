package com.example.christopher.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Christopher on 12/5/2016.
 * Code from http://stackoverflow.com/questions/14013833/populating-a-custom-listview-from-listarray
 */
public class RequestListAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<Person> pendingRequests;

    private LayoutInflater myInflater;
    private boolean mNotifyOnChange = true;

    public RequestListAdapter(Context cntxt, ArrayList<Person> requests){
        super(cntxt, R.layout.friend_request_box);
        context = cntxt;
        pendingRequests = requests;
        myInflater = LayoutInflater.from(cntxt);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder myHolder;
        int type = getItemViewType(position);
        if (convertView == null){
            myHolder = new ViewHolder();
            switch (type){
                case 1:
                    convertView = myInflater.inflate(R.layout.friend_request_box, parent, false);
                    myHolder.nameBox = (TextView) convertView.findViewById(R.id.friendRequestNameView);
                    myHolder.profilePicture = (ImageView) convertView.findViewById(R.id.friendRequestImageView);
                    break;
            }
            convertView.setTag(myHolder);
        }
        else{
            myHolder = (ViewHolder) convertView.getTag();
        }
        myHolder.nameBox.setText(pendingRequests.get(position).getName());
        myHolder.deviceID = pendingRequests.get(position).getDeviceID();
        myHolder.pos = position;
        return convertView;
    }

    static class ViewHolder{
        ImageView profilePicture;
        TextView nameBox;
        String deviceID;
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
