package com.example.christopher.myapplication;

import android.content.Context;
import android.graphics.Color;
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
 * Created by Christopher on 2/9/2017.
 */

public class FriendListAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<Person> friends;

    private LayoutInflater myInflater;
    private boolean mNotifyOnChange = true;

    public FriendListAdapter(Context cntxt, ArrayList<Person> frnds){
        super(cntxt, R.layout.friend_box);
        context = cntxt;
        myInflater = LayoutInflater.from(context);
        friends = frnds;
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getPosition(Object item) {
        return friends.indexOf(item);
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
                    convertView = myInflater.inflate(R.layout.friend_box, parent, false);
                    myHolder.nameBox = (TextView) convertView.findViewById(R.id.friendListNameView);
                    myHolder.areaBox = (TextView) convertView.findViewById(R.id.friendListAreaView);
                    myHolder.profilePicture = (ImageView) convertView.findViewById(R.id.friendListImageView);
                    myHolder.onlineStatus = (ImageView) convertView.findViewById(R.id.friendListOnlineIndicator);
                    break;
            }
            convertView.setTag(myHolder);
        }
        else{
            myHolder = (ViewHolder) convertView.getTag();
        }
        myHolder.nameBox.setText(friends.get(position).getName());
        myHolder.deviceID = friends.get(position).getDeviceID();
        myHolder.pos = position;
        myHolder.isOnline = friends.get(position).isOnline();
        myHolder.areaBox.setText(friends.get(position).getAreaName());
        if (myHolder.isOnline) {
            myHolder.onlineStatus.setColorFilter(Color.parseColor(Map.ONLINE_GREEN));
        }
        else{
            myHolder.onlineStatus.setColorFilter(Color.parseColor(Map.ONLINE_RED));
        }
        return convertView;
    }

    static class ViewHolder{
        ImageView profilePicture;
        TextView nameBox;
        TextView areaBox;
        ImageView onlineStatus;
        String deviceID;
        boolean isOnline;
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
        friends = people;
        notifyDataSetChanged();
    }
}
